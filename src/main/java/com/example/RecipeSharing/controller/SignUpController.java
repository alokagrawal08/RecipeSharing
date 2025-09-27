package com.example.RecipeSharing.controller;

import com.example.RecipeSharing.model.Users;
import com.example.RecipeSharing.payloads.SignUpRequestDTO;
import com.example.RecipeSharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class SignUpController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequestDTO signUpRequestDTO){
          if(Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequestDTO.getEmail()))){
              return ResponseEntity.
                      badRequest().
                      body("Email Already Exists");
          }
          if(Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequestDTO.getUsername()))){
              return ResponseEntity.
                      badRequest().
                      body("Username Already exists, pls change the username");
          }
          Users users = new Users();
          String hashedPassword= passwordEncoder.encode(signUpRequestDTO.getPassword());
          users.setEmail(signUpRequestDTO.getEmail());
          users.setUsername(signUpRequestDTO.getUsername());
          users.setPassword(hashedPassword);
          users.setRole(Collections.singleton("USER"));
          userRepository.save(users);
          return ResponseEntity.
                  ok("User Registered Successfully");
    }
}
