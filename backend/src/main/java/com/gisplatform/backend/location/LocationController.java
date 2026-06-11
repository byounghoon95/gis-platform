package com.gisplatform.backend.location;

import com.gisplatform.backend.location.dto.LocationRequest;
import com.gisplatform.backend.location.dto.LocationResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

@Validated
@RestController
@RequestMapping("/api/admin/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<LocationResponse> create(@Valid @RequestBody LocationRequest request) {
        LocationResponse response = locationService.create(request);
        return ResponseEntity
                .created(URI.create("/api/admin/locations/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<LocationResponse>> list(
            @RequestParam(required = false) String businessType,
            @RequestParam(required = false) @Min(0) @Max(100) Integer minScore,
            @RequestParam(required = false) @Min(0) @Max(100) Integer maxScore,
            @RequestParam(required = false) String keyword
    ) {
        if (minScore != null && maxScore != null && minScore > maxScore) {
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(locationService.list(businessType, minScore, maxScore, keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody LocationRequest request
    ) {
        return ResponseEntity.ok(locationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        locationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
