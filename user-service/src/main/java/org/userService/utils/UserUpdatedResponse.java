package org.userService.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.userService.model.User;

/**
 * This class is used to be designed for generating structured success responses after updating the details.
 *
 * @author safwanmohammed907@gmal.com
 */

public class UserUpdatedResponse {
    @JsonProperty("message")
    private String message;
    @JsonProperty("user")
    private User user;

    public UserUpdatedResponse(String message, User user) {
        this.message = message;
        this.user = user;
    }
}

