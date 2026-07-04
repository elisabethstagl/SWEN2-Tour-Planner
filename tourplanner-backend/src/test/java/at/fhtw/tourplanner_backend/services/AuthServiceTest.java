package at.fhtw.tourplanner_backend.services;

import at.fhtw.tourplanner_backend.dto.auth.LoginRequestDto;
import at.fhtw.tourplanner_backend.dto.auth.LoginResponseDto;
import at.fhtw.tourplanner_backend.entities.User;
import at.fhtw.tourplanner_backend.exceptions.InvalidCredentialsException;
import at.fhtw.tourplanner_backend.repositories.UserRepository;
import at.fhtw.tourplanner_backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_returnsToken_whenCredentialsAreValid() {
        User user = new User();
        user.setUsername("bob");
        user.setPassword("encodedPassword");

        LoginRequestDto request = loginRequest("bob", "plainPassword");
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        LoginResponseDto response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void login_throwsException_whenUsernameDoesNotExist() {
        LoginRequestDto request = loginRequest("ghost", "password");
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_throwsException_whenPasswordIsWrong() {
        User user = new User();
        user.setUsername("bob");
        user.setPassword("encodedPassword");

        LoginRequestDto request = loginRequest("bob", "wrongPassword");
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    private LoginRequestDto loginRequest(String username, String password) {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setUsername(username);
        dto.setPassword(password);
        return dto;
    }
}
