package org.userService.dto;

import lombok.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * This class is used as a data transfer object for creating user.
 *
 * @author safwanmohammed907@gmal.com
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    @NotBlank(message = "User name is mandatory.")
    private String name;
    @NotBlank(message = "Phone number is mandatory.")
    private String phone;
    private String email;
    @Min(value = 18, message = "Age must be at least 18")
    private Integer age;


}