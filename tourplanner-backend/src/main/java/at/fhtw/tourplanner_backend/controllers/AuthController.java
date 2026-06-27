package at.fhtw.tourplanner_backend.controllers;

import at.fhtw.tourplanner_backend.dto.auth.LoginRequestDto;
import at.fhtw.tourplanner_backend.dto.auth.LoginResponseDto;
import at.fhtw.tourplanner_backend.entities.User;
import at.fhtw.tourplanner_backend.repositories.UserRepository;
import at.fhtw.tourplanner_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new LoginResponseDto(token));
    }
}
