package at.fhtw.tourplanner_backend.mapper;

import at.fhtw.tourplanner_backend.dto.user.UserRequestDto;
import at.fhtw.tourplanner_backend.dto.user.UserResponseDto;
import at.fhtw.tourplanner_backend.entities.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void toEntity_mapsUsernameAndEmail_andStoresOnlyTheEncodedPasswordItWasGiven() {
        UserRequestDto dto = new UserRequestDto("bob", "bob@example.com", "plainPassword1");

        User user = UserMapper.toEntity(dto, "hashed-value");

        assertThat(user.getUsername()).isEqualTo("bob");
        assertThat(user.getEmail()).isEqualTo("bob@example.com");
        assertThat(user.getPassword()).isEqualTo("hashed-value");
    }

    @Test
    void toResponseDto_mapsIdUsernameAndEmail() {
        User user = new User();
        user.setId(1L);
        user.setUsername("bob");
        user.setEmail("bob@example.com");
        user.setPassword("hashed-value");

        UserResponseDto dto = UserMapper.toResponseDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUsername()).isEqualTo("bob");
        assertThat(dto.getEmail()).isEqualTo("bob@example.com");
    }
}
