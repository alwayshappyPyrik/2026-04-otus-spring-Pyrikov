package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.otus.hw.dto.UserCreateRequestDto;
import ru.otus.hw.dto.UserRequestDto;
import ru.otus.hw.dto.UserResponseDto;
import ru.otus.hw.dto.UserUpdateRequestDto;
import ru.otus.hw.services.UserService;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    @GetMapping("/{id}")
    public String findUserById(@PathVariable Long id, Model model) {
        UserRequestDto request = UserRequestDto.builder()
                .id(id)
                .build();
        model.addAttribute("user", userService.findById(request));
        return "users/detail";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", UserCreateRequestDto.builder().build());
        return "users/new";
    }

    @PostMapping
    public String createUser(@Valid @ModelAttribute("user") UserCreateRequestDto createRequest,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "users/new";
        }

        userService.insert(createRequest);
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        UserRequestDto request = UserRequestDto.builder()
                .id(id)
                .build();
        UserResponseDto userResponse = userService.findById(request);

        UserUpdateRequestDto userForm = UserUpdateRequestDto.builder()
                .id(userResponse.id())
                .login(userResponse.login())
                .email(userResponse.email())
                .enabled(userResponse.enabled())
                .build();

        model.addAttribute("user", userForm);
        return "users/edit";
    }

    @PutMapping("/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("user") UserUpdateRequestDto updateRequest,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "users/edit";
        }

        UserUpdateRequestDto request = updateRequest.toBuilder()
                .id(id)
                .build();

        UserResponseDto updatedUser = userService.update(request);
        return "redirect:/users/" + updatedUser.id();
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        UserRequestDto request = UserRequestDto.builder()
                .id(id)
                .build();
        userService.deleteById(request);
        return "redirect:/users";
    }
}
