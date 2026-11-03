package co.improsvita.persistence.crud;

import co.improsvita.persistence.entities.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientJpaRepository extends JpaRepository<ClientEntity, Integer> {
    Optional<ClientEntity> findByName(String name);
    Optional<ClientEntity> findByEmail(String email);
}
