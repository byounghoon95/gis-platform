package com.gisplatform.backend.gisdata;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FootTrafficSampleRepository extends JpaRepository<FootTrafficSample, Long> {

    @Query(value = """
            SELECT COALESCE(AVG(count), 0)
            FROM foot_traffic_samples
            WHERE ST_DWithin(
                geom,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
                :radiusMeters
            )
            """, nativeQuery = true)
    double averageCountWithinRadius(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusMeters") int radiusMeters
    );

    @Query(value = """
            SELECT COALESCE(SUM(count), 0)
            FROM foot_traffic_samples
            WHERE ST_DWithin(
                geom,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
                :radiusMeters
            )
            """, nativeQuery = true)
    long sumCountWithinRadius(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusMeters") int radiusMeters
    );
}
