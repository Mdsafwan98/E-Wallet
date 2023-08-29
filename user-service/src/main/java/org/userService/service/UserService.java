package org.userService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.userService.utils.ValidationException;
import org.userService.model.User;
import org.userService.repository.UserRepository;

import java.util.Optional;

/**
 * This class is used as a service for User API.
 *
 * @author safwanmohammed907@gmal.com
 */
@Service
public class UserService {
    public static final String USER_CREATED_TOPIC = "user-created";
    public static final String USER_UPDATED_TOPIC = "user-updated";
    @Autowired
    UserRepository userRepository;
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    // ObjectMapper is used to convert between Java objects and JSON representation, providing serialization and deserialization capabilities.
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Method is for creating onboarded user and should listen to messages from the USER_CREATED_TOPIC Kafka topic.
     *
     * @param user
     * @return
     * @throws JsonProcessingException
     * @throws ValidationException
     */
    public boolean createUserDetails(User user) throws JsonProcessingException, ValidationException {
        User existingPhoneNum = userRepository.findByPhone(user.getPhone());
        if (existingPhoneNum != null) {
            throw new ValidationException("Phone number already exist.");
        }
        User existingEmail = userRepository.findByEmail(user.getEmail());
        if (existingEmail != null) {
            throw new ValidationException("Email id already exist.");
        }
        userRepository.save(user);
        //Following fields needed to listen to Wallet Service for creating wallet for onboarded user
        JSONObject userObject = new JSONObject();
        userObject.put("phone", user.getPhone());
        userObject.put("email", user.getEmail());
        kafkaTemplate.send(USER_CREATED_TOPIC, this.objectMapper.writeValueAsString(userObject));
        return true;
    }

    /**
     * Method to get user details by userId from database.
     *
     * @param userId
     * @return
     * @throws ValidationException
     */
    public User getUserDetails(int userId) throws ValidationException {
        User userDetails = userRepository.findById(userId).orElse(null);
        if (userDetails == null) {
            throw new ValidationException("The user details does not exist for the requested id.");
        }
        return userDetails;
    }

    /**
     * Method to get user details by phone number from database.
     *
     * @param phone
     * @return
     * @throws ValidationException
     */
    public User getByPhone(String phone) throws ValidationException {
        User userDetails = userRepository.findByPhone(phone);
        if (userDetails == null) {
            throw new ValidationException("The user details does not exist for the requested phone number.");
        }
        return userDetails;
    }

    /**
     * Method to update user details in database and should listen to messages from the USER_UPDATED_TOPIC Kafka topic.
     *
     * @param user
     * @return
     * @throws ValidationException
     * @throws JsonProcessingException
     */
    public User updateUserDetails(User user) throws ValidationException, JsonProcessingException {
        Optional<User> optionalUser = userRepository.findById(user.getId());
        if (optionalUser.isPresent()) {
            //Fetch user details to update
            User userDetails = optionalUser.get();
            if (user.getName() != null && !user.getName().isBlank()) {
                userDetails.setName(user.getName());
            }
            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                userDetails.setEmail(user.getEmail());
            }
            if (user.getPhone() != null && !user.getPhone().isBlank()) {
                userDetails.setPhone(user.getPhone());
            }
            if (user.getAge() != null) {
                userDetails.setAge(user.getAge());
            }
            //Following fields needed to listen to Wallet Service for updating wallet for existing user
            JSONObject userObject = new JSONObject();
            userObject.put("phone", user.getPhone());
            userObject.put("email", user.getEmail());
            userObject.put("id", user.getId());
            kafkaTemplate.send(USER_UPDATED_TOPIC, this.objectMapper.writeValueAsString(userObject));
            return userRepository.save(userDetails);
        } else {
            throw new ValidationException("User with id " + user.getId() + " not found.");
        }
    }

    /**
     * Method to delete existing user details from database.
     *
     * @param id
     * @return
     * @throws ValidationException
     */
    public boolean deleteUserDetails(Integer id) throws ValidationException {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new ValidationException("Invalid user id.");
        }
        return true;
    }
}
