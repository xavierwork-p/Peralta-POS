package com.peraltapos.taxpayer;

import com.peraltapos.common.web.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dgii/rnc")
public class DgiiRncController {

    private final DgiiRncLookupService dgiiRncLookupService;
    private final DgiiRncImportService dgiiRncImportService;

    public DgiiRncController(DgiiRncLookupService dgiiRncLookupService, DgiiRncImportService dgiiRncImportService) {
        this.dgiiRncLookupService = dgiiRncLookupService;
        this.dgiiRncImportService = dgiiRncImportService;
    }

    @GetMapping("/{rnc}")
    public ApiResponse<DgiiRncResponse> findByRnc(@PathVariable String rnc) {
        return ApiResponse.ok(dgiiRncLookupService.findByRnc(rnc));
    }

    @PostMapping("/import")
    public ApiResponse<DgiiRncImportSummary> importFromDgii() {
        return ApiResponse.ok(dgiiRncImportService.importFromDgii());
    }
}
