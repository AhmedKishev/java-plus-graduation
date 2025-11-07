package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "request-service")
public interface RequestClient {

    @GetMapping("admin/confirmed/{eventId}")
    public Long countByEventIdAndStatus(@PathVariable Long eventId);


    @GetMapping("/admin/confirmed")
    public Map<Long, Long> getConfirmedRequestsByEventIds(@RequestParam List<Long> ids);

}
