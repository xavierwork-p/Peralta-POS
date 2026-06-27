package com.peraltapos.fiscal;

import com.peraltapos.common.web.BusinessException;
import com.peraltapos.crm.customer.Customer;
import com.peraltapos.crm.customer.CustomerFiscalProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class NcfService {

    private final NcfSequenceRepository ncfSequenceRepository;

    public NcfService(NcfSequenceRepository ncfSequenceRepository) {
        this.ncfSequenceRepository = ncfSequenceRepository;
    }

    @Transactional
    public IssuedNcf issueForSale(Customer customer, String customerFiscalId) {
        FiscalDocumentType documentType = resolveSaleDocumentType(customer, customerFiscalId);
        NcfSequence sequence = ncfSequenceRepository.findFirstByDocumentTypeAndActiveTrueOrderByValidUntilAsc(documentType)
                .orElseThrow(() -> new BusinessException("No hay secuencia NCF activa para " + documentType));

        if (sequence.getValidUntil().isBefore(LocalDate.now())) {
            throw new BusinessException("La secuencia NCF " + sequence.getPrefix() + " esta vencida");
        }

        if (sequence.getCurrentNumber() > sequence.getEndNumber()) {
            throw new BusinessException("La secuencia NCF " + sequence.getPrefix() + " esta agotada");
        }

        return new IssuedNcf(documentType, sequence.issueNext());
    }

    @Transactional(readOnly = true)
    public List<NcfSequenceResponse> activeSequences() {
        return ncfSequenceRepository.findByActiveTrueOrderByDocumentTypeAscValidUntilAsc()
                .stream()
                .map(NcfSequenceResponse::from)
                .toList();
    }

    private FiscalDocumentType resolveSaleDocumentType(Customer customer, String customerFiscalId) {
        if (customer != null) {
            if (customer.getFiscalProfile() == CustomerFiscalProfile.TAX_CREDIT) {
                return FiscalDocumentType.CREDITO_FISCAL;
            }
            if (customer.getFiscalProfile() == CustomerFiscalProfile.GOVERNMENT) {
                return FiscalDocumentType.GUBERNAMENTAL;
            }
            if (customer.getFiscalProfile() == CustomerFiscalProfile.SPECIAL_REGIME) {
                return FiscalDocumentType.REGIMEN_ESPECIAL;
            }
        }

        if (customerFiscalId != null && !customerFiscalId.isBlank() && customerFiscalId.trim().length() == 9) {
            return FiscalDocumentType.CREDITO_FISCAL;
        }

        return FiscalDocumentType.CONSUMO;
    }

    public record IssuedNcf(FiscalDocumentType documentType, String ncf) {
    }
}
