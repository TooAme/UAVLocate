package com.chenhy.repository;

import com.chenhy.domain.Statics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Statics entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StaticsRepository extends JpaRepository<Statics, Long> {}
