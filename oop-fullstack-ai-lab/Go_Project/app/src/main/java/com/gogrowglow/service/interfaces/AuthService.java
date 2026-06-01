package com.gogrowglow.service.interfaces;

import com.gogrowglow.dto.request.LoginRequest;
import com.gogrowglow.dto.request.RegisterRequest;
import com.gogrowglow.dto.response.AuthResponse;
import com.gogrowglow.dto.response.UserProfileResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserProfileResponse currentUser(String email);
}
