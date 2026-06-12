package com.gisplatform.backend.gisdata;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

    @Query(value = """
            SELECT *
            FROM facilities
            WHERE ST_DWithin(
                geom,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
                :radiusMeters
            )
            """, nativeQuery = true)
    List<Facility> findWithinRadius(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusMeters") int radiusMeters
    );

    @Query(value = """
            SELECT COUNT(*)
            FROM facilities
            WHERE ST_DWithin(
                geom,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
                :radiusMeters
            )
            """, nativeQuery = true)
    long countWithinRadius(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusMeters") int radiusMeters
    );
}
