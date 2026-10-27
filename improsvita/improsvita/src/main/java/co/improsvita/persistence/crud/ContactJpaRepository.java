package co.improsvita.persistence.crud;

import co.improsvita.persistence.entities.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactJpaRepository extends JpaRepository<ContactEntity, Integer> {
    Optional<ContactEntity> findByDocumentNumberIgnoreCase(String documentNumber);
    Optional<ContactEntity> findByEmailIgnoreCase(String email);
}
