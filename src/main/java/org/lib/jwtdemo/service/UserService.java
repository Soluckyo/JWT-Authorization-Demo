package org.lib.jwtdemo.service;

import lombok.RequiredArgsConstructor;
import org.lib.jwtdemo.entity.Role;
import org.lib.jwtdemo.entity.User;
import org.lib.jwtdemo.repo.UserRepo;
import org.lib.jwtdemo.dto.JwtResponseDTO;
import org.lib.jwtdemo.security.JwtUtils;
import org.lib.jwtdemo.dto.RefreshTokenDTO;
import org.lib.jwtdemo.dto.JwtRequestDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAll() {
        return userRepo.findAll();
    }

    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        return userRepo.save(user);
    }

    public JwtResponseDTO signIn(JwtRequestDTO jwtRequestDTO) throws AuthenticationException {
        User user = findByCredentials(jwtRequestDTO);
        return jwtUtils.generateAuthToken(user);
    }

    public JwtResponseDTO refreshToken(RefreshTokenDTO refreshTokenDTO) throws Exception {
        String refreshToken = refreshTokenDTO.getRefreshToken();
        if(refreshToken == null && jwtUtils.validateJwtToken(refreshToken)){
            User user = findByEmail(jwtUtils.getEmailFromToken(refreshToken));
            return jwtUtils.refreshBaseToken(user, refreshToken);
        }throw new AuthenticationException("Invalid refresh token");
    }

    private User findByCredentials(JwtRequestDTO jwtRequestDTO) throws AuthenticationException {
        Optional<User> userOptional = userRepo.findByEmail(jwtRequestDTO.getEmail());
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            if(passwordEncoder.matches(jwtRequestDTO.getPassword(), user.getPassword())) {
                return user;
            }
        } throw new AuthenticationException("Email or password is not correct");
    }

    private User findByEmail(String email) throws Exception {
        return userRepo.findByEmail(email).orElseThrow(()-> new Exception(String.format("Email %s not found", email)));
    }

    public void setRole(Long userId, Role role){
        userRepo.findById(userId).ifPresent(user -> user.setRole(role));
    }
}
