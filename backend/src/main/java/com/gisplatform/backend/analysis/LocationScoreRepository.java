package com.gisplatform.backend.analysis;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationScoreRepository extends JpaRepository<LocationScore, Long> {

    Optional<LocationScore> findFirstByLocationIdOrderByCalculatedAtDesc(Long locationId);
}
