package com.peraltapos.taxpayer;

import com.peraltapos.crm.customer.CustomerFiscalProfile;

public record TaxpayerLookupResponse(
        String rnc,
        String name,
        String status,
        CustomerFiscalProfile fiscalProfile,
        String source,
        boolean verified
) {
}
