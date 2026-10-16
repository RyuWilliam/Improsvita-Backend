package co.improsvita.domain.service;

import co.improsvita.domain.model.Bed;
import co.improsvita.domain.model.Location;
import co.improsvita.domain.model.MovementType;
import co.improsvita.domain.model.Seed;
import co.improsvita.domain.model.SeedLot;
import co.improsvita.domain.model.SeedLotStatus;
import co.improsvita.domain.model.SeedMovement;
import co.improsvita.domain.model.SeedSupplier;
import co.improsvita.domain.model.Sowing;
import co.improsvita.domain.model.SowingStatus;
import co.improsvita.domain.model.Supplier;
import co.improsvita.domain.repository.BedRepository;
import co.improsvita.domain.repository.LocationRepository;
import co.improsvita.domain.repository.SeedLotRepository;
import co.improsvita.domain.repository.SeedMovementRepository;
import co.improsvita.domain.repository.SeedRepository;
import co.improsvita.domain.repository.SeedSupplierRepository;
import co.improsvita.domain.repository.SowingRepository;
import co.improsvita.domain.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.EnumSet;
import java.util.Set;

@Service
public class InventoryService {

    private final SeedRepository seedRepository;
    private final SupplierRepository supplierRepository;
    private final LocationRepository locationRepository;
    private final SeedLotRepository seedLotRepository;
    private final SeedMovementRepository seedMovementRepository;
    private final SeedSupplierRepository seedSupplierRepository;
    private final SowingRepository sowingRepository;
    private final BedRepository bedRepository;

    public InventoryService(SeedRepository seedRepository,
                            SupplierRepository supplierRepository,
                            LocationRepository locationRepository,
                            SeedLotRepository seedLotRepository,
                            SeedMovementRepository seedMovementRepository,
                            SeedSupplierRepository seedSupplierRepository,
                            SowingRepository sowingRepository,
                            BedRepository bedRepository) {
        this.seedRepository = seedRepository;
        this.supplierRepository = supplierRepository;
        this.locationRepository = locationRepository;
        this.seedLotRepository = seedLotRepository;
        this.seedMovementRepository = seedMovementRepository;
        this.seedSupplierRepository = seedSupplierRepository;
        this.sowingRepository = sowingRepository;
        this.bedRepository = bedRepository;
    }

    private static final Set<SowingStatus> INITIAL_STATUSES =
        EnumSet.of(SowingStatus.PLANNED, SowingStatus.IN_PROGRESS);

    @Transactional
    public SeedLot registerEntry(Integer seedId, Integer supplierId, Integer locationId,
                                 Integer lotNumber, Integer quantity,
                                 Integer germinationPercentage,
                                 LocalDate entryDate, LocalDate dueDate) {
        requirePositive(quantity, "La cantidad de entrada debe ser mayor a cero");
        if (germinationPercentage != null
                && (germinationPercentage < 1 || germinationPercentage > 100)) {
            throw new IllegalArgumentException("El porcentaje de germinación debe estar entre 1 y 100");
        }

        Seed seed = requireSeed(seedId);
        Supplier supplier = requireSupplier(supplierId);
        Location location = requireLocation(locationId);

        SeedSupplier bridge = seedSupplierRepository.findActiveBySeedAndSupplier(seed.getId(), supplier.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "El proveedor " + supplierId + " no suministra la semilla " + seedId + ". Registra primero la relación."));

        SeedLot lot = new SeedLot();
        lot.setLotNumber(lotNumber);
        lot.setSeedId(seed.getId());
        lot.setLocationId(location.getId());
        LocalDate entry = entryDate != null ? entryDate : LocalDate.now();
        lot.setEntryDate(entry);
        lot.setDueDate(dueDate != null ? dueDate : entry.plusYears(2));
        lot.setGerminationPercentage(germinationPercentage);
        lot.setInitialQuantity(quantity);
        lot.setAvailableQuantity(quantity);
        lot.setStatus(SeedLotStatus.AVAILABLE);
        SeedLot saved = seedLotRepository.save(lot);

        SeedMovement movement = new SeedMovement();
        movement.setLotId(saved.getId());
        movement.setSupplierId(bridge.getSupplierId());
        movement.setMovementType(MovementType.ENTRY);
        movement.setQuantity(quantity);
        movement.setMovementDate(LocalDateTime.now());
        movement.setReason("Entrada lote " + lotNumber);
        seedMovementRepository.save(movement);

