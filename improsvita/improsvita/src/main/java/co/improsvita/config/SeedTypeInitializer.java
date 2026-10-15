package co.improsvita.config;

import co.improsvita.persistence.crud.SeedJpaRepository;
import co.improsvita.persistence.crud.SeedTypeJpaRepository;
import co.improsvita.persistence.entities.SeedTypeEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class SeedTypeInitializer implements CommandLineRunner {

    private final SeedTypeJpaRepository seedTypeJpaRepository;
    private final SeedJpaRepository seedJpaRepository;

    public SeedTypeInitializer(SeedTypeJpaRepository seedTypeJpaRepository,
                               SeedJpaRepository seedJpaRepository) {
        this.seedTypeJpaRepository = seedTypeJpaRepository;
        this.seedJpaRepository = seedJpaRepository;
    }

    @Override
    public void run(String... args) {
        createDefaultTypes();
        migrateLegacySeedTypes();
    }

    private void createDefaultTypes() {
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

    private void migrateLegacySeedTypes() {
        seedJpaRepository.findBySeedTypeIsNull().forEach(seed -> {
            if (seed.getType() == null) {
                return;
            }
            seedTypeJpaRepository.findByNameIgnoreCase(seed.getType().name()).ifPresent(seedType -> {
                seed.setSeedType(seedType);
                seedJpaRepository.save(seed);
            });
        });
    }
}