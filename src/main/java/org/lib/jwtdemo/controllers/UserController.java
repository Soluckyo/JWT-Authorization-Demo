package org.lib.jwtdemo.controllers;


import lombok.RequiredArgsConstructor;
import org.lib.jwtdemo.dto.UserDTO;
import org.lib.jwtdemo.entity.Role;
import org.lib.jwtdemo.entity.User;
import org.lib.jwtdemo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/get_all")
    public ResponseEntity<List<User>> getAll() {
        List<User> allUs = userService.getAll();
        return ResponseEntity.ok(allUs);
    }

    @PostMapping("/add_user")
    public ResponseEntity<String> addUser(@RequestBody UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());

        userService.addUser(user);
        return ResponseEntity.ok("User added successfully");
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/only_user")
    public ResponseEntity<String> hasUser() {
        return ResponseEntity.status(HttpStatus.OK).body("Method only for ROLE_USER");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/only_admin")
    public ResponseEntity<String> hasAdmin() {
        return ResponseEntity.status(HttpStatus.OK).body("Method only for ROLE_ADMIN");
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/set_role/{userId}")
    public ResponseEntity<String> setRole(@RequestBody Role role, @PathVariable Long userId) {
        userService.setRole(userId, role);
        return ResponseEntity.ok("Role set successfully");
    }

}
