package co.improsvita.persistence.entities;

import co.improsvita.persistence.enums.SeedType;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "seeds")
public class SeedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seed_id")
    private Integer seedId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "seed_type")
    private SeedType type;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "seed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeedSupplierEntity> seedSuppliers;

    @OneToMany(mappedBy = "seed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeedLotEntity> lots;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(nullable = false)
    private Boolean active = true;

    public SeedEntity() {
    }

    public SeedEntity(Integer seedId, String name, SeedType type, String description, LocalDateTime createdDate, LocalDateTime lastUpdated, Boolean active) {
        this.seedId = seedId;
        this.name = name;
        this.type = type;
        this.description = description;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
        this.active = active;
    }

    public Integer getSeedId() {
        return seedId;
    }

    public void setSeedId(Integer seedId) {
        this.seedId = seedId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SeedType getType() {
        return type;
    }

    public void setType(SeedType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SeedSupplierEntity> getSeedSuppliers() {
        return seedSuppliers;
    }

    public void setSeedSuppliers(List<SeedSupplierEntity> seedSuppliers) {
        this.seedSuppliers = seedSuppliers;
    }

    public List<SeedLotEntity> getLots() {
        return lots;
    }

    public void setLots(List<SeedLotEntity> lots) {
        this.lots = lots;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}