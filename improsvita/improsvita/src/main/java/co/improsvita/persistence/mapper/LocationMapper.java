package co.improsvita.persistence.mapper;

import co.improsvita.domain.model.Location;
import co.improsvita.persistence.entities.LocationEntity;

public class LocationMapper {

    private LocationMapper() {
    }

    public static Location toDomain(LocationEntity entity) {
        if (entity == null) return null;
        Location location = new Location();
        location.setId(entity.getLocationId());
        location.setLocationName(entity.getLocationName());
        location.setActive(entity.getActive());
        return location;
    }

    public static LocationEntity toEntity(Location domain) {
        if (domain == null) return null;
        LocationEntity entity = new LocationEntity();
        entity.setLocationId(domain.getId());
        entity.setLocationName(domain.getLocationName());
        entity.setActive(domain.getActive());
        return entity;
    }
}