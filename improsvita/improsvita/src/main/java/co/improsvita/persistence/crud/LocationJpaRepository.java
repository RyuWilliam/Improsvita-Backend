package co.improsvita.persistence.crud;

import co.improsvita.persistence.entities.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationJpaRepository extends JpaRepository<LocationEntity, Integer> {
}