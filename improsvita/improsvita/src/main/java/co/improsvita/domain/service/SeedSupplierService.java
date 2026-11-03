package co.improsvita.domain.service;

import co.improsvita.domain.model.ContactRole;
import co.improsvita.domain.model.SeedSupplier;
import co.improsvita.domain.repository.SeedRepository;
import co.improsvita.domain.repository.SeedSupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeedSupplierService {

    private final SeedSupplierRepository seedSupplierRepository;
    private final SeedRepository seedRepository;
    private final ContactService contactService;

    public SeedSupplierService(SeedSupplierRepository seedSupplierRepository,
                               SeedRepository seedRepository,
                               ContactService contactService) {
        this.seedSupplierRepository = seedSupplierRepository;
        this.seedRepository = seedRepository;
        this.contactService = contactService;
    }

    public SeedSupplier link(Integer seedId, Integer supplierId) {
        if (seedRepository.getById(seedId) == null) {
            throw new IllegalArgumentException("Semilla no encontrada: " + seedId);
        }
        contactService.assertHasRole(supplierId, ContactRole.SUPPLIER);
        return seedSupplierRepository.findActiveBySeedAndSupplier(seedId, supplierId)
                .orElseGet(() -> {
                    SeedSupplier bridge = new SeedSupplier();
                    bridge.setSeedId(seedId);
                    bridge.setSupplierId(supplierId);
                    bridge.setActive(true);
                    return seedSupplierRepository.save(bridge);
                });
    }

    public void unlink(Integer seedId, Integer supplierId) {
        seedSupplierRepository.findActiveBySeedAndSupplier(seedId, supplierId)
                .ifPresent(bridge -> {
                    bridge.setActive(false);
                    seedSupplierRepository.save(bridge);
                });
    }

    public List<SeedSupplier> getBySeedId(Integer seedId) {
        return seedSupplierRepository.getBySeedId(seedId);
    }

    public List<SeedSupplier> getBySupplierId(Integer supplierId) {
        return seedSupplierRepository.getBySupplierId(supplierId);
    }
}