        return saved;
    }

    @Transactional
    public SeedLot adjustQuantity(Integer lotId, Integer quantity, String reason,
                                  Integer germinationPercentage) {
        if (quantity == null || quantity == 0) {
            throw new IllegalArgumentException("La cantidad debe ser distinta de cero (positiva suma, negativa resta)");
        }
        if (germinationPercentage != null
                && (germinationPercentage < 1 || germinationPercentage > 100)) {
            throw new IllegalArgumentException("El porcentaje de germinación debe estar entre 1 y 100");
        }

        SeedLot lot = requireLot(lotId);
        int updated = lot.getAvailableQuantity() + quantity;
        if (updated < 0) {
            throw new IllegalStateException("El ajuste deja el lote " + lotId + " en negativo: " + updated);
        }

        SeedMovement movement = new SeedMovement();
        movement.setLotId(lot.getId());
        movement.setSupplierId(null);
        movement.setMovementType(MovementType.ADJUSTMENT);
        movement.setQuantity(quantity);
        movement.setMovementDate(LocalDateTime.now());
        movement.setReason(reason);
        seedMovementRepository.save(movement);

        lot.setAvailableQuantity(updated);
        if (germinationPercentage != null) {
            lot.setGerminationPercentage(germinationPercentage);
        }
        if (updated == 0) {
            lot.setStatus(SeedLotStatus.DEPLETED);
        } else if (lot.getStatus() == SeedLotStatus.DEPLETED) {
            lot.setStatus(SeedLotStatus.AVAILABLE);
        }
        return seedLotRepository.save(lot);
    }

    public SeedLot getLotById(Integer lotId) {
        return seedLotRepository.getById(lotId);
    }

    @Transactional
    public Sowing sow(Integer lotId, Integer bedId, Integer quantity,
                      LocalDate sowingDate, LocalDate expectedGerminationDate, String notes,
                      SowingStatus status) {
        requirePositive(quantity, "La cantidad sembrada debe ser mayor a cero");

        SowingStatus initialStatus = status != null ? status : SowingStatus.IN_PROGRESS;
        if (!INITIAL_STATUSES.contains(initialStatus)) {
            throw new IllegalArgumentException("Estado inicial no permitido: " + initialStatus
                    + ". Permitidos: " + INITIAL_STATUSES);
        }
        SeedLot lot = requireLot(lotId);
        Bed bed = bedRepository.getById(bedId);
        if (bed == null) {
            throw new IllegalArgumentException("Cama no encontrada: " + bedId);
        }
        if (Boolean.FALSE.equals(bed.getActive())) {
            throw new IllegalStateException("La cama " + bed.getCode() + " está inhabilitada");
        }
        if (lot.getAvailableQuantity() < quantity) {
            throw new IllegalStateException("Stock insuficiente en el lote " + lotId
                    + ": disponible " + lot.getAvailableQuantity() + ", solicitado " + quantity);
        }

        SeedMovement movement = new SeedMovement();
        movement.setLotId(lot.getId());
        movement.setSupplierId(null);
        movement.setMovementType(MovementType.EXIT);
        movement.setQuantity(quantity);
        movement.setMovementDate(LocalDateTime.now());
        movement.setReason("Siembra en cama " + bed.getCode());
        seedMovementRepository.save(movement);

        lot.setAvailableQuantity(lot.getAvailableQuantity() - quantity);
        if (lot.getAvailableQuantity() == 0) {
            lot.setStatus(SeedLotStatus.DEPLETED);
        }
        seedLotRepository.save(lot);

        Sowing sowing = new Sowing();
        sowing.setLotId(lot.getId());
        sowing.setBedId(bed.getId());
        sowing.setQuantitySown(quantity);
        sowing.setSowingDate(sowingDate != null ? sowingDate : LocalDate.now());
        sowing.setExpectedGerminationDate(expectedGerminationDate);
        sowing.setStatus(initialStatus);
        sowing.setNotes(notes);
        sowing.setActive(true);
        return sowingRepository.save(sowing);
    }

    public List<SeedLot> getAllLots() {
        return seedLotRepository.getAll();
    }

    public List<SeedLot> getLotsBySeed(Integer seedId) {
        return seedLotRepository.getBySeedId(seedId);
    }

    public List<SeedLot> getLotsByLocation(Integer locationId) {
        return seedLotRepository.getByLocationId(locationId);
    }

    public List<SeedLot> getLotsByStatus(SeedLotStatus status) {
        return seedLotRepository.getByStatus(status);
    }

    public List<SeedMovement> getKardex(Integer lotId) {
        requireLot(lotId);
        return seedMovementRepository.getByLotId(lotId);
    }

    public Long getStockBySeedId(Integer seedId) {
        requireSeed(seedId);
        return seedRepository.getStockBySeedId(seedId);
    }

    private Seed requireSeed(Integer seedId) {
        Seed seed = seedRepository.getById(seedId);
        if (seed == null) {
            throw new IllegalArgumentException("Semilla no encontrada: " + seedId);
        }
        return seed;
    }

    private Supplier requireSupplier(Integer supplierId) {
        Supplier supplier = supplierRepository.getById(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Proveedor no encontrado: " + supplierId);
        }
        return supplier;
    }

    private Location requireLocation(Integer locationId) {
        Location location = locationRepository.getById(locationId);
        if (location == null) {
            throw new IllegalArgumentException("Ubicación no encontrada: " + locationId);
        }
        return location;
    }

    private SeedLot requireLot(Integer lotId) {
        SeedLot lot = seedLotRepository.getById(lotId);
        if (lot == null) {
            throw new IllegalArgumentException("Lote no encontrado: " + lotId);
        }
        return lot;
    }

    private void requirePositive(Integer quantity, String message) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
}