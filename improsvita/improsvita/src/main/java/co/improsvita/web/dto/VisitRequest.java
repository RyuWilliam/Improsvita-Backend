package co.improsvita.web.dto;

import java.time.LocalDate;

public class VisitRequest {

    private LocalDate visitDate;
    private Integer suitableEstimated;
    private String visitMethod;
    private String result;
    private Boolean active;

    public VisitRequest() {
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

    public String getVisitMethod() {
        return visitMethod;
    }

    public void setVisitMethod(String visitMethod) {
        this.visitMethod = visitMethod;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
