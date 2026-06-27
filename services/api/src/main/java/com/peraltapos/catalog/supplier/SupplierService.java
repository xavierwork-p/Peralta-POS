package com.peraltapos.catalog.supplier;

import com.peraltapos.common.web.BusinessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Transactional(readOnly = true)
    public List<SupplierResponse> list(String search) {
        Sort sort = Sort.by("name").ascending();
        List<Supplier> suppliers = (search == null || search.isBlank())
                ? supplierRepository.findAll(sort)
                : supplierRepository.findByNameContainingIgnoreCaseOrRncContainingIgnoreCase(search, search, sort);

        return suppliers.stream().map(SupplierResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SupplierResponse get(UUID id) {
        return SupplierResponse.from(findEntity(id));
    }

    @Transactional
    public SupplierResponse create(SupplierRequest request) {
        Supplier supplier = new Supplier();
        supplier.updateFrom(request);
        return SupplierResponse.from(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierResponse update(UUID id, SupplierRequest request) {
        Supplier supplier = findEntity(id);
        supplier.updateFrom(request);
        return SupplierResponse.from(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierResponse deactivate(UUID id) {
        Supplier supplier = findEntity(id);
        supplier.deactivate();
        return SupplierResponse.from(supplierRepository.save(supplier));
    }

    private Supplier findEntity(UUID id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Suplidor no encontrado"));
    }
}
