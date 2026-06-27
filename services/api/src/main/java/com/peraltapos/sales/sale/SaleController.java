package com.peraltapos.sales.sale;

import com.peraltapos.common.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping
    public ApiResponse<List<SaleResponse>> list(@RequestParam(required = false) String search) {
        return ApiResponse.ok(saleService.list(search));
    }

    @GetMapping("/{id}")
    public ApiResponse<SaleResponse> get(@PathVariable UUID id) {
        return ApiResponse.ok(saleService.get(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SaleResponse> create(@Valid @RequestBody SaleRequest request) {
        return ApiResponse.created(saleService.create(request));
    }
}
