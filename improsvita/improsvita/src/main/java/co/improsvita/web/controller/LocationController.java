package co.improsvita.web.controller;

import co.improsvita.domain.model.Location;
import co.improsvita.domain.service.LocationService;
import co.improsvita.web.dto.LocationRequest;
import co.improsvita.web.dto.LocationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<List<LocationResponse>> getAllLocations() {
        List<LocationResponse> locations = locationService.getAllLocations()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getLocationById(@PathVariable Integer id) {
        Location location = locationService.getLocationById(id);
        if (location == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(location));
    }

    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(@RequestBody LocationRequest request) {
        Location location = new Location();
        location.setLocationName(request.getLocationName());
        location.setActive(true);

        Location created = locationService.createLocation(location);
        return ResponseEntity.ok(toResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationResponse> updateLocation(@PathVariable Integer id, @RequestBody LocationRequest request) {
        Location existing = locationService.getLocationById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setLocationName(request.getLocationName());
        if (request.getActive() != null) {
            existing.setActive(request.getActive());
        }

        Location updated = locationService.updateLocation(existing);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocationById(@PathVariable Integer id) {
        locationService.deleteLocationById(id);
        return ResponseEntity.noContent().build();
    }

    private LocationResponse toResponse(Location location) {
        LocationResponse response = new LocationResponse();
        response.setId(location.getId());
        response.setLocationName(location.getLocationName());
        response.setActive(location.getActive());
        return response;
    }
}