package co.improsvita.persistence.mapper;

import co.improsvita.domain.model.Supplier;
import co.improsvita.persistence.entities.SupplierEntity;

public class ProviderMapper {

    private ProviderMapper() {
    }

    public static Supplier toDomain(SupplierEntity entity) {
        if (entity == null) return null;
        Supplier supplier = new Supplier();
        supplier.setId(entity.getSupplierId());
        supplier.setName(entity.getName());
        supplier.setPhone(entity.getPhone());
        supplier.setEmail(entity.getEmail());
        supplier.setActive(entity.getActive());
        return supplier;
    }

    public static SupplierEntity toEntity(Supplier domain) {
        if (domain == null) return null;
        SupplierEntity entity = new SupplierEntity();
        entity.setSupplierId(domain.getId());
        entity.setName(domain.getName());
        entity.setPhone(domain.getPhone());
        entity.setEmail(domain.getEmail());
        entity.setActive(domain.getActive());
        return entity;
    }
}
