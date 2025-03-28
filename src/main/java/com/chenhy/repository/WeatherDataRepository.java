package com.chenhy.repository;

import com.chenhy.domain.WeatherData;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    // 可以添加自定义查询方法
    WeatherData findFirstByLocationIdOrderByObservationTimeDesc(String locationId);
}
