package co.improsvita.persistence;

import co.improsvita.domain.model.Location;
import co.improsvita.domain.repository.LocationRepository;
import co.improsvita.persistence.crud.LocationJpaRepository;
import co.improsvita.persistence.entities.LocationEntity;
import co.improsvita.persistence.mapper.LocationMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class LocationRepositoryImpl implements LocationRepository {

    private final LocationJpaRepository locationJpaRepository;

    public LocationRepositoryImpl(LocationJpaRepository locationJpaRepository) {
        this.locationJpaRepository = locationJpaRepository;
    }

    @Override
    public List<Location> getAll() {
        return locationJpaRepository.findAll()
                .stream()
                .map(LocationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Location getById(Integer id) {
        return locationJpaRepository.findById(id)
                .map(LocationMapper::toDomain)
                .orElse(null);
    }

    @Override
    @Transactional
    public Location save(Location location) {
        LocationEntity saved = locationJpaRepository.save(LocationMapper.toEntity(location));
        return LocationMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        locationJpaRepository.deleteById(id);
    }
}