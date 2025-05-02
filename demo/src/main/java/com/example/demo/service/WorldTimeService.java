package com.example.demo.service;

import com.example.demo.dto.WorldTimeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WorldTimeService {

    private final RestTemplate restTemplate;

    @Autowired
    public WorldTimeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WorldTimeResponse getTimeForZone(String timezone) {
        String url = "http://worldtimeapi.org/api/timezone/" + timezone;
        return restTemplate.getForObject(url, WorldTimeResponse.class);
    }
}
