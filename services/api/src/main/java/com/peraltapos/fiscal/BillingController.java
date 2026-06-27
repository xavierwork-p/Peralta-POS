package com.peraltapos.fiscal;

import com.peraltapos.common.web.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final NcfService ncfService;
    private final EcfSimulationService ecfSimulationService;

    public BillingController(NcfService ncfService, EcfSimulationService ecfSimulationService) {
        this.ncfService = ncfService;
        this.ecfSimulationService = ecfSimulationService;
    }

    @GetMapping("/ncf-sequences")
    public ApiResponse<List<NcfSequenceResponse>> activeSequences() {
        return ApiResponse.ok(ncfService.activeSequences());
    }

    @PostMapping("/ecf/sales/{saleId}/generate")
    public ApiResponse<EcfDocumentResponse> generateEcf(@PathVariable UUID saleId) {
        return ApiResponse.ok(ecfSimulationService.generate(saleId));
    }

    @PostMapping("/ecf/sales/{saleId}/submit-simulated")
    public ApiResponse<EcfDocumentResponse> submitEcfSimulated(@PathVariable UUID saleId) {
        return ApiResponse.ok(ecfSimulationService.submitSimulated(saleId));
    }

    @GetMapping("/ecf/sales/{saleId}")
    public ApiResponse<EcfDocumentResponse> getEcf(@PathVariable UUID saleId) {
        return ApiResponse.ok(ecfSimulationService.get(saleId));
    }
}
