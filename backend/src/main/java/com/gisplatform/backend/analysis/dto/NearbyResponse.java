package com.gisplatform.backend.analysis.dto;

import java.util.List;

public record NearbyResponse(
        Long locationId,
        int radiusMeters,
        NearbyCountsResponse counts,
        List<NearbyFacilityResponse> facilities,
        List<NearbyCompetitorResponse> competitors,
        List<NearbyTransitStopResponse> transitStops,
        FootTrafficSummaryResponse footTraffic
) {
}
