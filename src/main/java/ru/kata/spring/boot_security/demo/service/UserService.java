package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService extends UserDetailsService {

    boolean saveUser(String name, String email, byte age, String password);

    void removeUserById(long id);

    List<User> getAllUsers();

    boolean updateUser(User user);

    User getUserById(Long id);

    User getUserByEmail(String email);
}
