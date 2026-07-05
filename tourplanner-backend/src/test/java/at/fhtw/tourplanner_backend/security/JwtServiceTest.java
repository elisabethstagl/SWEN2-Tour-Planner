package at.fhtw.tourplanner_backend.security;

import at.fhtw.tourplanner_backend.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // base64 test secret, only used in this test
        ReflectionTestUtils.setField(jwtService, "secret",
                "dGhpcy1pcy1hLXRlc3Qtc2VjcmV0LWtleS1mb3ItanVuaXQtb25seQ==");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3_600_000L);
    }

    @Test
    void generateToken_andExtractUsername_roundTrip() {
        User user = new User();
        user.setId(1L);
        user.setUsername("bob");

        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractUsername(token)).isEqualTo("bob");
    }

    @Test
    void isTokenValid_returnsTrue_forMatchingUsername() {
        User user = new User();
        user.setUsername("bob");
        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token, "bob")).isTrue();
    }

    @Test
    void isTokenValid_returnsFalse_forDifferentUsername() {
        User user = new User();
        user.setUsername("bob");
        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token, "someone-else")).isFalse();
    }
}
