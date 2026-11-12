package co.improsvita.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class VisitRequest {

    private LocalDate visitDate;

    @NotNull(message = "La cantidad estimada de plántulas aptas es obligatoria")
    @Min(value = 0, message = "La cantidad estimada de plántulas aptas debe ser mayor o igual a cero")
    private Integer suitableEstimated;
    private String visitMethod;
    private String result;
    @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
    private String notes;
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
