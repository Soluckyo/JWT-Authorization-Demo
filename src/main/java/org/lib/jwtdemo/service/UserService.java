package org.lib.jwtdemo.service;

import lombok.RequiredArgsConstructor;
import org.lib.jwtdemo.entity.User;
import org.lib.jwtdemo.repo.UserRepo;
import org.lib.jwtdemo.security.JwtAuthenticationDTO;
import org.lib.jwtdemo.security.JwtService;
import org.lib.jwtdemo.security.RefreshTokenDTO;
import org.lib.jwtdemo.security.UserCredentialsDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAll() {
        return userRepo.findAll();
    }

    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public JwtAuthenticationDTO signIn(UserCredentialsDTO userCredentialsDTO) throws AuthenticationException {
        User user = findByCredentials(userCredentialsDTO);
        return jwtService.generateAuthToken(user.getEmail());
    }

    public JwtAuthenticationDTO refreshToken(RefreshTokenDTO refreshTokenDTO) throws Exception {
        String refreshToken = refreshTokenDTO.getRefreshToken();
        if(refreshToken == null && jwtService.validateJwtToken(refreshToken)){
            User user = findByEmail(jwtService.getEmailFromToken(refreshToken));
            return jwtService.refreshBaseToken(user.getEmail(), refreshToken);
        }throw new AuthenticationException("Invalid refresh token");
    }

    private User findByCredentials(UserCredentialsDTO userCredentialsDTO) throws AuthenticationException {
        Optional<User> userOptional = userRepo.findByEmail(userCredentialsDTO.getEmail());
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            if(passwordEncoder.matches(userCredentialsDTO.getPassword(), user.getPassword())) {
                return user;
            }
        } throw new AuthenticationException("Email or password is not correct");
    }

    private User findByEmail(String email) throws Exception {
        return userRepo.findByEmail(email).orElseThrow(()-> new Exception(String.format("Email %s not found", email)));
    }
}
