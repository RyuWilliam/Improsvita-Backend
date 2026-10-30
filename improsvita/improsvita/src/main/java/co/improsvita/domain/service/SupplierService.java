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
        assertEmailAvailable(supplier.getEmail(), null);
        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(Supplier supplier) {
        assertEmailAvailable(supplier.getEmail(), supplier.getId());
        return supplierRepository.save(supplier);
    }

    public void deleteSupplierById(Integer id) {
        supplierRepository.deleteById(id);
    }

    /**
     * Regla de negocio en el servicio, no en el @Repository: si se lanza desde
     * un bean de persistencia, Spring la envuelve en una DataAccessException.
     */
    private void assertEmailAvailable(String email, Integer ownId) {
        if (email == null || email.isBlank()) {
            return;
        }
        Supplier existing = supplierRepository.getByEmail(email);
        if (existing != null && !existing.getId().equals(ownId)) {
            throw new IllegalStateException("Ya existe un proveedor con el email " + email);
        }
    }
}
