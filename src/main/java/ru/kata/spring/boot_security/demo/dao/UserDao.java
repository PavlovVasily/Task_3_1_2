package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserDao {
    boolean saveUser(String name, String email, byte age, String password, String[] roleNames);

    void removeUserById(long id);

    List<User> getAllUsers();

    boolean updateUser(User user, String[] roleNames);

    User getUserById(Long id);

    User getUserByEmail(String email);

}
