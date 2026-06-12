package com.gisplatform.backend.analysis;

import com.gisplatform.backend.analysis.dto.FootTrafficSummaryResponse;
import com.gisplatform.backend.analysis.dto.LocationScoreResponse;
import com.gisplatform.backend.analysis.dto.NearbyCompetitorResponse;
import com.gisplatform.backend.analysis.dto.NearbyCountsResponse;
import com.gisplatform.backend.analysis.dto.NearbyFacilityResponse;
import com.gisplatform.backend.analysis.dto.NearbyResponse;
import com.gisplatform.backend.analysis.dto.NearbyTransitStopResponse;
import com.gisplatform.backend.gisdata.Competitor;
import com.gisplatform.backend.gisdata.CompetitorRepository;
import com.gisplatform.backend.gisdata.Facility;
import com.gisplatform.backend.gisdata.FacilityRepository;
import com.gisplatform.backend.gisdata.FootTrafficSampleRepository;
import com.gisplatform.backend.gisdata.TransitStop;
import com.gisplatform.backend.gisdata.TransitStopRepository;
import com.gisplatform.backend.gisdata.TransitStopType;
import com.gisplatform.backend.location.Location;
import com.gisplatform.backend.location.LocationRepository;
import jakarta.persistence.EntityManager;
import java.util.Comparator;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LocationAnalysisService {

    private final LocationRepository locationRepository;
    private final FacilityRepository facilityRepository;
    private final CompetitorRepository competitorRepository;
    private final TransitStopRepository transitStopRepository;
    private final FootTrafficSampleRepository footTrafficSampleRepository;
    private final LocationScoreRepository locationScoreRepository;
    private final LocationScoreCalculator scoreCalculator;
    private final EntityManager entityManager;

    public LocationAnalysisService(
            LocationRepository locationRepository,
            FacilityRepository facilityRepository,
            CompetitorRepository competitorRepository,
            TransitStopRepository transitStopRepository,
            FootTrafficSampleRepository footTrafficSampleRepository,
            LocationScoreRepository locationScoreRepository,
            LocationScoreCalculator scoreCalculator,
            EntityManager entityManager
    ) {
        this.locationRepository = locationRepository;
        this.facilityRepository = facilityRepository;
        this.competitorRepository = competitorRepository;
        this.transitStopRepository = transitStopRepository;
        this.footTrafficSampleRepository = footTrafficSampleRepository;
        this.locationScoreRepository = locationScoreRepository;
        this.scoreCalculator = scoreCalculator;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public NearbyResponse getNearby(Long locationId, int radiusMeters) {
        Location location = findLocation(locationId);
        NearbyData nearby = loadNearby(location, radiusMeters);
        return toNearbyResponse(location, radiusMeters, nearby);
    }

    @Transactional
    public LocationScoreResponse analyze(Long locationId, int radiusMeters) {
        Location location = findLocation(locationId);
        NearbyData nearby = loadNearby(location, radiusMeters);
        NearbySummary summary = nearby.summary(location.getRentPrice());
        ScoreBreakdown score = scoreCalculator.calculate(summary, location.getRentPrice());
        String explanation = buildExplanation(radiusMeters, summary);
        LocationScore savedScore = locationScoreRepository.saveAndFlush(
                new LocationScore(location.getId(), radiusMeters, score, explanation)
        );
        entityManager.refresh(savedScore);

        return toScoreResponse(savedScore);
    }

    @Transactional(readOnly = true)
    public LocationScoreResponse getLatestScore(Long locationId) {
        findLocation(locationId);
        return locationScoreRepository.findFirstByLocationIdOrderByCalculatedAtDesc(locationId)
                .map(this::toScoreResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location score not found"));
    }

    private Location findLocation(Long locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found"));
    }

    private NearbyData loadNearby(Location location, int radiusMeters) {
        List<Facility> facilities = facilityRepository.findWithinRadius(
                location.getLatitude(), location.getLongitude(), radiusMeters);
        List<Competitor> competitors = competitorRepository.findWithinRadiusAndBusinessType(
                location.getLatitude(), location.getLongitude(), radiusMeters, location.getBusinessType());
        List<TransitStop> subwayStops = transitStopRepository.findWithinRadiusAndType(
                location.getLatitude(), location.getLongitude(), radiusMeters, TransitStopType.SUBWAY.name());
        List<TransitStop> busStops = transitStopRepository.findWithinRadiusAndType(
                location.getLatitude(), location.getLongitude(), radiusMeters, TransitStopType.BUS.name());
        double averageFootTraffic = footTrafficSampleRepository.averageCountWithinRadius(
                location.getLatitude(), location.getLongitude(), radiusMeters);

        return new NearbyData(facilities, competitors, subwayStops, busStops, averageFootTraffic);
    }

    private NearbyResponse toNearbyResponse(Location location, int radiusMeters, NearbyData nearby) {
        NearbySummary summary = nearby.summary(location.getRentPrice());
        List<NearbyTransitStopResponse> transitStops = nearby.transitStops().stream()
                .sorted(Comparator.comparing(NearbyTransitStopResponse::type).thenComparing(NearbyTransitStopResponse::name))
                .toList();

        return new NearbyResponse(
                location.getId(),
                radiusMeters,
                new NearbyCountsResponse(
                        summary.transitStopCount(),
                        summary.subwayStationCount(),
                        summary.busStopCount(),
                        summary.demandFacilityCount(),
                        summary.competitorCount()
                ),
                nearby.facilities().stream().map(this::toFacilityResponse).toList(),
                nearby.competitors().stream().map(this::toCompetitorResponse).toList(),
                transitStops,
                new FootTrafficSummaryResponse(summary.averageFootTraffic())
        );
    }

    private String buildExplanation(int radiusMeters, NearbySummary summary) {
        return "%dm radius contains %d subway stations and %d bus stops. Demand facility count is %d, competitor count is %d, and average foot traffic is %.1f. Rent baseline is %s."
                .formatted(
                        radiusMeters,
                        summary.subwayStationCount(),
                        summary.busStopCount(),
                        summary.demandFacilityCount(),
                        summary.competitorCount(),
                        summary.averageFootTraffic(),
                        summary.rentPrice() == null ? "not available" : summary.rentPrice().toString()
                );
    }

    private LocationScoreResponse toScoreResponse(LocationScore score) {
        return new LocationScoreResponse(
                score.getLocationId(),
                score.getRadiusMeters(),
                score.getFootTrafficScore(),
                score.getTransportScore(),
                score.getDemandScore(),
                score.getCompetitionScore(),
                score.getRentScore(),
                score.getTotalScore(),
                score.getExplanation(),
                score.getCalculatedAt()
        );
    }

    private NearbyFacilityResponse toFacilityResponse(Facility facility) {
        return new NearbyFacilityResponse(
                facility.getId(),
                facility.getName(),
                facility.getCategory(),
                facility.getAddress(),
                facility.getLatitude(),
                facility.getLongitude()
        );
    }

    private NearbyCompetitorResponse toCompetitorResponse(Competitor competitor) {
        return new NearbyCompetitorResponse(
                competitor.getId(),
                competitor.getName(),
                competitor.getBusinessType(),
                competitor.getAddress(),
                competitor.getLatitude(),
                competitor.getLongitude()
        );
    }


    private record NearbyData(
            List<Facility> facilities,
            List<Competitor> competitors,
            List<TransitStop> subwayStops,
            List<TransitStop> busStops,
            double averageFootTraffic
    ) {
        NearbySummary summary(Integer rentPrice) {
            return new NearbySummary(
                    subwayStops.size() + busStops.size(),
                    subwayStops.size(),
                    busStops.size(),
                    facilities.size(),
                    competitors.size(),
                    averageFootTraffic,
                    rentPrice
            );
        }

        List<NearbyTransitStopResponse> transitStops() {
            return java.util.stream.Stream.concat(subwayStops.stream(), busStops.stream())
                    .map(stop -> new NearbyTransitStopResponse(
                            stop.getId(),
                            stop.getName(),
                            stop.getType().name(),
                            stop.getLatitude(),
                            stop.getLongitude()
                    ))
                    .toList();
        }
    }
}
