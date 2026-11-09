package co.improsvita.persistence.mapper;

import co.improsvita.domain.model.Sale;
import co.improsvita.persistence.entities.ContactEntity;
import co.improsvita.persistence.entities.ReservationEntity;
import co.improsvita.persistence.entities.SaleEntity;
import co.improsvita.persistence.entities.SowingEntity;

public class SaleMapper {

    private SaleMapper() {
    }

    public static Sale toDomain(SaleEntity entity) {
        if (entity == null) return null;
        Sale sale = new Sale();
        sale.setId(entity.getSaleId());
        sale.setContactId(entity.getContact() != null ? entity.getContact().getContactId() : null);
        sale.setSowingId(entity.getSowing() != null ? entity.getSowing().getSowingId() : null);
        sale.setReservationId(entity.getReservation() != null
                ? entity.getReservation().getReservationId() : null);
        sale.setSaleDate(entity.getSaleDate());
        sale.setQuantity(entity.getQuantity());
        sale.setUnitPrice(entity.getUnitPrice());
        sale.setTotal(entity.getTotal());
        sale.setSaleType(mapType(entity.getSaleType()));
        sale.setNotes(entity.getNotes());
        sale.setActive(entity.getActive());
        return sale;
    }

    public static SaleEntity toEntity(Sale domain, ContactEntity contact, SowingEntity sowing,
                                      ReservationEntity reservation) {
        if (domain == null) return null;
        SaleEntity entity = new SaleEntity();
        entity.setSaleId(domain.getId());
        entity.setContact(contact);
        entity.setSowing(sowing);
        entity.setReservation(reservation);
        entity.setSaleDate(domain.getSaleDate());
        entity.setQuantity(domain.getQuantity());
        entity.setUnitPrice(domain.getUnitPrice());
        entity.setTotal(domain.getTotal());
        entity.setSaleType(mapType(domain.getSaleType()));
        entity.setNotes(domain.getNotes());
        entity.setActive(domain.getActive());
        return entity;
    }

    private static co.improsvita.domain.model.SaleType mapType(
            co.improsvita.persistence.enums.SaleType entityType) {
        if (entityType == null) return null;
        return co.improsvita.domain.model.SaleType.valueOf(entityType.name());
    }

    private static co.improsvita.persistence.enums.SaleType mapType(
            co.improsvita.domain.model.SaleType domainType) {
        if (domainType == null) return null;
        return co.improsvita.persistence.enums.SaleType.valueOf(domainType.name());
    }
}
