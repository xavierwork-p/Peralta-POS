package com.peraltapos.reporting;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class BusinessReportController {

    private static final MediaType XLSX = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );
    private static final MediaType DOCX = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private final BusinessReportService businessReportService;

    public BusinessReportController(BusinessReportService businessReportService) {
        this.businessReportService = businessReportService;
    }

    @GetMapping("/inventory.xlsx")
    public ResponseEntity<byte[]> inventoryExcel(@RequestParam(defaultValue = "false") boolean includeCosts) {
        return file(
                businessReportService.inventoryExcel("products", includeCosts),
                XLSX,
                "productos-" + LocalDate.now() + ".xlsx",
                false
        );
    }

    @GetMapping("/inventory.pdf")
    public ResponseEntity<byte[]> inventoryPdf(@RequestParam(defaultValue = "false") boolean includeCosts) {
        return file(
                businessReportService.inventoryPdf("products", includeCosts),
                MediaType.APPLICATION_PDF,
                "productos-" + LocalDate.now() + ".pdf",
                false
        );
    }

    @GetMapping("/inventory.docx")
    public ResponseEntity<byte[]> inventoryWord(@RequestParam(defaultValue = "false") boolean includeCosts) {
        return file(
                businessReportService.inventoryWord("products", includeCosts),
                DOCX,
                "productos-" + LocalDate.now() + ".docx",
                false
        );
    }

    @GetMapping("/inventory/{section}.xlsx")
    public ResponseEntity<byte[]> inventorySectionExcel(
            @PathVariable String section,
            @RequestParam(defaultValue = "false") boolean includeCosts
    ) {
        InventoryReportSection reportSection = InventoryReportSection.fromPath(section);
        return file(
                businessReportService.inventoryExcel(section, includeCosts),
                XLSX,
                reportSection.filename() + "-" + LocalDate.now() + ".xlsx",
                false
        );
    }

    @GetMapping("/inventory/{section}.pdf")
    public ResponseEntity<byte[]> inventorySectionPdf(
            @PathVariable String section,
            @RequestParam(defaultValue = "false") boolean includeCosts
    ) {
        InventoryReportSection reportSection = InventoryReportSection.fromPath(section);
        return file(
                businessReportService.inventoryPdf(section, includeCosts),
                MediaType.APPLICATION_PDF,
                reportSection.filename() + "-" + LocalDate.now() + ".pdf",
                false
        );
    }

    @GetMapping("/inventory/{section}.docx")
    public ResponseEntity<byte[]> inventorySectionWord(
            @PathVariable String section,
            @RequestParam(defaultValue = "false") boolean includeCosts
    ) {
        InventoryReportSection reportSection = InventoryReportSection.fromPath(section);
        return file(
                businessReportService.inventoryWord(section, includeCosts),
                DOCX,
                reportSection.filename() + "-" + LocalDate.now() + ".docx",
                false
        );
    }

    @GetMapping("/purchase-orders/{id}.xlsx")
    public ResponseEntity<byte[]> purchaseOrderExcel(@PathVariable UUID id) {
        return file(
                businessReportService.purchaseOrderExcel(id),
                XLSX,
                "orden-compra-" + id + ".xlsx",
                false
        );
    }

    @GetMapping("/purchase-orders/{id}.pdf")
    public ResponseEntity<byte[]> purchaseOrderPdf(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean inline
    ) {
        return file(
                businessReportService.purchaseOrderPdf(id),
                MediaType.APPLICATION_PDF,
                "orden-compra-" + id + ".pdf",
                inline
        );
    }

    @GetMapping("/purchase-orders/{id}.docx")
    public ResponseEntity<byte[]> purchaseOrderWord(@PathVariable UUID id) {
        return file(
                businessReportService.purchaseOrderWord(id),
                DOCX,
                "orden-compra-" + id + ".docx",
                false
        );
    }

    @GetMapping("/quotes/{id}.xlsx")
    public ResponseEntity<byte[]> quoteExcel(@PathVariable UUID id) {
        return file(
                businessReportService.quoteExcel(id),
                XLSX,
                "cotizacion-" + id + ".xlsx",
                false
        );
    }

    @GetMapping("/quotes/{id}.pdf")
    public ResponseEntity<byte[]> quotePdf(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean inline
    ) {
        return file(
                businessReportService.quotePdf(id),
                MediaType.APPLICATION_PDF,
                "cotizacion-" + id + ".pdf",
                inline
        );
    }

    @GetMapping("/quotes/{id}.docx")
    public ResponseEntity<byte[]> quoteWord(@PathVariable UUID id) {
        return file(
                businessReportService.quoteWord(id),
                DOCX,
                "cotizacion-" + id + ".docx",
                false
        );
    }

    private ResponseEntity<byte[]> file(
            byte[] content,
            MediaType mediaType,
            String filename,
            boolean inline
    ) {
        ContentDisposition disposition = (inline
                ? ContentDisposition.inline()
                : ContentDisposition.attachment())
                .filename(filename, StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(content.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(content);
    }
}
