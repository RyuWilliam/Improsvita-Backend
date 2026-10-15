package co.improsvita.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SowingReviewRequest {

    private LocalDate reviewDate;
    private BigDecimal germinatedQuantity;
    private String notes;

    public SowingReviewRequest() {
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public BigDecimal getGerminatedQuantity() {
        return germinatedQuantity;
    }

    public void setGerminatedQuantity(BigDecimal germinatedQuantity) {
        this.germinatedQuantity = germinatedQuantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}