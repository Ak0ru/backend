// src/main/java/com/example/demo/dto/WorldTimeResponseDto.java
package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WorldTimeResponseDto {
    private String datetime;
    private String timezone;
    @JsonProperty("utc_offset")
    private String utcOffset;
    // + геттеры/сеттеры
}
