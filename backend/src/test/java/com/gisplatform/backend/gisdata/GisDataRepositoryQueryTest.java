package com.gisplatform.backend.gisdata;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Query;

import static org.assertj.core.api.Assertions.assertThat;

class GisDataRepositoryQueryTest {

    @Test
    void facilitiesUsePostgisRadiusQueries() throws Exception {
        Query query = query(FacilityRepository.class, "findWithinRadius", BigDecimal.class, BigDecimal.class, int.class);

        assertRadiusQuery(query.value(), "facilities");
    }

    @Test
    void competitorsFilterByBusinessTypeAndRadius() throws Exception {
        Query query = query(
                CompetitorRepository.class,
                "findWithinRadiusAndBusinessType",
                BigDecimal.class,
                BigDecimal.class,
                int.class,
                String.class
        );

        assertRadiusQuery(query.value(), "competitors");
        assertThat(query.value()).contains("business_type = :businessType");
    }

    @Test
    void transitStopsFilterByTypeAndRadius() throws Exception {
        Query query = query(
                TransitStopRepository.class,
                "findWithinRadiusAndType",
                BigDecimal.class,
                BigDecimal.class,
                int.class,
                String.class
        );

        assertRadiusQuery(query.value(), "transit_stops");
        assertThat(query.value()).contains("type = :type");
    }

    @Test
    void footTrafficAggregatesByRadius() throws Exception {
        Query averageQuery = query(
                FootTrafficSampleRepository.class,
                "averageCountWithinRadius",
                BigDecimal.class,
                BigDecimal.class,
                int.class
        );
        Query sumQuery = query(
                FootTrafficSampleRepository.class,
                "sumCountWithinRadius",
                BigDecimal.class,
                BigDecimal.class,
                int.class
        );

        assertRadiusQuery(averageQuery.value(), "foot_traffic_samples");
        assertRadiusQuery(sumQuery.value(), "foot_traffic_samples");
        assertThat(averageQuery.value()).contains("AVG(count)");
        assertThat(sumQuery.value()).contains("SUM(count)");
    }

    private static Query query(Class<?> repositoryType, String methodName, Class<?>... parameterTypes) throws Exception {
        Method method = repositoryType.getMethod(methodName, parameterTypes);
        return method.getAnnotation(Query.class);
    }

    private static void assertRadiusQuery(String query, String tableName) {
        assertThat(query).contains(tableName, "ST_DWithin", "ST_SetSRID", "ST_MakePoint", "::geography");
    }
}
