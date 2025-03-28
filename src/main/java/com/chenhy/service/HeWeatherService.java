package com.chenhy.service;

import com.chenhy.domain.WeatherData;
import com.chenhy.repository.WeatherDataRepository;
import com.chenhy.service.dto.HeWeatherNowResponse;
import com.chenhy.service.mapper.WeatherMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class HeWeatherService {

    private final Logger log = LoggerFactory.getLogger(HeWeatherService.class);

    private final WeatherDataRepository weatherDataRepository;
    private final WeatherMapper weatherMapper;
    private final RestTemplate restTemplate;

    @Value("${heweather.api.key}")
    private String apiKey;

    @Value("${heweather.api.url}")
    private String apiUrl;

    public HeWeatherService(WeatherDataRepository weatherDataRepository, WeatherMapper weatherMapper, RestTemplate restTemplate) {
        this.weatherDataRepository = weatherDataRepository;
        this.weatherMapper = weatherMapper;
        this.restTemplate = restTemplate;
    }

    public HeWeatherNowResponse fetchAndSaveWeather(String locationId) {
        log.debug("Fetching weather data for location: {}", locationId);

        // 构建API URL
        String url = String.format("%s?location=%s&key=%s", apiUrl, locationId, apiKey);

        // 调用和风天气API
        HeWeatherNowResponse response = restTemplate.getForObject(url, HeWeatherNowResponse.class);

        if (response != null && response.getNow() != null) {
            // 转换为实体并保存
            WeatherData weatherData = weatherMapper.toEntity(response);
            weatherData.setLocationId(locationId);
            weatherDataRepository.save(weatherData);

            log.debug("Saved weather data for location: {}", locationId);
        } else {
            log.warn("No weather data received for location: {}", locationId);
        }

        return response;
    }
}
