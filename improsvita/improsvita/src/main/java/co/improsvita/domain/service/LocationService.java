package co.improsvita.domain.service;

import co.improsvita.domain.model.Location;
import co.improsvita.domain.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<Location> getAllLocations() {
        return locationRepository.getAll();
    }

    public Location getLocationById(Integer id) {
        return locationRepository.getById(id);
    }

    public Location createLocation(Location location) {
        return locationRepository.save(location);
    }

    public Location updateLocation(Location location) {
        return locationRepository.save(location);
    }

    public void deleteLocationById(Integer id) {
        locationRepository.deleteById(id);
    }
}