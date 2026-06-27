package com.peraltapos.sales.quote;

import com.peraltapos.common.web.ApiResponse;
import com.peraltapos.sales.sale.SaleResponse;
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
@RequestMapping("/api/quotes")
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping
    public ApiResponse<List<QuoteResponse>> list(@RequestParam(required = false) String search) {
        return ApiResponse.ok(quoteService.list(search));
    }

    @GetMapping("/{id}")
    public ApiResponse<QuoteResponse> get(@PathVariable UUID id) {
        return ApiResponse.ok(quoteService.get(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<QuoteResponse> create(@Valid @RequestBody QuoteRequest request) {
        return ApiResponse.created(quoteService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<QuoteResponse> update(@PathVariable UUID id, @Valid @RequestBody QuoteRequest request) {
        return ApiResponse.ok(quoteService.update(id, request));
    }

    @PostMapping("/{id}/invoice")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SaleResponse> invoice(@PathVariable UUID id) {
        return ApiResponse.created(quoteService.invoice(id));
    }
}
