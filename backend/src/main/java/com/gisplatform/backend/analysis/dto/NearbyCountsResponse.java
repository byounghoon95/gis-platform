package com.gisplatform.backend.analysis.dto;

public record NearbyCountsResponse(
        int transitStopCount,
        int subwayStationCount,
        int busStopCount,
        int demandFacilityCount,
        int competitorCount
) {
}
