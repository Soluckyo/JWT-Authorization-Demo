package org.lib.jwtdemo.controllers;


import lombok.RequiredArgsConstructor;
import org.lib.jwtdemo.dto.JwtResponseDTO;
import org.lib.jwtdemo.dto.RefreshTokenDTO;
import org.lib.jwtdemo.dto.JwtRequestDTO;
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
    public ResponseEntity<JwtResponseDTO> signIn(@RequestBody JwtRequestDTO jwtRequestDTO){
        try{
            JwtResponseDTO jwtResponseDTO = userService.signIn(jwtRequestDTO);
            return ResponseEntity.ok(jwtResponseDTO);
        }catch (AuthenticationException e){
            throw new RuntimeException("Authentication failed" + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDTO> refresh(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        try{
            JwtResponseDTO jwtResponseDTO = userService.refreshToken(refreshTokenDTO);
            return ResponseEntity.ok(jwtResponseDTO);
        }catch (Exception e){
            throw new RuntimeException("failed" + e.getMessage());
        }
    }
}
