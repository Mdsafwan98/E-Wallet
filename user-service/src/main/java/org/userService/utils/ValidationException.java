package org.userService.utils;

/**
 * This is a custom exception class that is used to represent validation-related exceptions in the application.
 *
 * @author safwanmohammed907@gmal.com
 */
public class ValidationException extends Throwable {

    public ValidationException(String message) {
        super(message);
    }


}