package org.lib.jwtdemo.controllers;


import lombok.RequiredArgsConstructor;
import org.lib.jwtdemo.security.JwtAuthenticationDTO;
import org.lib.jwtdemo.security.RefreshTokenDTO;
import org.lib.jwtdemo.security.UserCredentialsDTO;
import org.lib.jwtdemo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;


    @PostMapping("/sign_in")
    public ResponseEntity<JwtAuthenticationDTO> signIn(@RequestBody UserCredentialsDTO userCredentialsDTO){
        try{
            JwtAuthenticationDTO jwtAuthenticationDTO = userService.signIn(userCredentialsDTO);
            return ResponseEntity.ok(jwtAuthenticationDTO);
        }catch (AuthenticationException e){
            throw new RuntimeException("Authentication failed" + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationDTO> refresh(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        try{
            JwtAuthenticationDTO jwtAuthenticationDTO = userService.refreshToken(refreshTokenDTO);
            return ResponseEntity.ok(jwtAuthenticationDTO);
        }catch (Exception e){
            throw new RuntimeException("failed" + e.getMessage());
        }
    }
}
