package Videoclub.service;

import Videoclub.dto.AuthResponse;
import Videoclub.dto.LoginRequest;
import Videoclub.dto.RegisterRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
}