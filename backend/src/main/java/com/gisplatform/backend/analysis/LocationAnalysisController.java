package com.gisplatform.backend.analysis;

import com.gisplatform.backend.analysis.dto.LocationScoreResponse;
import com.gisplatform.backend.analysis.dto.NearbyResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/locations")
public class LocationAnalysisController {

    private final LocationAnalysisService locationAnalysisService;

    public LocationAnalysisController(LocationAnalysisService locationAnalysisService) {
        this.locationAnalysisService = locationAnalysisService;
    }

    @GetMapping("/{locationId}/nearby")
    public ResponseEntity<NearbyResponse> nearby(
            @PathVariable Long locationId,
            @RequestParam(defaultValue = "500") @Min(1) @Max(5000) int radius
    ) {
        return ResponseEntity.ok(locationAnalysisService.getNearby(locationId, radius));
    }

    @PostMapping("/{locationId}/analysis")
    public ResponseEntity<LocationScoreResponse> analyze(
            @PathVariable Long locationId,
            @RequestParam(defaultValue = "500") @Min(1) @Max(5000) int radius
    ) {
        return ResponseEntity.ok(locationAnalysisService.analyze(locationId, radius));
    }

    @GetMapping("/{locationId}/score")
    public ResponseEntity<LocationScoreResponse> latestScore(@PathVariable Long locationId) {
        return ResponseEntity.ok(locationAnalysisService.getLatestScore(locationId));
    }
}
