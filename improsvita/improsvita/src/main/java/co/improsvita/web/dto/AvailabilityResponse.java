package co.improsvita.web.dto;

/**
 * Plántulas disponibles para vender en una siembra.
 * {@code available} = última visita activa − ventas activas (nunca negativo).
 */
public class AvailabilityResponse {

    private Integer sowingId;
    private Integer seedlingsAvailable;
    private Integer soldQuantity;
    private Integer available;

    public AvailabilityResponse() {
    }

    public AvailabilityResponse(Integer sowingId, Integer seedlingsAvailable,
                                Integer soldQuantity, Integer available) {
        this.sowingId = sowingId;
        this.seedlingsAvailable = seedlingsAvailable;
        this.soldQuantity = soldQuantity;
        this.available = available;
    }

    public Integer getSowingId() {
        return sowingId;
    }

    public void setSowingId(Integer sowingId) {
        this.sowingId = sowingId;
    }

    public Integer getSeedlingsAvailable() {
        return seedlingsAvailable;
    }

    public void setSeedlingsAvailable(Integer seedlingsAvailable) {
        this.seedlingsAvailable = seedlingsAvailable;
    }

    public Integer getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(Integer soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }
}
