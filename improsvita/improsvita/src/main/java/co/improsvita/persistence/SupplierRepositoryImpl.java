package co.improsvita.persistence;

import co.improsvita.domain.model.Supplier;
import co.improsvita.domain.repository.SupplierRepository;
import co.improsvita.persistence.crud.SupplierJpaRepository;
import co.improsvita.persistence.mapper.ProviderMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SupplierRepositoryImpl implements SupplierRepository {

    private final SupplierJpaRepository supplierJpaRepository;

    public SupplierRepositoryImpl(SupplierJpaRepository supplierJpaRepository) {
        this.supplierJpaRepository = supplierJpaRepository;
    }

    @Override
    public List<Supplier> getAll() {
        return supplierJpaRepository.findAll()
                .stream()
                .map(ProviderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Supplier getById(Integer id) {
        return supplierJpaRepository.findById(id)
                .map(ProviderMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Supplier getByName(String name) {
        return supplierJpaRepository.findByName(name)
                .map(ProviderMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Supplier save(Supplier supplier) {
        var entity = ProviderMapper.toEntity(supplier);
        var saved = supplierJpaRepository.save(entity);
        return ProviderMapper.toDomain(saved);
    }

    @Override
    public void deleteByName(String name) {
        supplierJpaRepository.findByName(name)
                .ifPresent(supplierJpaRepository::delete);
    }

    @Override
    public void deleteById(Integer id) {
        supplierJpaRepository.deleteById(id);
    }
}
