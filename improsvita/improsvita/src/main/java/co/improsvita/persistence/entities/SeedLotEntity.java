package co.improsvita.persistence.entities;

import co.improsvita.persistence.enums.SeedLotStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "seed_lots")
public class SeedLotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lot_id")
    private Integer lotId;

    @Column(name = "lot_number", nullable = false)
    private Integer lotNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seed_id", nullable = false)
    private SeedEntity seed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private SupplierEntity supplier;

    @Column(name = "entry_date")
    private LocalDate entryDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "initial_quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal initialQuantity;

    @Column(name = "available_quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal availableQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeedLotStatus status;

    @OneToMany(mappedBy = "seedLot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionMovementEntity> movements;

    public SeedLotEntity() {
    }

    public Integer getLotId() {
        return lotId;
    }

    public void setLotId(Integer lotId) {
        this.lotId = lotId;
    }

    public Integer getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(Integer lotNumber) {
        this.lotNumber = lotNumber;
    }

    public SeedEntity getSeed() {
        return seed;
    }

    public void setSeed(SeedEntity seed) {
        this.seed = seed;
    }

    public SupplierEntity getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierEntity supplier) {
        this.supplier = supplier;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(BigDecimal initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(BigDecimal availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public SeedLotStatus getStatus() {
        return status;
    }

    public void setStatus(SeedLotStatus status) {
        this.status = status;
    }

    public List<TransactionMovementEntity> getMovements() {
        return movements;
    }

    public void setMovements(List<TransactionMovementEntity> movements) {
        this.movements = movements;
    }
}
