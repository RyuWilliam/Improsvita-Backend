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
import co.improsvita.domain.validation.DateRangeValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private final DateRangeValidator dateRangeValidator;

    public InventoryService(SeedRepository seedRepository,
                            SupplierRepository supplierRepository,
                            LocationRepository locationRepository,
                            SeedLotRepository seedLotRepository,
                            SeedMovementRepository seedMovementRepository,
                            SeedSupplierRepository seedSupplierRepository,
                            SowingRepository sowingRepository,
                            BedRepository bedRepository,
                            DateRangeValidator dateRangeValidator) {
        this.seedRepository = seedRepository;
        this.supplierRepository = supplierRepository;
        this.locationRepository = locationRepository;
        this.seedLotRepository = seedLotRepository;
        this.seedMovementRepository = seedMovementRepository;
        this.seedSupplierRepository = seedSupplierRepository;
        this.sowingRepository = sowingRepository;
        this.bedRepository = bedRepository;
        this.dateRangeValidator = dateRangeValidator;
    }

    @Transactional
    public SeedLot registerEntry(Integer seedId, Integer supplierId, Integer locationId,
                                 Integer lotNumber, BigDecimal quantity,
                                 LocalDate entryDate, LocalDate dueDate) {
        requirePositive(quantity, "La cantidad de entrada debe ser mayor a cero");

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
        lot.setEntryDate(entryDate != null ? entryDate : LocalDate.now());
        lot.setDueDate(dueDate);
        lot.setInitialQuantity(quantity);
        lot.setAvailableQuantity(quantity);
        lot.setStatus(SeedLotStatus.AVAILABLE);
        SeedLot saved = seedLotRepository.save(lot);

        recordMovement(saved.getId(), bridge.getSupplierId(), MovementType.ENTRY, quantity, "Entrada lote " + lotNumber);

        return saved;
    }

    @Transactional
    public SeedLot registerExit(Integer lotId, BigDecimal quantity, String reason) {
        requirePositive(quantity, "La cantidad de salida debe ser mayor a cero");

        SeedLot lot = requireLot(lotId);
        if (lot.getAvailableQuantity().compareTo(quantity) < 0) {
            throw new IllegalStateException("Stock insuficiente en el lote " + lotId
                    + ": disponible " + lot.getAvailableQuantity() + ", solicitado " + quantity);
        }

        recordMovement(lot.getId(), null, MovementType.EXIT, quantity, reason);

        lot.setAvailableQuantity(lot.getAvailableQuantity().subtract(quantity));
        if (lot.getAvailableQuantity().compareTo(BigDecimal.ZERO) == 0) {
            lot.setStatus(SeedLotStatus.DEPLETED);
        }
        return seedLotRepository.save(lot);
    }

    @Transactional
    public SeedLot adjustStock(Integer lotId, BigDecimal quantity, String reason) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("La cantidad del ajuste debe ser distinta de cero (positiva suma, negativa resta)");
        }

        SeedLot lot = requireLot(lotId);
        BigDecimal updated = lot.getAvailableQuantity().add(quantity);
        if (updated.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("El ajuste deja el lote " + lotId + " en negativo: " + updated);
        }

        recordMovement(lot.getId(), null, MovementType.ADJUSTMENT, quantity, reason);

        lot.setAvailableQuantity(updated);
        if (updated.compareTo(BigDecimal.ZERO) == 0) {
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
    public Sowing sow(Integer lotId, Integer bedId, BigDecimal quantity,
                      LocalDate sowingDate, LocalDate expectedGerminationDate, String notes) {
        requirePositive(quantity, "La cantidad sembrada debe ser mayor a cero");

        LocalDate effectiveSowingDate = sowingDate != null ? sowingDate : LocalDate.now();
        dateRangeValidator.validateOptionalEnd(effectiveSowingDate, expectedGerminationDate,
                "fecha de siembra", "fecha estimada de germinación");

        SeedLot lot = requireLot(lotId);
        Bed bed = bedRepository.getById(bedId);
        if (bed == null) {
            throw new IllegalArgumentException("Cama no encontrada: " + bedId);
        }
        if (Boolean.FALSE.equals(bed.getActive())) {
            throw new IllegalStateException("La cama " + bed.getCode() + " está inhabilitada");
        }
        if (lot.getAvailableQuantity().compareTo(quantity) < 0) {
            throw new IllegalStateException("Stock insuficiente en el lote " + lotId
                    + ": disponible " + lot.getAvailableQuantity() + ", solicitado " + quantity);
        }

        recordMovement(lot.getId(), null, MovementType.EXIT, quantity, "Siembra en cama " + bed.getCode());

        lot.setAvailableQuantity(lot.getAvailableQuantity().subtract(quantity));
        if (lot.getAvailableQuantity().compareTo(BigDecimal.ZERO) == 0) {
            lot.setStatus(SeedLotStatus.DEPLETED);
        }
        seedLotRepository.save(lot);

        Sowing sowing = new Sowing();
        sowing.setLotId(lot.getId());
        sowing.setBedId(bed.getId());
        sowing.setQuantitySown(quantity);
        sowing.setSowingDate(effectiveSowingDate);
        sowing.setExpectedGerminationDate(expectedGerminationDate);
        sowing.setStatus(SowingStatus.IN_PROGRESS);
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

    public BigDecimal getStockBySeedId(Integer seedId) {
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

    /** Único punto donde se registra un movimiento de inventario (kardex). */
    private void recordMovement(Integer lotId, Integer supplierId, MovementType type,
                                BigDecimal quantity, String reason) {
        SeedMovement movement = new SeedMovement();
        movement.setLotId(lotId);
        movement.setSupplierId(supplierId);
        movement.setMovementType(type);
        movement.setQuantity(quantity);
        movement.setMovementDate(LocalDateTime.now());
        movement.setReason(reason);
        seedMovementRepository.save(movement);
    }

    private void requirePositive(BigDecimal quantity, String message) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
}