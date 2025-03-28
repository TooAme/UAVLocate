package com.chenhy.web.rest;

import com.chenhy.domain.WeatherData;
import com.chenhy.service.HeWeatherService;
import com.chenhy.service.dto.HeWeatherNowResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherResource {

    private final Logger log = LoggerFactory.getLogger(WeatherResource.class);

    private final HeWeatherService heWeatherService;

    public WeatherResource(HeWeatherService heWeatherService) {
        this.heWeatherService = heWeatherService;
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<HeWeatherNowResponse> getWeatherData(@PathVariable String locationId) {
        log.debug("REST request to get weather for location: {}", locationId);
        HeWeatherNowResponse result = heWeatherService.fetchAndSaveWeather(locationId);
        return ResponseEntity.ok(result);
    }
}
