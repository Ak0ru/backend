package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WorldTimeResponse {
    private String timezone;

    @JsonProperty("datetime")
    private String dateTime;

    @JsonProperty("utc_offset")
    private String utcOffset;

    // Геттеры и сеттеры
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public String getUtcOffset() { return utcOffset; }
    public void setUtcOffset(String utcOffset) { this.utcOffset = utcOffset; }
}
