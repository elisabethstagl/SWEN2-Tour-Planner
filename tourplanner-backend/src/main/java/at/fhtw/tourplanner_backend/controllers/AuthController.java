package at.fhtw.tourplanner_backend.controllers;

import at.fhtw.tourplanner_backend.dto.auth.LoginRequestDto;
import at.fhtw.tourplanner_backend.dto.auth.LoginResponseDto;
import at.fhtw.tourplanner_backend.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }
}