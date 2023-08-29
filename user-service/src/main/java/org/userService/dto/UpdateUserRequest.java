package org.userService.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.*;

/**
 * This class is used as a data transfer object for updating user details.
 *
 * @author safwanmohammed907@gmal.com
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {
    @NotNull(message = "User id is mandatory.")
    private Integer id;
    @NotBlank(message = "User name is mandatory.")
    private String name;
    @NotBlank(message = "Phone number is mandatory.")
    private String phone;
    private String email;
    @Min(value = 18, message = "Age must be at least 18")
    private Integer age;

}