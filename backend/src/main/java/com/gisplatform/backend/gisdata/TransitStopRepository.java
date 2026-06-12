package com.gisplatform.backend.gisdata;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransitStopRepository extends JpaRepository<TransitStop, Long> {

    @Query(value = """
            SELECT *
            FROM transit_stops
            WHERE type = :type
              AND ST_DWithin(
                  geom,
                  ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
                  :radiusMeters
              )
            """, nativeQuery = true)
    List<TransitStop> findWithinRadiusAndType(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusMeters") int radiusMeters,
            @Param("type") String type
    );

    @Query(value = """
            SELECT COUNT(*)
            FROM transit_stops
            WHERE type = :type
              AND ST_DWithin(
                  geom,
                  ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
                  :radiusMeters
              )
            """, nativeQuery = true)
    long countWithinRadiusAndType(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusMeters") int radiusMeters,
            @Param("type") String type
    );
}
