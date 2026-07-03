package at.fhtw.tourplanner_backend.mapper;

import at.fhtw.tourplanner_backend.dto.user.UserRequestDto;
import at.fhtw.tourplanner_backend.dto.user.UserResponseDto;
import at.fhtw.tourplanner_backend.entities.User;

public class UserMapper {

    // encodedPassword must already be hashed by the caller
    public static User toEntity(UserRequestDto dto, String encodedPassword) {
        User user = new User();

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(encodedPassword);

        return user;
    }

    public static UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(
            user.getId(),
            user.getUsername(),
            user.getEmail()
        );
    }
}