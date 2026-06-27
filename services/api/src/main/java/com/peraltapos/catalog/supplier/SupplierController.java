package com.peraltapos.catalog.supplier;

import com.peraltapos.common.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ApiResponse<List<SupplierResponse>> list(@RequestParam(required = false) String search) {
        return ApiResponse.ok(supplierService.list(search));
    }

    @GetMapping("/{id}")
    public ApiResponse<SupplierResponse> get(@PathVariable UUID id) {
        return ApiResponse.ok(supplierService.get(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SupplierResponse> create(@Valid @RequestBody SupplierRequest request) {
        return ApiResponse.created(supplierService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<SupplierResponse> update(@PathVariable UUID id, @Valid @RequestBody SupplierRequest request) {
        return ApiResponse.ok(supplierService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<SupplierResponse> deactivate(@PathVariable UUID id) {
        return ApiResponse.ok(supplierService.deactivate(id));
    }
}
