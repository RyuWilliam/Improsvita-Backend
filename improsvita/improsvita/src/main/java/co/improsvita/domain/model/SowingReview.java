package co.improsvita.domain.model;

import java.time.LocalDate;

public class SowingReview {

    private Integer id;
    private Integer sowingId;
    private LocalDate reviewDate;
    private Integer germinatedQuantity;
    private String notes;

    public SowingReview() {
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

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Integer getGerminatedQuantity() {
        return germinatedQuantity;
    }

    public void setGerminatedQuantity(Integer germinatedQuantity) {
        this.germinatedQuantity = germinatedQuantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}