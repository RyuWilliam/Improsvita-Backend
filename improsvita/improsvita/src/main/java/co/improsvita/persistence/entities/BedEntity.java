package co.improsvita.persistence.entities;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "beds")
public class BedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bed_id")
    private Integer bedId;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "max_capacity", precision = 19, scale = 4)
    private BigDecimal maxCapacity;

    @OneToMany(mappedBy = "bed")
    private List<SowingEntity> sowings;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(nullable = false)
    private Boolean active = true;

    public BedEntity() {
    }

    public BedEntity(String code, BigDecimal maxCapacity) {
        this.code = code;
        this.maxCapacity = maxCapacity;
        this.active = true;
    }

    public Integer getBedId() {
        return bedId;
    }

    public void setBedId(Integer bedId) {
        this.bedId = bedId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(BigDecimal maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public List<SowingEntity> getSowings() {
        return sowings;
    }

    public void setSowings(List<SowingEntity> sowings) {
        this.sowings = sowings;
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