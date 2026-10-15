package co.improsvita.persistence.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "sowing_reviews", indexes = {@Index(columnList = "sowing_id")})
public class SowingReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sowing_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SowingEntity sowing;

    @Column(name = "review_date", nullable = false)
    private LocalDate reviewDate;

    @Column(name = "germinated_quantity", nullable = false)
    private Integer germinatedQuantity;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public SowingReviewEntity() {
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public SowingEntity getSowing() {
        return sowing;
    }

    public void setSowing(SowingEntity sowing) {
        this.sowing = sowing;
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