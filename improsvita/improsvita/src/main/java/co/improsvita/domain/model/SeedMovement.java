package co.improsvita.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SeedMovement {

    private Integer id;
    private Integer lotId;
    private Integer supplierId;
    private MovementType movementType;
    private BigDecimal quantity;
    private LocalDateTime movementDate;
    private String reason;

    public SeedMovement() {
    }

    public SeedMovement(Integer id, Integer lotId, Integer supplierId,
                        MovementType movementType, BigDecimal quantity,
                        LocalDateTime movementDate, String reason) {
        this.id = id;
        this.lotId = lotId;
        this.supplierId = supplierId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.movementDate = movementDate;
        this.reason = reason;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLotId() {
        return lotId;
    }

    public void setLotId(Integer lotId) {
        this.lotId = lotId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getMovementDate() {
        return movementDate;
    }

    public void setMovementDate(LocalDateTime movementDate) {
        this.movementDate = movementDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}