package com.gisplatform.backend.location;

import com.gisplatform.backend.location.dto.LocationRequest;
import com.gisplatform.backend.location.dto.LocationResponse;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final EntityManager entityManager;

    public LocationService(LocationRepository locationRepository, EntityManager entityManager) {
        this.locationRepository = locationRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public LocationResponse create(LocationRequest request) {
        Location location = new Location(
                request.name(),
                request.businessType(),
                request.address(),
                request.latitude(),
                request.longitude(),
                request.rentPrice(),
                request.memo()
        );

        Location savedLocation = locationRepository.saveAndFlush(location);
        entityManager.refresh(savedLocation);

        return toResponse(savedLocation);
    }

    @Transactional(readOnly = true)
    public List<LocationResponse> list(String businessType, Integer minScore, Integer maxScore, String keyword) {
        Specification<Location> specification = Specification
                .where(LocationSpecifications.businessTypeEquals(businessType))
                .and(LocationSpecifications.keywordContains(keyword));

        return locationRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LocationResponse get(Long id) {
        return toResponse(findLocation(id));
    }

    @Transactional
    public LocationResponse update(Long id, LocationRequest request) {
        Location location = findLocation(id);
        location.update(
                request.name(),
                request.businessType(),
                request.address(),
                request.latitude(),
                request.longitude(),
                request.rentPrice(),
                request.memo()
        );

        locationRepository.flush();
        entityManager.refresh(location);

        return toResponse(location);
    }

    @Transactional
    public void delete(Long id) {
        Location location = findLocation(id);
        locationRepository.delete(location);
    }

    private Location findLocation(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found"));
    }

    private LocationResponse toResponse(Location location) {
        return new LocationResponse(
                location.getId(),
                location.getName(),
                location.getBusinessType(),
                location.getAddress(),
                location.getLatitude(),
                location.getLongitude(),
                location.getRentPrice(),
                location.getMemo(),
                location.getCreatedAt(),
                location.getUpdatedAt()
        );
    }
}
