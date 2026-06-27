package com.peraltapos.taxpayer;

import com.peraltapos.crm.customer.CustomerFiscalProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.Normalizer;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Component
public class DgiiTaxpayerLookupProvider implements TaxpayerLookupProvider {

    private final RestTemplate restTemplate;
    private final String lookupUrl;

    public DgiiTaxpayerLookupProvider(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${app.taxpayer.dgii.lookup-url:}") String lookupUrl
    ) {
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(4))
                .readTimeout(Duration.ofSeconds(8))
                .build();
        this.lookupUrl = lookupUrl;
    }

    @Override
    public Optional<TaxpayerLookupResponse> findByRnc(String rnc) {
        if (!isConfigured()) {
            return Optional.empty();
        }

        try {
            String url = UriComponentsBuilder.fromUriString(lookupUrl)
                    .queryParam("rnc", rnc)
                    .toUriString();

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<?, ?> body = response.getBody();

            if (!response.getStatusCode().is2xxSuccessful() || body == null) {
                return Optional.empty();
            }

            String name = firstText(body, "name", "nombre", "razonSocial", "razon_social");

            if (name == null || name.isBlank()) {
                return Optional.empty();
            }

            String status = firstText(body, "status", "estado");
            String fiscalProfileText = firstText(
                    body,
                    "fiscalProfile",
                    "perfilFiscal",
                    "perfil_fiscal",
                    "regimen",
                    "regimenFiscal",
                    "regimen_fiscal",
                    "categoria",
                    "tipoContribuyente",
                    "tipo_contribuyente"
            );
            return Optional.of(new TaxpayerLookupResponse(rnc, name, status, fiscalProfileFor(fiscalProfileText), "DGII", true));
        } catch (RestClientException | IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    @Override
    public boolean isConfigured() {
        return lookupUrl != null && !lookupUrl.isBlank();
    }

    private CustomerFiscalProfile fiscalProfileFor(String value) {
        if (value == null || value.isBlank()) {
            return CustomerFiscalProfile.TAX_CREDIT;
        }

        String cleanValue = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase();

        if (cleanValue.contains("ZONA FRANCA")) {
            return CustomerFiscalProfile.FREE_ZONE;
        }

        if (cleanValue.contains("GOBIERNO")
                || cleanValue.contains("GUBERNAMENTAL")
                || cleanValue.contains("ESTATAL")
                || cleanValue.contains("INSTITUCION PUBLICA")) {
            return CustomerFiscalProfile.GOVERNMENT;
        }

        if (cleanValue.contains("REGIMEN ESPECIAL")
                || cleanValue.contains("EXENTO")
                || cleanValue.contains("EXENCION")) {
            return CustomerFiscalProfile.SPECIAL_REGIME;
        }

        return CustomerFiscalProfile.TAX_CREDIT;
    }

    private String firstText(Map<?, ?> body, String... keys) {
        for (String key : keys) {
            Object value = body.get(key);

            if (value != null && !value.toString().isBlank()) {
                return value.toString();
            }
        }

        return null;
    }
}
