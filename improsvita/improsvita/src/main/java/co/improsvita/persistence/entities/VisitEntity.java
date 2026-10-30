package co.improsvita.persistence.entities;

import co.improsvita.persistence.enums.VisitMethod;
import co.improsvita.persistence.enums.VisitResult;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "visits", indexes = {@Index(columnList = "sowing_id")})
public class VisitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_id")
    private Integer visitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sowing_id", nullable = false)
    private SowingEntity sowing;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(name = "suitable_estimated", nullable = false)
    private Integer suitableEstimated;

    @Enumerated(EnumType.STRING)
    @Column(name = "visit_method", nullable = false)
    private VisitMethod visitMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitResult result;

    @Column(nullable = false)
    private Boolean active = true;

    public VisitEntity() {
    }

    public Integer getVisitId() {
        return visitId;
    }

    public void setVisitId(Integer visitId) {
        this.visitId = visitId;
    }

    public SowingEntity getSowing() {
        return sowing;
    }

    public void setSowing(SowingEntity sowing) {
        this.sowing = sowing;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    public Integer getSuitableEstimated() {
        return suitableEstimated;
    }

    public void setSuitableEstimated(Integer suitableEstimated) {
        this.suitableEstimated = suitableEstimated;
    }

    public VisitMethod getVisitMethod() {
        return visitMethod;
    }

    public void setVisitMethod(VisitMethod visitMethod) {
        this.visitMethod = visitMethod;
    }

    public VisitResult getResult() {
        return result;
    }

    public void setResult(VisitResult result) {
        this.result = result;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
