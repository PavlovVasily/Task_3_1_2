package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;

@Controller
public class AdminController {
    @Autowired
    UserService userService;

    @GetMapping("/admin")
    public String showAll(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/all";
    }

    @GetMapping("/admin/new")
    public String newUser(@ModelAttribute("user") User user) {
        return "admin/new";
    }

    @PostMapping("/admin/new")
    public String create(@ModelAttribute("user") @Valid User user,
                         BindingResult bindingResult /*Всегда идет после аргумента с анотацией валид. Сюда пишутся ошибки валидности*/,
                         Model model) {
        if (bindingResult.hasErrors()) {
            System.out.println("Error POST");
            return "admin/new";
        }

        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            System.out.println("Пароли не совпадают");
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "admin/new";
        }

        if (!userService.saveUser(user.getName(), user.getEmail(), (byte) user.getAge(), user.getPassword())) {
            model.addAttribute("emailError", "Пользователь с такой почтой уже существует");
            return "admin/new";
        }

        return "redirect:/admin";
    }

    @GetMapping("/admin/{id}/edit")
    public String edit(@PathVariable("id") long id, Model model) {
        model.addAttribute(userService.getUserById(id));
        return "admin/edit";
    }

    @PatchMapping("/admin/{id}")
    public String update(@ModelAttribute("user") @Valid User user,
                         BindingResult bindingResult,
                         @PathVariable("id") long id,
                         Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/edit";
        }

        User editedUser = userService.getUserById(user.getId());
        user.setPassword(editedUser.getPassword());
        user.setRoles(editedUser.getRoles());

        if (!userService.updateUser(user)) {
            model.addAttribute("emailError", "Пользователь с такой почтой уже существует");
            return "admin/edit";
        }

        return "redirect:/admin";
    }


    @DeleteMapping(value = "/admin/{id}")
    public String remove(@PathVariable("id") long id) {
        userService.removeUserById(id);
        return "redirect:/admin";
    }
}
