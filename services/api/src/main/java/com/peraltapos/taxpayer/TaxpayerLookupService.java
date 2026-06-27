package com.peraltapos.taxpayer;

import com.peraltapos.common.web.BusinessException;
import com.peraltapos.crm.customer.Customer;
import com.peraltapos.crm.customer.CustomerFiscalProfile;
import com.peraltapos.crm.customer.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TaxpayerLookupService {

    private final TaxpayerLookupProvider taxpayerLookupProvider;
    private final CustomerRepository customerRepository;
    private final DgiiRncLookupService dgiiRncLookupService;

    public TaxpayerLookupService(
            TaxpayerLookupProvider taxpayerLookupProvider,
            CustomerRepository customerRepository,
            DgiiRncLookupService dgiiRncLookupService
    ) {
        this.taxpayerLookupProvider = taxpayerLookupProvider;
        this.customerRepository = customerRepository;
        this.dgiiRncLookupService = dgiiRncLookupService;
    }

    @Transactional(readOnly = true)
    public TaxpayerLookupResponse findByRnc(String rawRnc) {
        String rnc = cleanRnc(rawRnc);

        if (rnc.length() < 9) {
            throw new BusinessException("En Republica Dominicana se consulta RNC o cedula numerica; revisa que no estes usando un RFC de otro pais");
        }

        Optional<TaxpayerLookupResponse> externalResult = taxpayerLookupProvider.findByRnc(rnc);

        if (externalResult.isPresent()) {
            return externalResult.get();
        }

        try {
            DgiiRncResponse localDgiiResult = dgiiRncLookupService.findByRnc(rnc);
            return new TaxpayerLookupResponse(
                    localDgiiResult.rnc(),
                    localDgiiResult.name(),
                    localDgiiResult.estado(),
                    localDgiiResult.fiscalProfile(),
                    localDgiiResult.source(),
                    localDgiiResult.verified()
            );
        } catch (BusinessException exception) {
            // Si no esta importado en DGII local, continuamos con clientes registrados.
        }

        return customerRepository.findByFiscalId(rnc)
                .or(() -> customerRepository.findAll()
                        .stream()
                        .filter(customer -> cleanRnc(customer.getFiscalId()).equals(rnc))
                        .findFirst())
                .map(this::fromCustomer)
                .orElseThrow(() -> new BusinessException(notFoundMessage()));
    }

    private TaxpayerLookupResponse fromCustomer(Customer customer) {
        return new TaxpayerLookupResponse(
                customer.getFiscalId(),
                customer.getName(),
                customer.isActive() ? "ACTIVO" : "INACTIVO",
                customer.getFiscalProfile() == null ? CustomerFiscalProfile.STANDARD : customer.getFiscalProfile(),
                "Clientes",
                false
        );
    }

    private String notFoundMessage() {
        if (taxpayerLookupProvider.isConfigured()) {
            return "No se encontro ese RNC en la fuente fiscal configurada, base local DGII ni clientes registrados";
        }

        return "No se encontro ese RNC en la base local DGII ni en clientes registrados. Ejecuta la importacion DGII_RNC";
    }

    private String cleanRnc(String rawRnc) {
        return rawRnc == null ? "" : rawRnc.replaceAll("\\D", "");
    }
}
