package com.example.demo.controller;

import com.example.demo.dto.WorldTimeResponseDto;
import com.example.demo.service.WorldTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/worldtime")
public class WorldTimeController {

    private final WorldTimeService svc;

    @Autowired
    public WorldTimeController(WorldTimeService svc) {
        this.svc = svc;
    }

    @GetMapping("/{area}/{location}")
    public ResponseEntity<WorldTimeResponseDto> getTime(
            @PathVariable String area,
            @PathVariable String location
    ) {
        String tz = area + "/" + location;
        return ResponseEntity.ok(svc.getTime(tz));
    }
}
