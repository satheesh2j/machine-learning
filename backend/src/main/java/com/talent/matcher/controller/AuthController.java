package com.talent.matcher.controller;

import com.talent.matcher.dto.LoginRequest;
import com.talent.matcher.dto.LoginResponse;
import com.talent.matcher.dto.PasswordChangeRequest;
import com.talent.matcher.service.JwtService;
import com.talent.matcher.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        String token = jwtService.generateToken(request.username());
        return ResponseEntity.ok(new LoginResponse(token, userService.requirePasswordChange(request.username())));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid PasswordChangeRequest request,
                                               Authentication authentication) {
        userService.changePassword(authentication.getName(), request.newPassword());
        return ResponseEntity.ok().build();
    }
}
