package com.example.Internship_project.controller;

import com.example.Internship_project.dto.SigninUserRequest;
import com.example.Internship_project.dto.SignupUserRequest;
import com.example.Internship_project.jwt.JwtCore;
import com.example.Internship_project.model.User;
import com.example.Internship_project.repository.UserRepository;
import com.example.Internship_project.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class SecurityController {

    private UserRepository userRepository;
    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtCore jwtCore;

    public SecurityController(UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtCore jwtCore) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtCore = jwtCore;
    }

    @PostMapping("/signin") //Авторизация
    public ResponseEntity<?> signin(@RequestBody SigninUserRequest userRequest) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);
        return ResponseEntity.ok(jwt);
    }


    @PostMapping("/signup") //Регистрация
    public ResponseEntity<?> signup(@RequestBody SignupUserRequest userRequest) {
        if(userRepository.existsByUsername(userRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Такой user есть");
        }
        if(userRepository.existsByEmail(userRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Такой user с почтой");
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));  // Хэшируем пароль перед сохранением
        userRepository.save(user);

        return ResponseEntity.ok("Аккаунт создан");
    }

}
