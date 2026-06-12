package com.gisplatform.backend.analysis;

public record NearbySummary(
        int transitStopCount,
        int subwayStationCount,
        int busStopCount,
        int demandFacilityCount,
        int competitorCount,
        double averageFootTraffic,
        Integer rentPrice
) {
}
