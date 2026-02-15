package com.noobsmoke.springsecurity2026.controller;

import com.noobsmoke.springsecurity2026.dto.LoginUserDTO;
import com.noobsmoke.springsecurity2026.dto.RegisterUserDTO;
import com.noobsmoke.springsecurity2026.dto.VerifyUserDTO;
import com.noobsmoke.springsecurity2026.model.User;
import com.noobsmoke.springsecurity2026.responses.LoginResponse;
import com.noobsmoke.springsecurity2026.service.AuthenticationService;
import com.noobsmoke.springsecurity2026.service.JWTService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@AllArgsConstructor
public class AuthenticationController {

    private final JWTService jwtService;
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDTO registerUserDTO) {
        return ResponseEntity.ok(authenticationService.signUp(registerUserDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDTO loginUserDTO) {
        User authenticationUser = authenticationService.loginAuthentication(loginUserDTO);
        String jwtToken = jwtService.generateToken(authenticationUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getJwtExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDTO verifyUserDTO) {
        try {
            authenticationService.verifyUser(verifyUserDTO);
            return ResponseEntity.ok("Account Successfully Verified");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerification(email);
            return ResponseEntity.ok("Verification Code Sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
