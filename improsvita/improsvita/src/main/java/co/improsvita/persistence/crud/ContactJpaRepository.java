package co.improsvita.persistence.crud;

import co.improsvita.domain.model.ContactRole;
import co.improsvita.persistence.entities.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContactJpaRepository extends JpaRepository<ContactEntity, Integer> {
    Optional<ContactEntity> findByEmail(String email);
    Optional<ContactEntity> findFirstByName(String name);
    List<ContactEntity> findByActiveTrue();
    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT c FROM ContactEntity c JOIN c.roles r WHERE r = :role")
    List<ContactEntity> findByRole(@Param("role") ContactRole role);

    @Query("SELECT DISTINCT c FROM ContactEntity c JOIN c.roles r WHERE r = :role AND c.active = true")
    List<ContactEntity> findActiveByRole(@Param("role") ContactRole role);
}
