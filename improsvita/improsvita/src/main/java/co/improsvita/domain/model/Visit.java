package co.improsvita.domain.model;

import java.time.LocalDate;

public class Visit {

    private Integer id;
    private Integer sowingId;
    private LocalDate visitDate;
    private Integer suitableEstimated;
    private VisitMethod visitMethod;
    private VisitResult result;
    private Boolean active;

    public Visit() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSowingId() {
        return sowingId;
    }

    public void setSowingId(Integer sowingId) {
        this.sowingId = sowingId;
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
