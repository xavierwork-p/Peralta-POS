package com.peraltapos.taxpayer;

import com.peraltapos.common.web.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/taxpayers")
public class TaxpayerLookupController {

    private final TaxpayerLookupService taxpayerLookupService;

    public TaxpayerLookupController(TaxpayerLookupService taxpayerLookupService) {
        this.taxpayerLookupService = taxpayerLookupService;
    }

    @GetMapping("/rnc/{rnc}")
    public ApiResponse<TaxpayerLookupResponse> findByRnc(@PathVariable String rnc) {
        return ApiResponse.ok(taxpayerLookupService.findByRnc(rnc));
    }
}
