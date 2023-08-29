package org.userService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.userService.model.User;

/**
 * This class is used as a repository for User API.
 *
 * @author safwanmohammed907@gmal.com
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByPhone(String phone);

    User findByEmail(String email);
}
