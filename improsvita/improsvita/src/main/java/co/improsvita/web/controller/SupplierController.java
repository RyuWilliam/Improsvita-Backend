package co.improsvita.web.controller;

import co.improsvita.domain.model.Supplier;
import co.improsvita.domain.service.SupplierService;
import co.improsvita.web.dto.SupplierRequest;
import co.improsvita.web.dto.SupplierResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ResponseEntity<List<SupplierResponse>> getAllSuppliers() {
        List<SupplierResponse> suppliers = supplierService.getAllSuppliers()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponse> getSupplierById(@PathVariable Integer id) {
        Supplier supplier = supplierService.getSupplierById(id);
        if (supplier == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(supplier));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<SupplierResponse> getSupplierByName(@PathVariable String name) {
        Supplier supplier = supplierService.getSupplierByName(name);
        if (supplier == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(supplier));
    }

    @PostMapping
    public ResponseEntity<SupplierResponse> createSupplier(@RequestBody SupplierRequest request) {
        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setPhone(request.getPhone());
        supplier.setEmail(request.getEmail());
        supplier.setActive(true);

        Supplier created = supplierService.createSupplier(supplier);
        return ResponseEntity.ok(toResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponse> updateSupplier(@PathVariable Integer id, @RequestBody SupplierRequest request) {
        Supplier existing = supplierService.getSupplierById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setName(request.getName());
        existing.setPhone(request.getPhone());
        existing.setEmail(request.getEmail());

        Supplier updated = supplierService.updateSupplier(existing);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplierById(@PathVariable Integer id) {
        supplierService.deleteSupplierById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/name/{name}")
    public ResponseEntity<Void> deleteSupplierByName(@PathVariable String name) {
        supplierService.deleteSupplierByName(name);
        return ResponseEntity.noContent().build();
    }

    private SupplierResponse toResponse(Supplier supplier) {
        SupplierResponse response = new SupplierResponse();
        response.setId(supplier.getId());
        response.setName(supplier.getName());
        response.setPhone(supplier.getPhone());
        response.setEmail(supplier.getEmail());
        return response;
    }
}
