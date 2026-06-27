package com.peraltapos.taxpayer;

import java.util.Optional;

public interface TaxpayerLookupProvider {

    Optional<TaxpayerLookupResponse> findByRnc(String rnc);

    default boolean isConfigured() {
        return false;
    }
}
