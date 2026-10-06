package co.improsvita.persistence.entities;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "seeds_suppliers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"seed_id", "supplier_id"}),
        indexes = {@Index(columnList = "seed_id"), @Index(columnList = "supplier_id")})
public class SeedSupplierEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seed_provider_id")
    private Integer seedProviderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seed_id", nullable = false)
    private SeedEntity seed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private SupplierEntity supplier;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private Boolean active = true;

    public SeedSupplierEntity() {
    }

    public SeedSupplierEntity(SeedEntity seed, SupplierEntity supplier) {
        this.seed = seed;
        this.supplier = supplier;
        this.active = true;
    }

    public Integer getSeedProviderId() {
        return seedProviderId;
    }

    public void setSeedProviderId(Integer seedProviderId) {
        this.seedProviderId = seedProviderId;
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}