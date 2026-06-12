package com.gisplatform.backend.gisdata;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompetitorRepository extends JpaRepository<Competitor, Long> {

    @Query(value = """
            SELECT *
            FROM competitors
            WHERE business_type = :businessType
              AND ST_DWithin(
                  geom,
                  ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
                  :radiusMeters
              )
            """, nativeQuery = true)
    List<Competitor> findWithinRadiusAndBusinessType(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusMeters") int radiusMeters,
            @Param("businessType") String businessType
    );

    @Query(value = """
            SELECT COUNT(*)
            FROM competitors
            WHERE business_type = :businessType
              AND ST_DWithin(
                  geom,
                  ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
                  :radiusMeters
              )
            """, nativeQuery = true)
    long countWithinRadiusAndBusinessType(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusMeters") int radiusMeters,
            @Param("businessType") String businessType
    );
}
