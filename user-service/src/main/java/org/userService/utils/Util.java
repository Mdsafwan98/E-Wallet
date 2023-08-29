package org.userService.utils;

import org.userService.dto.CreateUserRequest;
import org.userService.dto.GetUserResponse;
import org.userService.dto.UpdateUserRequest;
import org.userService.model.User;

/**
 * This class is used to handle serialization.
 *
 * @author safwanmohammed907@gmal.com
 **/
public class Util {
    public static User convertCreateUserRequest(CreateUserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .age(request.getAge())
                .phone(request.getPhone())
                .build();
    }

    public static GetUserResponse convertToUserResponse(User user) {
        return GetUserResponse.builder()
                .name(user.getName())
                .phone(user.getPhone())
                .age(user.getAge())
                .email(user.getEmail())
                .updatedOn(user.getUpdatedOn())
                .createdOn(user.getCreatedOn())
                .build();
    }

    public static User convertUpdateUserRequest(UpdateUserRequest request) {
        return User.builder()
                .id(request.getId())
                .name(request.getName())
                .email(request.getEmail())
                .age(request.getAge())
                .phone(request.getPhone())
                .build();
    }
}
