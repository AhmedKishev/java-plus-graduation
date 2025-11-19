package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.request.NewUserRequestDto;
import ru.practicum.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    // MODIFY OPS

    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(
            @RequestBody @Valid NewUserRequestDto newUserRequestDto
    ) {
        return userService.create(newUserRequestDto);
    }

    @DeleteMapping("/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @PathVariable @Positive(message = "User Id not valid") Long userId
    ) {
        userService.delete(userId);
    }

    // GET COLLECTION

    @GetMapping("/admin/users")
    public Collection<UserDto> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return userService.findByIdListWithOffsetAndLimit(ids, from, size);
    }

    @GetMapping("/admin/user/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        return userService.existUserById(userId);
    }

    @GetMapping("/admin/user/set/ids")
    public Map<Long, UserDto> findAllBySetIds(@RequestParam Set<Long> ids) {
        return userService.getAllBySetIds(ids);
    }

    @GetMapping("/admin/user/short/{userId}")
    public UserShortDto findByIdShort(@PathVariable Long userId) {
        return userService.findByIdShort(userId);
    }

    @GetMapping("/admin/user/short/list")
    public Map<Long, UserShortDto> findAllByIdsShort(@RequestParam List<Long> ids) {
        return userService.findAllByIdsShort(ids);
    }



}
