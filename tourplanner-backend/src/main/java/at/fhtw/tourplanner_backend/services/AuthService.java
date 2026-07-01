package at.fhtw.tourplanner_backend.services;

import at.fhtw.tourplanner_backend.dto.auth.LoginRequestDto;
import at.fhtw.tourplanner_backend.dto.auth.LoginResponseDto;
import at.fhtw.tourplanner_backend.entities.User;
import at.fhtw.tourplanner_backend.repositories.UserRepository;
import at.fhtw.tourplanner_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Failed login attempt for unknown username '{}'.", request.getUsername());
                    return new RuntimeException("Invalid credentials");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Failed login attempt for username '{}'.", request.getUsername());
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        log.info("User '{}' logged in successfully.", user.getUsername());

        return new LoginResponseDto(token);
    }
}