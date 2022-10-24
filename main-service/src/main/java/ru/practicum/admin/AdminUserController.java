package ru.practicum.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.UserService;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody NewUserDto newUserDto) {
        return userService.addNewUser(newUserDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsersByAdmin(
            @RequestParam(required = false) Long[] ids,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return userService.getUsersByAdmin(ids, from, size);
    }

}
