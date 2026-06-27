package com.peraltapos.company;

import com.peraltapos.common.web.BusinessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional(readOnly = true)
    public CompanyResponse profile() {
        return CompanyResponse.from(findCompany());
    }

    @Transactional
    public CompanyResponse update(CompanyRequest request) {
        Company company = findCompany();
        company.updateFrom(request);
        return CompanyResponse.from(companyRepository.save(company));
    }

    private Company findCompany() {
        return companyRepository.findByOrderByCreatedAtAsc(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException("No hay empresa configurada"));
    }
}
