package com.noobsmoke.springsecurity2026.service;

import com.noobsmoke.springsecurity2026.dto.LoginUserDTO;
import com.noobsmoke.springsecurity2026.dto.RegisterUserDTO;
import com.noobsmoke.springsecurity2026.dto.VerifyUserDTO;
import com.noobsmoke.springsecurity2026.model.User;
import com.noobsmoke.springsecurity2026.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public User signUp(RegisterUserDTO registerUserDTO) {
       User newUser = new User(registerUserDTO.getUsername(), registerUserDTO.getPassword(), registerUserDTO.getEmail());
       newUser.setVerificationCode(generateVerificationCode());
       newUser.setVerificationExpirationAt(LocalDateTime.now().plusMinutes(15));
       newUser.setEnabled(true);
       sendVerificationEmail(newUser);
       return userRepository.save(newUser);
    }

    public User loginAuthentication(LoginUserDTO loginUserDTO) {
        User loggedInUser = userRepository.findByUsername(loginUserDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        if (!loggedInUser.isEnabled()) throw new RuntimeException("Account Not Verified");

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDTO.getUsername(),
                        loginUserDTO.getPassword()
                )
        );
        return loggedInUser;
    }

    public void verifyUser(VerifyUserDTO verifyUserDTO) {
        User optionalUser = userRepository.findByUsername(verifyUserDTO.getUsername()).orElse(null);
        if (optionalUser == null) throw new RuntimeException("User Not Found");
        if (optionalUser.getVerificationExpirationAt().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Verification code has expired");
        if (optionalUser.getVerificationCode().equals(verifyUserDTO.getVerificationCode())) {
            optionalUser.setEnabled(true);
            optionalUser.setVerificationCode(null);
            optionalUser.setVerificationExpirationAt(null);
            userRepository.save(optionalUser);
        }
    }

    public void resendVerification(String email) {
        User optionalUser = userRepository.findByEmail(email).orElse(null);
        if (optionalUser == null) throw new RuntimeException("User Not Found");
        if (optionalUser.isEnabled()) throw new RuntimeException("Account Is Already Verified");
        optionalUser.setVerificationCode(generateVerificationCode());
        optionalUser.setVerificationExpirationAt(LocalDateTime.now().plusMinutes(60));
        sendVerificationEmail(optionalUser);
        userRepository.save(optionalUser);
    }

    public void sendVerificationEmail(User user) {
        String subject = "Account Verification (" + user.getUsername() + ")";
        String verificationCode = user.getVerificationCode();
        String htmlMessage =
                "<!doctype html>"
                        + "<html lang=\"en\">"
                        + "<head><meta charset=\"UTF-8\" /><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\" /></head>"
                        + "<body style=\"margin:0;padding:0;background-color:#f4f6f8;\">"
                        + "<div style=\"display:none;max-height:0;overflow:hidden;opacity:0;color:transparent;\">"
                        + "Your verification code is " + verificationCode + ". It expires in 10 minutes."
                        + "</div>"
                        + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#f4f6f8;\">"
                        + "<tr><td align=\"center\" style=\"padding:24px 12px;\">"
                        + "<table role=\"presentation\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:600px;max-width:600px;background:#ffffff;border-radius:14px;overflow:hidden;\">"
                        + "<tr><td style=\"padding:22px 24px;background:#0b5fff;\">"
                        + "<div style=\"font-family:Arial,Helvetica,sans-serif;font-size:18px;line-height:1.2;color:#ffffff;font-weight:700;\">YourApp</div>"
                        + "<div style=\"font-family:Arial,Helvetica,sans-serif;font-size:13px;line-height:1.4;color:#dbe7ff;margin-top:6px;\">Email verification</div>"
                        + "</td></tr>"
                        + "<tr><td style=\"padding:28px 24px 10px 24px;\">"
                        + "<div style=\"font-family:Arial,Helvetica,sans-serif;font-size:16px;line-height:1.5;color:#111827;font-weight:700;\">Verify your email address</div>"
                        + "<div style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;line-height:1.6;color:#374151;margin-top:10px;\">Enter the code below to continue.</div>"
                        + "<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin:18px 0 6px 0;\"><tr><td style=\"background:#f3f4f6;border:1px solid #e5e7eb;border-radius:12px;padding:14px 18px;\">"
                        + "<div style=\"font-family:Arial,Helvetica,sans-serif;font-size:28px;letter-spacing:6px;color:#111827;font-weight:800;text-align:center;\">"
                        + verificationCode
                        + "</div></td></tr></table>"
                        + "<div style=\"font-family:Arial,Helvetica,sans-serif;font-size:12px;line-height:1.6;color:#6b7280;margin-top:6px;\">If you didn’t request this, ignore this email.</div>"
                        + "</td></tr>"
                        + "</table></td></tr></table>"
                        + "</body></html>";
        try {
            System.out.println(user.getEmail());
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Email Error");
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(90000) + 10000;
        return String.valueOf(code);
    }
}
