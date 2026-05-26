package com.billr.billr_backend.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.billr.billr_backend.auth.dto.AuthResponse;
import com.billr.billr_backend.auth.dto.LoginRequest;
import com.billr.billr_backend.auth.dto.RegisterRequest;
import com.billr.billr_backend.auth.model.Business;
import com.billr.billr_backend.auth.model.User;
import com.billr.billr_backend.auth.repository.BusinessRepository;
import com.billr.billr_backend.auth.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, BusinessRepository businessRepository, JwtService jwtService,
            AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        Business business = Business.builder()
                .user(user)
                .name(request.getBusinessName())
                .build();

        userRepository.save(user);
        businessRepository.save(business);

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .build();

    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .build();
    }
}
