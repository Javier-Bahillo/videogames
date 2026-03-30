package Videoclub.service.impl;

import Videoclub.dto.AuthResponse;
import Videoclub.dto.LoginRequest;
import Videoclub.dto.RegisterRequest;
import Videoclub.entity.User;
import Videoclub.repository.UserRepository;
import Videoclub.security.JwtUtil;
import Videoclub.service.AuthService;
import Videoclub.service.EmailService;
import Videoclub.service.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final LoginAttemptService loginAttemptService;

    @Override
    public AuthResponse login(LoginRequest request) {
        if (loginAttemptService.isBlocked(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Too many failed attempts. Try again in 15 minutes.");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            loginAttemptService.failure(request.getUsername());
            throw e;
        }
        loginAttemptService.success(request.getUsername());
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getRole());
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .role("USER")
                .status("ACTIVE")
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .createdBy("SYSTEM")
                .updateBy("SYSTEM")
                .build();

        userRepository.save(user);

        emailService.sendWelcomeEmail(user.getEmail(), user.getName(), user.getSurname(),
                user.getUsername(), user.getRole(), user.getCreatedAt());

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getRole());
    }
}