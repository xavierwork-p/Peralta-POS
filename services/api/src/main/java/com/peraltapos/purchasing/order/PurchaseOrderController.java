package com.peraltapos.purchasing.order;

import com.peraltapos.common.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/purchases/orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @GetMapping
    public ApiResponse<List<PurchaseOrderResponse>> list(@RequestParam(required = false) String search) {
        return ApiResponse.ok(purchaseOrderService.list(search));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PurchaseOrderResponse> create(@Valid @RequestBody PurchaseOrderRequest request) {
        return ApiResponse.created(purchaseOrderService.create(request));
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<PurchaseOrderResponse> cancel(@PathVariable UUID id) {
        return ApiResponse.ok(purchaseOrderService.cancel(id));
    }
}
