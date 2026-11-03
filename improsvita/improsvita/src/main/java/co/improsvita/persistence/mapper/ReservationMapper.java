package co.improsvita.persistence.mapper;

import co.improsvita.domain.model.Reservation;
import co.improsvita.persistence.entities.ClientEntity;
import co.improsvita.persistence.entities.ReservationEntity;
import co.improsvita.persistence.entities.SowingEntity;

public class ReservationMapper {

    private ReservationMapper() {
    }

    public static Reservation toDomain(ReservationEntity entity) {
        if (entity == null) return null;
        Reservation reservation = new Reservation();
        reservation.setId(entity.getReservationId());
        reservation.setSowingId(entity.getSowing() != null ? entity.getSowing().getSowingId() : null);
        reservation.setClientId(entity.getClient() != null ? entity.getClient().getClientId() : null);
        reservation.setReservationDate(entity.getReservationDate());
        reservation.setTotalPrice(entity.getTotalPrice());
        reservation.setReservationStatus(mapStatus(entity.getReservationStatus()));
        reservation.setNotes(entity.getNotes());
        reservation.setActive(entity.getActive());
        return reservation;
    }

    public static ReservationEntity toEntity(Reservation domain, SowingEntity sowing, ClientEntity client) {
        if (domain == null) return null;
        ReservationEntity entity = new ReservationEntity();
        entity.setReservationId(domain.getId());
        entity.setSowing(sowing);
        entity.setClient(client);
        entity.setReservationDate(domain.getReservationDate());
        entity.setTotalPrice(domain.getTotalPrice());
        entity.setReservationStatus(mapStatus(domain.getReservationStatus()));
        entity.setNotes(domain.getNotes());
        entity.setActive(domain.getActive());
        return entity;
    }

    private static co.improsvita.domain.model.ReservationStatus mapStatus(
            co.improsvita.persistence.enums.ReservationStatus entityStatus) {
        if (entityStatus == null) return null;
        return co.improsvita.domain.model.ReservationStatus.valueOf(entityStatus.name());
    }

    private static co.improsvita.persistence.enums.ReservationStatus mapStatus(
            co.improsvita.domain.model.ReservationStatus domainStatus) {
        if (domainStatus == null) return null;
        return co.improsvita.persistence.enums.ReservationStatus.valueOf(domainStatus.name());
    }
}
