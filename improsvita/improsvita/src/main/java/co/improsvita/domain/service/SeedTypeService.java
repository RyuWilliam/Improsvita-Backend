package co.improsvita.domain.service;

import co.improsvita.domain.model.SeedTypeCatalog;
import co.improsvita.domain.repository.SeedTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeedTypeService {

    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_DESCRIPTION_LENGTH = 255;

    private final SeedTypeRepository seedTypeRepository;

    public SeedTypeService(SeedTypeRepository seedTypeRepository) {
        this.seedTypeRepository = seedTypeRepository;
    }

    public List<SeedTypeCatalog> getAllSeedTypes() {
        return seedTypeRepository.getAll();
    }

    public SeedTypeCatalog getSeedTypeById(Integer id) {
        return seedTypeRepository.getById(id);
    }

    public SeedTypeCatalog resolveSeedType(Integer typeId, String typeName) {
        if (typeId != null) {
            SeedTypeCatalog seedType = seedTypeRepository.getById(typeId);
            if (seedType == null) {
                throw new IllegalArgumentException("Tipo de semilla no encontrado: " + typeId);
            }
            return seedType;
        }
        if (typeName != null && !typeName.isBlank()) {
            SeedTypeCatalog seedType = seedTypeRepository.getByName(typeName.trim());
            if (seedType == null) {
                throw new IllegalArgumentException("Tipo de semilla no encontrado: " + typeName);
            }
            return seedType;
        }
        return null;
    }

    public SeedTypeCatalog createSeedType(String name, String description) {
        String cleanName = validateName(name);
        String cleanDescription = validateDescription(description);

        if (seedTypeRepository.getByName(cleanName) != null) {
            throw new IllegalStateException("Ya existe un tipo de semilla con el nombre " + cleanName);
        }

        SeedTypeCatalog seedType = new SeedTypeCatalog();
        seedType.setName(cleanName);
        seedType.setDescription(cleanDescription);
        return seedTypeRepository.save(seedType);
    }

    public SeedTypeCatalog updateSeedType(Integer id, String name, String description) {
        SeedTypeCatalog existing = requireSeedType(id);
        String cleanName = validateName(name);
        String cleanDescription = validateDescription(description);

        SeedTypeCatalog sameName = seedTypeRepository.getByName(cleanName);
        if (sameName != null && !sameName.getId().equals(id)) {
            throw new IllegalStateException("Ya existe un tipo de semilla con el nombre " + cleanName);
        }

        existing.setName(cleanName);
        existing.setDescription(cleanDescription);
        return seedTypeRepository.save(existing);
    }

    public void deleteSeedType(Integer id) {
        requireSeedType(id);
        if (seedTypeRepository.isInUse(id)) {
            throw new IllegalStateException(
                    "No se puede eliminar el tipo de semilla " + id + " porque tiene semillas asociadas");
        }
        seedTypeRepository.deleteById(id);
    }

    private SeedTypeCatalog requireSeedType(Integer id) {
        SeedTypeCatalog seedType = seedTypeRepository.getById(id);
        if (seedType == null) {
            throw new IllegalArgumentException("Tipo de semilla no encontrado: " + id);
        }
        return seedType;
    }

    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del tipo de semilla es obligatorio");
        }
        String clean = name.trim();
        if (clean.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "El nombre no puede superar los " + MAX_NAME_LENGTH + " caracteres");
        }
        return clean;
    }

    private String validateDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        String clean = description.trim();
        if (clean.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    "La descripción no puede superar los " + MAX_DESCRIPTION_LENGTH + " caracteres");
        }
        return clean;
    }
}