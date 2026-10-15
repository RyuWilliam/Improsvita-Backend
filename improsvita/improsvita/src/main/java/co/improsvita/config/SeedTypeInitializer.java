package co.improsvita.config;

import co.improsvita.persistence.crud.SeedTypeJpaRepository;
import co.improsvita.persistence.entities.SeedTypeEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class SeedTypeInitializer implements CommandLineRunner {

    private final SeedTypeJpaRepository seedTypeJpaRepository;

    public SeedTypeInitializer(SeedTypeJpaRepository seedTypeJpaRepository) {
        this.seedTypeJpaRepository = seedTypeJpaRepository;
    }

    @Override
    public void run(String... args) {
        Map<String, String> defaults = new LinkedHashMap<>();
        defaults.put("HYBRID", "Semilla híbrida");
        defaults.put("TRADITIONAL", "Semilla tradicional");
        defaults.put("MODIFIED", "Semilla modificada");

        defaults.forEach((name, description) -> {
            if (seedTypeJpaRepository.findByNameIgnoreCase(name).isEmpty()) {
                seedTypeJpaRepository.save(new SeedTypeEntity(name, description));
            }
        });
    }
}