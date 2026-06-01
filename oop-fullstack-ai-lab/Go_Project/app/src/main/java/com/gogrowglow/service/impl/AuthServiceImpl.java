package com.gogrowglow.service.impl;

import com.gogrowglow.dto.request.LoginRequest;
import com.gogrowglow.dto.request.RegisterRequest;
import com.gogrowglow.dto.response.AuthResponse;
import com.gogrowglow.dto.response.UserProfileResponse;
import com.gogrowglow.entity.User;
import com.gogrowglow.exception.BadRequestException;
import com.gogrowglow.repository.UserRepository;
import com.gogrowglow.security.JwtTokenProvider;
import com.gogrowglow.service.interfaces.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use.");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already in use.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        String token = tokenProvider.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getUsername());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid credentials.");
        }
        String token = tokenProvider.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getUsername());
    }

    @Override
    public UserProfileResponse currentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found."));
        return new UserProfileResponse(user.getId(), user.getEmail(), user.getUsername());
    }
}
