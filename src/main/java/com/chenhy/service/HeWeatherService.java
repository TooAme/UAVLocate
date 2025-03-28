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

    // 配置参数
    @Value("${heweather.api.key}")
    private String apiKey;

    @Value("${heweather.api.url}")
    private String apiUrl;

    public HeWeatherService(WeatherDataRepository weatherDataRepository, WeatherMapper weatherMapper, RestTemplate restTemplate) {
        this.weatherDataRepository = weatherDataRepository;
        this.weatherMapper = weatherMapper;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public HeWeatherNowResponse fetchAndSaveWeather(String locationId) {
        HeWeatherNowResponse response = fetchFromApi(locationId);
        WeatherData weatherData = weatherMapper.toEntity(response);
        weatherData.setLocationId(locationId);
        WeatherData savedData = weatherDataRepository.save(weatherData);
        return weatherMapper.toDto(savedData);
    }

    private HeWeatherNowResponse fetchFromApi(String locationId) {
        String url = String.format("%s?location=%s&key=%s", apiUrl, locationId, apiKey);
        return restTemplate.getForObject(url, HeWeatherNowResponse.class);
    }
}
