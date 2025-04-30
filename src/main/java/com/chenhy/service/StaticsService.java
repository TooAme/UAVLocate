package com.chenhy.service;

import com.chenhy.domain.Statics;
import com.chenhy.domain.WeatherData;
import com.chenhy.repository.StaticsRepository;
import com.chenhy.repository.WeatherDataRepository;
import java.util.Optional;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.chenhy.domain.Statics}.
 */
@Service
@Transactional
public class StaticsService {

    private static final Logger LOG = LoggerFactory.getLogger(StaticsService.class);

    private final StaticsRepository staticsRepository;
    private final WeatherDataRepository weatherDataRepository;

    @Autowired
    public StaticsService(StaticsRepository staticsRepository, WeatherDataRepository weatherDataRepository) {
        this.staticsRepository = staticsRepository;
        this.weatherDataRepository = weatherDataRepository;
    }

    /**
     * Save a statics.
     *
     * @param statics the entity to save.
     * @return the persisted entity.
     */
    public Statics save(Statics statics) {
        LOG.debug("Request to save Statics : {}", statics);
        return staticsRepository.save(statics);
    }

    /**
     * Update a statics.
     *
     * @param statics the entity to save.
     * @return the persisted entity.
     */
    public Statics update(Statics statics) {
        LOG.debug("Request to update Statics : {}", statics);
        return staticsRepository.save(statics);
    }

    /**
     * Partially update a statics.
     *
     * @param statics the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Statics> partialUpdate(Statics statics) {
        LOG.debug("Request to partially update Statics : {}", statics);

        return staticsRepository
            .findById(statics.getId())
            .map(existingStatics -> {
                if (statics.getTime() != null) {
                    existingStatics.setTime(statics.getTime());
                }
                if (statics.getPosX() != null) {
                    existingStatics.setPosX(statics.getPosX());
                }
                if (statics.getPosY() != null) {
                    existingStatics.setPosY(statics.getPosY());
                }
                if (statics.getPosZ() != null) {
                    existingStatics.setPosZ(statics.getPosZ());
                }
                if (statics.getWindSpeed() != null) {
                    existingStatics.setWindSpeed(statics.getWindSpeed());
                }
                if (statics.getWindDirection() != null) {
                    existingStatics.setWindDirection(statics.getWindDirection());
                }

                return existingStatics;
            })
            .map(staticsRepository::save);
    }

    /**
     * Get all the statics.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Statics> findAll(Pageable pageable) {
        return staticsRepository.findAll(pageable);
    }

    /**
     * Get one statics by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Statics> findOne(Long id) {
        LOG.debug("Request to get Statics : {}", id);
        return staticsRepository.findById(id);
    }

    /**
     * Delete the statics by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Statics : {}", id);
        staticsRepository.deleteById(id);
    }

    // 定时任务，每10秒执行一次
    @Scheduled(fixedRate = 10000)
    public void addNewStatics() {
        LOG.debug("Executing scheduled task to add new Statics");

        String locationId = "101272001";
        LOG.debug("Using locationId: {}", locationId); // 添加日志输出locationId

        // 获取最新的WeatherData记录
        WeatherData latestWeatherData = weatherDataRepository.findFirstByLocationIdOrderByObservationTimeDesc(locationId);
        if (latestWeatherData != null) {
            Statics newStatics = new Statics();
            newStatics.setWindSpeed(latestWeatherData.getWindSpeed().longValue());
            newStatics.setWindDirection(latestWeatherData.getWindDirection());

            // 设置其他字段，这里假设其他字段可以为null或默认值
            newStatics.setTime(java.time.Instant.now());
            //            newStatics.setPosX(0L);
            //            newStatics.setPosY(0L);
            //            newStatics.setPosZ(0L);
            Random rand = new Random();
            newStatics.setPosX(rand.nextLong(20000) - 10000);
            newStatics.setPosY(rand.nextLong(20000) - 10000);
            newStatics.setPosZ(rand.nextLong(9999) + 1);
            // 保存新的Statics记录git
            Statics savedStatics = save(newStatics);
            LOG.debug("New Statics record added with ID: {}", savedStatics.getId());
        } else {
            LOG.warn("No latest WeatherData found to create a new Statics record for locationId: {}", locationId); // 修改日志输出，包含locationId
        }
    }
}
