package org.lib.jwtdemo.security;

import lombok.RequiredArgsConstructor;
import org.lib.jwtdemo.repo.UserRepo;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserService implements UserDetailsService {
    private final UserRepo userRepo;

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email).map(CustomUserDetails::new).orElseThrow(
                () -> new UsernameNotFoundException(String.format("Пользователь '%s' не найден", email)));

    }
}
