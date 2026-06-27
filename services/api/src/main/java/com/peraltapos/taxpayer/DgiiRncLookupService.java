package com.peraltapos.taxpayer;

import com.peraltapos.common.web.BusinessException;
import com.peraltapos.crm.customer.CustomerFiscalProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;

@Service
public class DgiiRncLookupService {

    private final DgiiRncRepository dgiiRncRepository;

    public DgiiRncLookupService(DgiiRncRepository dgiiRncRepository) {
        this.dgiiRncRepository = dgiiRncRepository;
    }

    @Transactional(readOnly = true)
    public DgiiRncResponse findByRnc(String rawRnc) {
        String rnc = cleanRnc(rawRnc);

        if (!isValidDominicanTaxId(rnc)) {
            throw new BusinessException("RNC/Cedula invalido");
        }

        DgiiRnc record = dgiiRncRepository.findById(rnc)
                .orElseThrow(() -> new BusinessException("RNC/Cedula no encontrado en la base local de DGII"));

        return DgiiRncResponse.from(record, fiscalProfileFor(record));
    }

    private CustomerFiscalProfile fiscalProfileFor(DgiiRnc record) {
        String searchableText = normalize(String.join(" ",
                text(record.getCategoria()),
                text(record.getRegimenPago()),
                text(record.getActividadEconomica()),
                text(record.getAdministracionLocal())
        ));

        if (searchableText.contains("ZONA FRANCA")) {
            return CustomerFiscalProfile.FREE_ZONE;
        }

        if (searchableText.contains("GOBIERNO")
                || searchableText.contains("GUBERNAMENTAL")
                || searchableText.contains("ESTATAL")
                || searchableText.contains("INSTITUCION PUBLICA")) {
            return CustomerFiscalProfile.GOVERNMENT;
        }

        if (searchableText.contains("REGIMEN ESPECIAL")
                || searchableText.contains("EXENTO")
                || searchableText.contains("EXENCION")) {
            return CustomerFiscalProfile.SPECIAL_REGIME;
        }

        return record.getRnc().length() == 9
                ? CustomerFiscalProfile.TAX_CREDIT
                : CustomerFiscalProfile.STANDARD;
    }

    private String cleanRnc(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    private boolean isValidDominicanTaxId(String value) {
        return value.length() == 9 || value.length() == 11;
    }

    private String text(String value) {
        return value == null ? "" : value;
    }

    private String normalize(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase();
    }
}
