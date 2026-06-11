package com.gisplatform.backend.location;

import com.gisplatform.backend.location.dto.LocationRequest;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LocationServiceTest {

    private final LocationRepository locationRepository = mock(LocationRepository.class);
    private final EntityManager entityManager = mock(EntityManager.class);
    private final LocationService locationService = new LocationService(locationRepository, entityManager);

    @Test
    void createStoresLocation() {
        when(locationRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

        locationService.create(sampleRequest("Gangnam Cafe"));

        verify(locationRepository).saveAndFlush(any(Location.class));
    }

    @Test
    void listUsesFilterSpecificationAndSort() {
        when(locationRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of());

        assertThat(locationService.list("CAFE", 10, 90, "gangnam")).isEmpty();

        verify(locationRepository).findAll(any(Specification.class), eq(Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Test
    void getMissingLocationThrowsNotFound() {
        when(locationRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationService.get(404L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404 NOT_FOUND");
    }

    @Test
    void updateMutatesExistingLocation() {
        Location location = new Location(
                "Old",
                "CAFE",
                "Old Address",
                new BigDecimal("37.0"),
                new BigDecimal("127.0"),
                null,
                null
        );
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        locationService.update(1L, sampleRequest("Updated"));

        assertThat(location.getName()).isEqualTo("Updated");
        verify(locationRepository).flush();
        verify(entityManager).refresh(location);
    }

    @Test
    void deleteRemovesExistingLocation() {
        Location location = new Location(
                "Gangnam Cafe",
                "CAFE",
                "Seoul Gangnam",
                new BigDecimal("37.4979"),
                new BigDecimal("127.0276"),
                3000000,
                "near station"
        );
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        locationService.delete(1L);

        verify(locationRepository).delete(location);
    }

    private LocationRequest sampleRequest(String name) {
        return new LocationRequest(
                name,
                "CAFE",
                "Seoul Gangnam",
                new BigDecimal("37.4979"),
                new BigDecimal("127.0276"),
                3000000,
                "near station"
        );
    }
}
