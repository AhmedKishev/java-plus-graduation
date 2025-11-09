package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserShortDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FeignClient(name = "user-service")
public interface UserClient {


    @GetMapping("/admin/user/{userId}")
    public UserDto findById(@PathVariable Long userId);

    @GetMapping("/admin/user/set/ids")
    public Map<Long, UserDto> findAllByIds(@RequestParam Set<Long> ids);

    @GetMapping("/admin/user/short/{userId}")
    public UserShortDto findByIdShort(@PathVariable Long userId);

    @GetMapping("/admin/user/short/list")
    public Map<Long, UserShortDto> findAllByIdsShort(@RequestParam List<Long> ids);

}