package com.chenhy.service.mapper;

import com.chenhy.domain.WeatherData;
import com.chenhy.service.dto.HeWeatherNowResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {})
public interface WeatherMapper {
    @Mapping(source = "now.temp", target = "temperature")
    @Mapping(source = "now.windSpeed", target = "windSpeed")
    @Mapping(source = "now.wind360", target = "windDirection") // 修改为 wind360
    @Mapping(source = "now.obsTime", target = "observationTime")
    WeatherData toEntity(HeWeatherNowResponse response);

    HeWeatherNowResponse toDto(WeatherData entity);
}
