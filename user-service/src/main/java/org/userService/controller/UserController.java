package org.userService.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.userService.utils.Util;
import org.userService.utils.InputValidation;
import org.userService.utils.UserUpdatedResponse;
import org.userService.utils.ValidationException;
import org.userService.dto.CreateUserRequest;
import org.userService.dto.GetUserResponse;
import org.userService.dto.UpdateUserRequest;
import org.userService.model.User;
import org.userService.service.UserService;

import javax.validation.Valid;

/**
 * This class is used as a controller for User API.
 *
 * @author safwanmohammed907@gmal.com
 */
@RestController
public class UserController {
    @Autowired
    UserService userService;
    private final InputValidation inputDetails;

    @Autowired
    public UserController(InputValidation inputDetails) {
        this.inputDetails = inputDetails;
    }

    /**
     * Method to create new user details.
     *
     * @param userRequest
     * @param bindingResult
     * @return
     * @throws JsonProcessingException
     * @throws ValidationException
     */
    @PostMapping("/user")
    public ResponseEntity<String> createUser(@RequestBody @Valid CreateUserRequest userRequest, BindingResult bindingResult) throws JsonProcessingException, ValidationException {
        inputDetails.validateInputDetails(bindingResult);
        boolean user = userService.createUserDetails(Util.convertCreateUserRequest(userRequest));
        if (user) {
            return ResponseEntity.ok("User is created successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to create user.");
        }
    }

    /**
     * Method to get user details by userId.
     *
     * @param userId
     * @return
     * @throws ValidationException
     */
    @GetMapping("/user/{userId}")
    public GetUserResponse getUser(@PathVariable(required = false) Integer userId) throws ValidationException {
        if (userId == null) {
            throw new ValidationException("User id is mandatory.");
        }
        User user = userService.getUserDetails(userId);
        return Util.convertToUserResponse(user);
    }

    /**
     * Method to get user details by phone number.
     *
     * @param phone
     * @return
     * @throws ValidationException
     */
    @GetMapping("/user/phone/{phone}")
    public GetUserResponse getUserByPhone(@PathVariable(required = false) String phone) throws ValidationException {
        if (phone.isBlank()) {
            throw new ValidationException("Phone number is mandatory.");
        }
        User user = userService.getByPhone(phone);
        return Util.convertToUserResponse(user);
    }

    /**
     * Method to update existing user details.
     *
     * @param updateUserRequest
     * @param bindingResult
     * @return
     * @throws ValidationException
     * @throws JsonProcessingException
     */
    @PutMapping("/user")
    public ResponseEntity<UserUpdatedResponse> updateUser(@RequestBody @Valid UpdateUserRequest updateUserRequest, BindingResult bindingResult) throws ValidationException, JsonProcessingException {
        inputDetails.validateInputDetails(bindingResult);
        User updatedUser = userService.updateUserDetails(Util.convertUpdateUserRequest(updateUserRequest));
        if (updatedUser != null) {
            UserUpdatedResponse userResponse = new UserUpdatedResponse("User details updated successfully.", updatedUser);
            return ResponseEntity.ok().body(userResponse);
        } else {
            return ResponseEntity.badRequest().body(new UserUpdatedResponse("Failed to update user.", null));
        }
    }

    /**
     * Method to delete existing user details by userId.
     *
     * @param id
     * @return
     * @throws ValidationException
     */
    @DeleteMapping(("/user"))
    public ResponseEntity<String> deleteUser(@RequestParam(required = false) Integer id) throws ValidationException {
        if (id == null) {
            throw new ValidationException("User id is mandatory.");
        }
        boolean deleteUser = userService.deleteUserDetails(id);
        if (deleteUser) {
            return ResponseEntity.ok("User is deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to delete user.");
        }
    }
}
