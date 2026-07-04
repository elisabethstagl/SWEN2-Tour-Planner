package at.fhtw.tourplanner_backend.services;

import at.fhtw.tourplanner_backend.dto.user.UserRequestDto;
import at.fhtw.tourplanner_backend.dto.user.UserResponseDto;
import at.fhtw.tourplanner_backend.entities.User;
import at.fhtw.tourplanner_backend.exceptions.DuplicateResourceException;
import at.fhtw.tourplanner_backend.exceptions.ResourceNotFoundException;
import at.fhtw.tourplanner_backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void register_savesNewUser_whenUsernameAndEmailAreUnique() {
        UserRequestDto dto = new UserRequestDto("bob", "bob@example.com", "plainPassword");
        when(userRepository.existsByUsername(dto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("bob");
        savedUser.setEmail("bob@example.com");
        savedUser.setPassword("encodedPassword");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDto response = userService.register(dto);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("bob");
        assertThat(response.getEmail()).isEqualTo("bob@example.com");
    }

    @Test
    void register_storesEncodedPassword_neverPlainText() {
        UserRequestDto dto = new UserRequestDto("bob", "bob@example.com", "plainPassword");
        when(userRepository.existsByUsername(dto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("bob");
        savedUser.setEmail("bob@example.com");
        savedUser.setPassword("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        userService.register(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("encodedPassword");

        verify(passwordEncoder).encode("plainPassword");
    }

    @Test
    void register_throwsDuplicateResourceException_whenUsernameAlreadyTaken() {
        UserRequestDto dto = new UserRequestDto("bob", "bob@example.com", "plainPassword");
        when(userRepository.existsByUsername("bob")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.register(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_throwsDuplicateResourceException_whenEmailAlreadyTaken() {
        UserRequestDto dto = new UserRequestDto("bob", "bob@example.com", "plainPassword");
        when(userRepository.existsByUsername("bob")).thenReturn(false);
        when(userRepository.existsByEmail("bob@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.register(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_returnsUser_whenExists() {
        User user = userWith(1L, "bob");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDto result = userService.getUserById(1L);

        assertThat(result.getUsername()).isEqualTo("bob");
    }

    @Test
    void getUserById_throwsResourceNotFound_whenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void getUserByUsername_returnsUser_whenExists() {
        User user = userWith(1L, "bob");
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));

        UserResponseDto result = userService.getUserByUsername("bob");

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getUserByUsername_throwsResourceNotFound_whenMissing() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUsername("ghost"));
    }

    private User userWith(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        return user;
    }
}
