package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;


@Controller
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/user/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasAuthority('ROLE_ADMIN')")
    public String showUser(@PathVariable("id") long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute(user);
        return "user/showUser";
    }


}
