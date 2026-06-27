package com.peraltapos.purchasing;

import com.peraltapos.common.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/purchases/invoices")
public class PurchaseInvoiceController {

    private final PurchaseInvoiceService purchaseInvoiceService;

    public PurchaseInvoiceController(PurchaseInvoiceService purchaseInvoiceService) {
        this.purchaseInvoiceService = purchaseInvoiceService;
    }

    @GetMapping
    public ApiResponse<List<PurchaseInvoiceResponse>> list(@RequestParam(required = false) String search) {
        return ApiResponse.ok(purchaseInvoiceService.list(search));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PurchaseInvoiceResponse> create(@Valid @RequestBody PurchaseInvoiceRequest request) {
        return ApiResponse.created(purchaseInvoiceService.create(request));
    }
}
