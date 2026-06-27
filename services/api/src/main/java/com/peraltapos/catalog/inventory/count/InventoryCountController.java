package com.peraltapos.catalog.inventory.count;

import com.peraltapos.common.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/counts")
public class InventoryCountController {

    private final InventoryCountService inventoryCountService;

    public InventoryCountController(InventoryCountService inventoryCountService) {
        this.inventoryCountService = inventoryCountService;
    }

    @GetMapping
    public ApiResponse<List<InventoryCountResponse>> list() {
        return ApiResponse.ok(inventoryCountService.list());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<InventoryCountResponse> create(@Valid @RequestBody InventoryCountRequest request) {
        return ApiResponse.created(inventoryCountService.create(request));
    }
}
