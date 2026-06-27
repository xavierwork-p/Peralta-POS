package com.peraltapos.fiscal;

import com.peraltapos.common.web.BusinessException;
import com.peraltapos.company.Company;
import com.peraltapos.company.CompanyRepository;
import com.peraltapos.sales.sale.Sale;
import com.peraltapos.sales.sale.SaleItem;
import com.peraltapos.sales.sale.SaleRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class EcfSimulationService {

    private static final DateTimeFormatter TRACK_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final SaleRepository saleRepository;
    private final CompanyRepository companyRepository;
    private final FiscalEcfDocumentRepository documentRepository;

    public EcfSimulationService(
            SaleRepository saleRepository,
            CompanyRepository companyRepository,
            FiscalEcfDocumentRepository documentRepository
    ) {
        this.saleRepository = saleRepository;
        this.companyRepository = companyRepository;
        this.documentRepository = documentRepository;
    }

    @Transactional
    public EcfDocumentResponse generate(UUID saleId) {
        Sale sale = findSale(saleId);
        Company company = findCompany();
        String unsignedXml = buildUnsignedXml(sale, company);
        String xmlHash = sha256(unsignedXml);
        String signatureValue = sha256(xmlHash + "|" + sale.getNcf() + "|PERALTA-POS-FIRMA-SIMULADA");
        String signedXml = buildSignedXml(unsignedXml, signatureValue);

        FiscalEcfDocument document = documentRepository.findBySaleId(saleId)
                .map(existing -> {
                    existing.regenerate(unsignedXml, signedXml, xmlHash, signatureValue);
                    return existing;
                })
                .orElseGet(() -> FiscalEcfDocument.generated(sale, unsignedXml, signedXml, xmlHash, signatureValue));

        FiscalEcfDocument savedDocument = documentRepository.save(document);
        sale.markEcfGenerated(EcfStatus.SIGNED, "db:fiscal_ecf_documents/" + savedDocument.getId());
        return EcfDocumentResponse.from(savedDocument);
    }

    @Transactional
    public EcfDocumentResponse submitSimulated(UUID saleId) {
        FiscalEcfDocument document = documentRepository.findBySaleId(saleId)
                .orElseThrow(() -> new BusinessException("Primero genera el XML e-CF simulado"));

        String hash = document.getXmlHash() == null ? sha256(document.getSignedXml()) : document.getXmlHash();
        String securityCode = hash.substring(0, Math.min(12, hash.length())).toUpperCase();
        String trackId = "SIM-DGII-" + OffsetDateTime.now().format(TRACK_FORMAT) + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String acknowledgement = buildAcknowledgementXml(document, trackId, securityCode);

        document.acceptSimulated(trackId, securityCode, acknowledgement);
        document.getSale().markEcfAccepted(trackId, securityCode, "db:fiscal_ecf_documents/" + document.getId());
        return EcfDocumentResponse.from(documentRepository.save(document));
    }

    @Transactional(readOnly = true)
    public EcfDocumentResponse get(UUID saleId) {
        return documentRepository.findBySaleId(saleId)
                .map(EcfDocumentResponse::from)
                .orElseThrow(() -> new BusinessException("Esta factura todavia no tiene e-CF generado"));
    }

    private Sale findSale(UUID saleId) {
        return saleRepository.findById(saleId)
                .orElseThrow(() -> new BusinessException("Factura no encontrada"));
    }

    private Company findCompany() {
        return companyRepository.findByOrderByCreatedAtAsc(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException("No hay empresa configurada"));
    }

    private String buildUnsignedXml(Sale sale, Company company) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<eCFSimulado version=\"1.0\" ambiente=\"PRUEBA\" legal=\"false\">\n");
        xml.append("  <Advertencia>DOCUMENTO SIMULADO NO VALIDO COMO ENVIO OFICIAL DGII</Advertencia>\n");
        xml.append("  <Encabezado>\n");
        append(xml, "NumeroFactura", sale.getInvoiceNumber(), 4);
        append(xml, "NCF", sale.getNcf(), 4);
        append(xml, "TipoComprobante", sale.getFiscalDocumentType().name(), 4);
        append(xml, "FechaEmision", sale.getIssuedAt().toString(), 4);
        xml.append("  </Encabezado>\n");
        xml.append("  <Emisor>\n");
        append(xml, "RNC", company.getRnc(), 4);
        append(xml, "RazonSocial", company.getName(), 4);
        append(xml, "NombreComercial", company.getCommercialName(), 4);
        append(xml, "Direccion", company.getAddress(), 4);
        xml.append("  </Emisor>\n");
        xml.append("  <Comprador>\n");
        append(xml, "RNCComprador", sale.getCustomerFiscalId(), 4);
        append(xml, "RazonSocialComprador", sale.getCustomerName(), 4);
        xml.append("  </Comprador>\n");
        xml.append("  <Detalles>\n");
        int lineNumber = 1;
        for (SaleItem item : sale.getItems()) {
            xml.append("    <Item>\n");
            append(xml, "NumeroLinea", String.valueOf(lineNumber++), 6);
            append(xml, "NombreItem", item.getProductName(), 6);
            append(xml, "Cantidad", money(item.getQuantity()), 6);
            append(xml, "PrecioUnitario", money(item.getUnitPrice()), 6);
            append(xml, "TasaITBIS", money(item.getTaxRate()), 6);
            append(xml, "MontoItem", money(item.getLineTotal()), 6);
            xml.append("    </Item>\n");
        }
        xml.append("  </Detalles>\n");
        xml.append("  <Totales>\n");
        append(xml, "Subtotal", money(sale.getSubtotal()), 4);
        append(xml, "ITBIS", money(sale.getTaxTotal()), 4);
        append(xml, "Descuento", money(sale.getDiscountTotal()), 4);
        append(xml, "Total", money(sale.getTotal()), 4);
        xml.append("  </Totales>\n");
        xml.append("</eCFSimulado>");
        return xml.toString();
    }

    private String buildSignedXml(String unsignedXml, String signatureValue) {
        return unsignedXml.replace(
                "</eCFSimulado>",
                "  <FirmaSimulada algoritmo=\"SHA-256\" validaLegalmente=\"false\">\n"
                        + "    <ValorFirma>" + signatureValue + "</ValorFirma>\n"
                        + "  </FirmaSimulada>\n"
                        + "</eCFSimulado>"
        );
    }

    private String buildAcknowledgementXml(FiscalEcfDocument document, String trackId, String securityCode) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<AcuseRecepcionSimulado ambiente=\"PRUEBA\" legal=\"false\">\n");
        append(xml, "TrackId", trackId, 2);
        append(xml, "Estado", "ACEPTADO_SIMULADO", 2);
        append(xml, "CodigoSeguridad", securityCode, 2);
        append(xml, "NCF", document.getSale().getNcf(), 2);
        append(xml, "NumeroFactura", document.getSale().getInvoiceNumber(), 2);
        append(xml, "FechaRecepcion", OffsetDateTime.now().toString(), 2);
        append(xml, "Mensaje", "Acuse de prueba guardado localmente. No fue enviado a DGII.", 2);
        xml.append("</AcuseRecepcionSimulado>");
        return xml.toString();
    }

    private void append(StringBuilder xml, String tag, String value, int spaces) {
        xml.append(" ".repeat(spaces))
                .append("<").append(tag).append(">")
                .append(escapeXml(value))
                .append("</").append(tag).append(">\n");
    }

    private String escapeXml(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String money(BigDecimal amount) {
        return amount == null ? "0.00" : amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 no disponible", exception);
        }
    }
}
