package com.chenhy.service;

import com.chenhy.domain.Statics;
import com.chenhy.repository.StaticsRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Service Implementation for managing {@link com.chenhy.domain.Statics}.
 */
@Service
@Transactional
public class StaticsService {

    private static final Logger LOG = LoggerFactory.getLogger(StaticsService.class);

    private final StaticsRepository staticsRepository;

    public StaticsService(StaticsRepository staticsRepository) {
        this.staticsRepository = staticsRepository;
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
}
