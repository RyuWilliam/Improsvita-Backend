package co.improsvita.persistence.entities;

import co.improsvita.persistence.enums.SowingStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sowings",
        indexes = {@Index(columnList = "lot_id"), @Index(columnList = "bed_id"),
                @Index(columnList = "status"), @Index(columnList = "sowing_date")})
public class SowingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sowing_id")
    private Integer sowingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private SeedLotEntity seedLot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bed_id", nullable = false)
    private BedEntity bed;

    @Column(name = "quantity_sown", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantitySown;

    @Column(name = "germinated_quantity", precision = 19, scale = 4)
    private BigDecimal germinatedQuantity;

    @Column(name = "sowing_date", nullable = false)
    private LocalDate sowingDate;

    @Column(name = "expected_germination_date")
    private LocalDate expectedGerminationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SowingStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private Boolean active = true;

    public SowingEntity() {
    }

    public Integer getSowingId() {
        return sowingId;
    }

    public void setSowingId(Integer sowingId) {
        this.sowingId = sowingId;
    }

    public SeedLotEntity getSeedLot() {
        return seedLot;
    }

    public void setSeedLot(SeedLotEntity seedLot) {
        this.seedLot = seedLot;
    }

    public BedEntity getBed() {
        return bed;
    }

    public void setBed(BedEntity bed) {
        this.bed = bed;
    }

    public BigDecimal getQuantitySown() {
        return quantitySown;
    }

    public void setQuantitySown(BigDecimal quantitySown) {
        this.quantitySown = quantitySown;
    }

    public BigDecimal getGerminatedQuantity() {
        return germinatedQuantity;
    }

    public void setGerminatedQuantity(BigDecimal germinatedQuantity) {
        this.germinatedQuantity = germinatedQuantity;
    }

    public LocalDate getSowingDate() {
        return sowingDate;
    }

    public void setSowingDate(LocalDate sowingDate) {
        this.sowingDate = sowingDate;
    }

    public LocalDate getExpectedGerminationDate() {
        return expectedGerminationDate;
    }

    public void setExpectedGerminationDate(LocalDate expectedGerminationDate) {
        this.expectedGerminationDate = expectedGerminationDate;
    }

    public SowingStatus getStatus() {
        return status;
    }

    public void setStatus(SowingStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}