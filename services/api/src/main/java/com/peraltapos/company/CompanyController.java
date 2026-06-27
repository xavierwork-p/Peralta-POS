package com.peraltapos.company;

import com.peraltapos.common.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/profile")
    public ApiResponse<CompanyResponse> profile() {
        return ApiResponse.ok(companyService.profile());
    }

    @PutMapping("/profile")
    public ApiResponse<CompanyResponse> update(@Valid @RequestBody CompanyRequest request) {
        return ApiResponse.ok(companyService.update(request));
    }
}
