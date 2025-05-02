package com.example.demo.controller;

import com.example.demo.dto.WorldTimeResponse;
import com.example.demo.service.WorldTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/worldtime")
public class WorldTimeController {

    private final WorldTimeService worldTimeService;

    @Autowired
    public WorldTimeController(WorldTimeService worldTimeService) {
        this.worldTimeService = worldTimeService;
    }

    @GetMapping("/{timezone}")
    public WorldTimeResponse getTimeByTimezone(@PathVariable String timezone) {
        return worldTimeService.getTimeForZone(timezone);
    }
}
