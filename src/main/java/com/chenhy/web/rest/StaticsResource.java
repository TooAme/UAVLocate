package com.chenhy.web.rest;

import com.chenhy.domain.Statics;
import com.chenhy.repository.StaticsRepository;
import com.chenhy.service.StaticsService;
import com.chenhy.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.chenhy.domain.Statics}.
 */
@RestController
@RequestMapping("/api/statics")
public class StaticsResource {

    private static final Logger LOG = LoggerFactory.getLogger(StaticsResource.class);

    private static final String ENTITY_NAME = "statics";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StaticsService staticsService;

    private final StaticsRepository staticsRepository;

    public StaticsResource(StaticsService staticsService, StaticsRepository staticsRepository) {
        this.staticsService = staticsService;
        this.staticsRepository = staticsRepository;
    }

    /**
     * {@code POST  /statics} : Create a new statics.
     *
     * @param statics the statics to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new statics, or with status {@code 400 (Bad Request)} if the statics has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Statics> createStatics(@RequestBody Statics statics) throws URISyntaxException {
        LOG.debug("REST request to save Statics : {}", statics);
        if (statics.getId() != null) {
            throw new BadRequestAlertException("A new statics cannot already have an ID", ENTITY_NAME, "idexists");
        }
        statics = staticsService.save(statics);
        return ResponseEntity.created(new URI("/api/statics/" + statics.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, statics.getId().toString()))
            .body(statics);
    }

    /**
     * {@code PUT  /statics/:id} : Updates an existing statics.
     *
     * @param id the id of the statics to save.
     * @param statics the statics to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statics,
     * or with status {@code 400 (Bad Request)} if the statics is not valid,
     * or with status {@code 500 (Internal Server Error)} if the statics couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Statics> updateStatics(@PathVariable(value = "id", required = false) final Long id, @RequestBody Statics statics)
        throws URISyntaxException {
        LOG.debug("REST request to update Statics : {}, {}", id, statics);
        if (statics.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statics.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!staticsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        statics = staticsService.update(statics);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, statics.getId().toString()))
            .body(statics);
    }

    /**
     * {@code PATCH  /statics/:id} : Partial updates given fields of an existing statics, field will ignore if it is null
     *
     * @param id the id of the statics to save.
     * @param statics the statics to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statics,
     * or with status {@code 400 (Bad Request)} if the statics is not valid,
     * or with status {@code 404 (Not Found)} if the statics is not found,
     * or with status {@code 500 (Internal Server Error)} if the statics couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Statics> partialUpdateStatics(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Statics statics
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Statics partially : {}, {}", id, statics);
        if (statics.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statics.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!staticsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Statics> result = staticsService.partialUpdate(statics);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, statics.getId().toString())
        );
    }

    /**
     * {@code GET  /statics} : get all the statics.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of statics in body.
     */
    @GetMapping("")
    public List<Statics> getAllStatics(@RequestParam(name = "sort", required = false, defaultValue = "id,desc") String sort) {
        LOG.debug("REST request to get all Statics");
        Pageable pageable = PageRequest.of(0, 10);
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            Sort.Direction direction = "desc".equalsIgnoreCase(sortParams[1]) ? Sort.Direction.DESC : Sort.Direction.ASC;
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, sortParams[0]));
        }
        return staticsService.findAll(pageable).getContent();
    }

    /**
     * {@code GET  /statics/:id} : get the "id" statics.
     *
     * @param id the id of the statics to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the statics, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Statics> getStatics(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Statics : {}", id);
        Optional<Statics> statics = staticsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(statics);
    }

    /**
     * {@code DELETE  /statics/:id} : delete the "id" statics.
     *
     * @param id the id of the statics to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatics(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Statics : {}", id);
        staticsService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
