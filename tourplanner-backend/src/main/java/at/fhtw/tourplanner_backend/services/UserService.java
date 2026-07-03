package at.fhtw.tourplanner_backend.services;

import at.fhtw.tourplanner_backend.dto.user.UserRequestDto;
import at.fhtw.tourplanner_backend.dto.user.UserResponseDto;
import at.fhtw.tourplanner_backend.entities.User;
import at.fhtw.tourplanner_backend.exceptions.DuplicateResourceException;
import at.fhtw.tourplanner_backend.exceptions.ResourceNotFoundException;
import at.fhtw.tourplanner_backend.mapper.UserMapper;
import at.fhtw.tourplanner_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto register(UserRequestDto dto) {

        if (userRepository.existsByUsername(dto.getUsername())) {
            log.warn("Registration failed: username '{}' already exists.", dto.getUsername());
            throw new DuplicateResourceException("Username already taken: " + dto.getUsername());
        }


        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("Registration failed: email '{}' already exists.", dto.getEmail());
            throw new DuplicateResourceException("Email already in use: " + dto.getEmail());
        }


        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        User user = UserMapper.toEntity(dto, encodedPassword);
        User savedUser = userRepository.save(user);

        log.info("New user '{}' registered.", user.getUsername());

        return UserMapper.toResponseDto(savedUser);
    }

    public UserResponseDto getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return UserMapper.toResponseDto(user);
    }

    public UserResponseDto getUserByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return UserMapper.toResponseDto(user);
    }
}