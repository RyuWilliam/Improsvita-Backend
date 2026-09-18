package co.improsvita.persistence.crud;

import co.improsvita.persistence.entities.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierJpaRepository extends JpaRepository<SupplierEntity, Integer> {
    Optional<SupplierEntity> findByName(String name);
}
