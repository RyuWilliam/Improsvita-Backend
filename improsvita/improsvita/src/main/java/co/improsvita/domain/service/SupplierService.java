package co.improsvita.domain.service;

import co.improsvita.domain.model.Supplier;
import co.improsvita.domain.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.getAll();
    }

    public Supplier getSupplierById(Integer id) {
        return supplierRepository.getById(id);
    }

    public Supplier getSupplierByName(String name) {
        return supplierRepository.getByName(name);
    }

    public Supplier createSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public void deleteSupplierByName(String name) {
        supplierRepository.deleteByName(name);
    }

    public void deleteSupplierById(Integer id) {
        supplierRepository.deleteById(id);
    }
}
