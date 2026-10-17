package co.improsvita.domain.repository;

import co.improsvita.domain.model.Supplier;

import java.util.List;

public interface SupplierRepository {
    List<Supplier> getAll();
    Supplier getById(Integer id);
    Supplier getByName(String name);
    Supplier save(Supplier supplier);
    void deleteById(Integer id);
}
