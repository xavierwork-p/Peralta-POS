package com.peraltapos.reporting;

import com.peraltapos.catalog.inventory.InventoryMovement;
import com.peraltapos.catalog.inventory.InventoryMovementRepository;
import com.peraltapos.catalog.inventory.count.InventoryCount;
import com.peraltapos.catalog.inventory.count.InventoryCountRepository;
import com.peraltapos.catalog.product.Product;
import com.peraltapos.catalog.product.ProductRepository;
import com.peraltapos.catalog.supplier.Supplier;
import com.peraltapos.catalog.supplier.SupplierRepository;
import com.peraltapos.purchasing.PurchaseInvoice;
import com.peraltapos.purchasing.PurchaseInvoiceRepository;
import com.peraltapos.purchasing.PurchasePaymentTerm;
import com.peraltapos.purchasing.order.PurchaseOrder;
import com.peraltapos.purchasing.order.PurchaseOrderItem;
import com.peraltapos.purchasing.order.PurchaseOrderRepository;
import com.peraltapos.purchasing.order.PurchaseOrderStatus;
import com.peraltapos.sales.quote.Quote;
import com.peraltapos.sales.quote.QuoteItem;
import com.peraltapos.sales.quote.QuoteRepository;
import com.peraltapos.sales.quote.QuoteStatus;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGrid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class BusinessReportService {

    private static final Color GREEN = new Color(18, 107, 93);
    private static final Color DARK = new Color(23, 33, 43);
    private static final Color LIGHT_GREEN = new Color(220, 234, 229);
    private static final Color MUTED = new Color(94, 109, 103);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ProductRepository productRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final InventoryCountRepository inventoryCountRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseInvoiceRepository purchaseInvoiceRepository;
    private final SupplierRepository supplierRepository;
    private final QuoteRepository quoteRepository;

    public BusinessReportService(
            ProductRepository productRepository,
            InventoryMovementRepository inventoryMovementRepository,
            InventoryCountRepository inventoryCountRepository,
            PurchaseOrderRepository purchaseOrderRepository,
            PurchaseInvoiceRepository purchaseInvoiceRepository,
            SupplierRepository supplierRepository,
            QuoteRepository quoteRepository
    ) {
        this.productRepository = productRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.inventoryCountRepository = inventoryCountRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseInvoiceRepository = purchaseInvoiceRepository;
        this.supplierRepository = supplierRepository;
        this.quoteRepository = quoteRepository;
    }

    @Transactional(readOnly = true)
    public byte[] inventoryExcel(String sectionValue, boolean includeCosts) {
        InventoryReportSection section = InventoryReportSection.fromPath(sectionValue);
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ExcelStyles styles = new ExcelStyles(workbook);
            switch (section) {
                case PRODUCTS -> createStockSheet(workbook, styles, includeCosts);
                case REPLENISHMENT -> createOrderSheet(workbook, styles);
                case PURCHASES -> createPurchaseInvoiceSheet(workbook, styles);
                case MOVEMENTS -> createMovementSheet(workbook, styles, includeCosts);
                case COUNTS -> createCountSheet(workbook, styles);
                case SUPPLIERS -> createSupplierSheet(workbook, styles);
            }
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo crear el reporte Excel", exception);
        }
    }

    @Transactional(readOnly = true)
    public byte[] inventoryWord(String sectionValue, boolean includeCosts) {
        InventoryReportSection section = InventoryReportSection.fromPath(sectionValue);
        try (XWPFDocument document = new XWPFDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            configureWordDocument(document);
            switch (section) {
                case PRODUCTS -> {
                    addWordTitle(document, "Catalogo de productos", "Listado comercial y existencias actuales");
                    addWordStockSection(document, includeCosts);
                }
                case REPLENISHMENT -> {
                    addWordTitle(document, "Ordenes de compra", "Reposicion, recepcion y seguimiento");
                    addWordOrderSection(document);
                }
                case PURCHASES -> {
                    addWordTitle(document, "Facturas de compra", "Compras recibidas de suplidores");
                    addWordPurchaseInvoiceSection(document);
                }
                case MOVEMENTS -> {
                    addWordTitle(document, "Movimientos de inventario", "Entradas, salidas y ajustes");
                    addWordMovementSection(document, includeCosts);
                }
                case COUNTS -> {
                    addWordTitle(document, "Conteos fisicos", "Diferencias y ajustes de inventario");
                    addWordCountSection(document);
                }
                case SUPPLIERS -> {
                    addWordTitle(document, "Directorio de suplidores", "Contactos y estado comercial");
                    addWordSupplierSection(document);
                }
            }
            document.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo crear el reporte Word", exception);
        }
    }

    @Transactional(readOnly = true)
    public byte[] inventoryPdf(String sectionValue, boolean includeCosts) {
        InventoryReportSection section = InventoryReportSection.fromPath(sectionValue);
        try {
            JasperPrint report = switch (section) {
                case PRODUCTS -> createStockPdf(includeCosts);
                case REPLENISHMENT -> createOrderListPdf();
                case PURCHASES -> createPurchaseInvoicePdf();
                case MOVEMENTS -> createMovementPdf(includeCosts);
                case COUNTS -> createCountPdf();
                case SUPPLIERS -> createSupplierPdf();
            };
            return JasperExportManager.exportReportToPdf(report);
        } catch (JRException exception) {
            throw new IllegalStateException("No se pudo crear el reporte PDF", exception);
        }
    }

    @Transactional(readOnly = true)
    public byte[] purchaseOrderExcel(UUID orderId) {
        PurchaseOrder order = findOrder(orderId);
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ExcelStyles styles = new ExcelStyles(workbook);
            Sheet sheet = workbook.createSheet("Orden de compra");
            createDocumentHeading(sheet, styles, "ORDEN DE COMPRA", order.getOrderNumber(), 6);
            int rowIndex = 4;
            rowIndex = addExcelLabelValue(sheet, styles, rowIndex, "Suplidor", order.getSupplier().getName());
            rowIndex = addExcelLabelValue(sheet, styles, rowIndex, "RNC", text(order.getSupplier().getRnc()));
            rowIndex = addExcelLabelValue(sheet, styles, rowIndex, "Fecha", formatDate(order.getOrderDate()));
            rowIndex = addExcelLabelValue(sheet, styles, rowIndex, "Entrega esperada", formatDate(order.getExpectedDate()));
            rowIndex = addExcelLabelValue(sheet, styles, rowIndex, "Estado", orderStatus(order.getStatus()));
            rowIndex += 1;
            String[] headers = {"Codigo", "Producto", "Cantidad", "Costo estimado", "ITBIS %", "Total"};
            Row header = sheet.createRow(rowIndex++);
            writeHeaderRow(header, styles.header(), headers);
            for (PurchaseOrderItem item : order.getItems()) {
                Row row = sheet.createRow(rowIndex++);
                writeText(row, 0, item.getProductSku(), styles.body());
                writeText(row, 1, item.getProductName(), styles.body());
                writeNumber(row, 2, item.getQuantity(), styles.quantity());
                writeNumber(row, 3, item.getUnitCost(), styles.money());
                writeNumber(row, 4, item.getTaxRate(), styles.quantity());
                writeNumber(row, 5, item.getLineTotal(), styles.money());
            }
            rowIndex += 1;
            rowIndex = addExcelTotal(sheet, styles, rowIndex, "Subtotal", order.getSubtotal());
            rowIndex = addExcelTotal(sheet, styles, rowIndex, "ITBIS", order.getTaxTotal());
            addExcelTotal(sheet, styles, rowIndex, "TOTAL", order.getTotal());
            if (order.getNotes() != null) {
                addExcelLabelValue(sheet, styles, rowIndex + 2, "Notas", order.getNotes());
            }
            finishSheet(sheet, 6, 1);
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo crear la orden Excel", exception);
        }
    }

    @Transactional(readOnly = true)
    public byte[] purchaseOrderWord(UUID orderId) {
        PurchaseOrder order = findOrder(orderId);
        try (XWPFDocument document = new XWPFDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            configureWordDocument(document);
            addWordTitle(document, "Orden de compra " + order.getOrderNumber(), "Ferreteria Peralta");
            addWordMetadata(document, List.of(
                    new String[]{"Suplidor", order.getSupplier().getName()},
                    new String[]{"RNC", text(order.getSupplier().getRnc())},
                    new String[]{"Fecha", formatDate(order.getOrderDate())},
                    new String[]{"Entrega esperada", formatDate(order.getExpectedDate())},
                    new String[]{"Estado", orderStatus(order.getStatus())}
            ));
            List<List<String>> rows = order.getItems().stream()
                    .map(item -> List.of(
                            item.getProductSku(),
                            item.getProductName(),
                            decimal(item.getQuantity()),
                            money(item.getUnitCost()),
                            decimal(item.getTaxRate()) + "%",
                            money(item.getLineTotal())
                    ))
                    .toList();
            addWordTable(
                    document,
                    List.of("Codigo", "Producto", "Cantidad", "Costo", "ITBIS", "Total"),
                    rows
            );
            addWordTotals(document, order.getSubtotal(), order.getTaxTotal(), order.getTotal());
            if (order.getNotes() != null) {
                addWordNote(document, "Notas: " + order.getNotes());
            }
            document.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo crear la orden Word", exception);
        }
    }

    @Transactional(readOnly = true)
    public byte[] purchaseOrderPdf(UUID orderId) {
        PurchaseOrder order = findOrder(orderId);
        try {
            List<String> headers = List.of("Codigo", "Producto", "Cantidad", "Costo", "ITBIS", "Total");
            List<List<String>> rows = order.getItems().stream()
                    .map(item -> List.of(
                            item.getProductSku(),
                            item.getProductName(),
                            decimal(item.getQuantity()),
                            money(item.getUnitCost()),
                            decimal(item.getTaxRate()) + "%",
                            money(item.getLineTotal())
                    ))
                    .toList();
            String subtitle = "Suplidor: " + order.getSupplier().getName()
                    + "   |   RNC: " + text(order.getSupplier().getRnc())
                    + "\nFecha: " + formatDate(order.getOrderDate())
                    + "   |   Entrega esperada: " + formatDate(order.getExpectedDate())
                    + "   |   Estado: " + orderStatus(order.getStatus());
            List<String> footer = new ArrayList<>(List.of(
                    "Subtotal: " + money(order.getSubtotal()),
                    "ITBIS: " + money(order.getTaxTotal()),
                    "TOTAL: " + money(order.getTotal())
            ));
            if (order.getNotes() != null) {
                footer.add("Notas: " + order.getNotes());
            }
            JasperPrint print = createTableReport(
                    "ORDEN DE COMPRA " + order.getOrderNumber(),
                    subtitle,
                    headers,
                    rows,
                    footer,
                    false
            );
            return JasperExportManager.exportReportToPdf(print);
        } catch (JRException exception) {
            throw new IllegalStateException("No se pudo crear la orden PDF", exception);
        }
    }

    @Transactional(readOnly = true)
    public byte[] quoteExcel(UUID quoteId) {
        Quote quote = findQuote(quoteId);
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ExcelStyles styles = new ExcelStyles(workbook);
            Sheet sheet = workbook.createSheet("Cotizacion");
            createDocumentHeading(sheet, styles, "COTIZACION", quote.getQuoteNumber(), 7);
            int rowIndex = 4;
            rowIndex = addExcelLabelValue(sheet, styles, rowIndex, "Cliente", quote.getCustomerName(), 6);
            rowIndex = addExcelLabelValue(sheet, styles, rowIndex, "RNC o cedula", text(quote.getCustomerFiscalId()), 6);
            rowIndex = addExcelLabelValue(sheet, styles, rowIndex, "Fecha", formatDate(quote.getIssueDate()), 6);
            rowIndex = addExcelLabelValue(sheet, styles, rowIndex, "Valida hasta", formatDate(quote.getValidUntil()), 6);
            rowIndex = addExcelLabelValue(sheet, styles, rowIndex, "Estado", quoteStatus(quote.getStatus()), 6);
            rowIndex += 1;
            String[] headers = {"Producto", "Cantidad", "Precio", "ITBIS %", "Subtotal", "ITBIS", "Total"};
            writeHeaderRow(sheet.createRow(rowIndex++), styles.header(), headers);
            for (QuoteItem item : quote.getItems()) {
                Row row = sheet.createRow(rowIndex++);
                writeText(row, 0, item.getProductName(), styles.body());
                writeNumber(row, 1, item.getQuantity(), styles.quantity());
                writeNumber(row, 2, item.getUnitPrice(), styles.money());
                writeNumber(row, 3, item.getTaxRate(), styles.quantity());
                writeNumber(row, 4, item.getSubtotal(), styles.money());
                writeNumber(row, 5, item.getTaxAmount(), styles.money());
                writeNumber(row, 6, item.getLineTotal(), styles.money());
            }
            rowIndex += 1;
            rowIndex = addExcelTotal(sheet, styles, rowIndex, "Subtotal", quote.getSubtotal(), 5, 6);
            rowIndex = addExcelTotal(sheet, styles, rowIndex, "ITBIS", quote.getTaxTotal(), 5, 6);
            addExcelTotal(sheet, styles, rowIndex, "TOTAL", quote.getTotal(), 5, 6);
            if (quote.getNotes() != null) {
                addExcelLabelValue(sheet, styles, rowIndex + 2, "Condiciones", quote.getNotes(), 6);
            }
            finishSheet(sheet, headers.length, 10);
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo crear la cotizacion Excel", exception);
        }
    }

    @Transactional(readOnly = true)
    public byte[] quoteWord(UUID quoteId) {
        Quote quote = findQuote(quoteId);
        try (XWPFDocument document = new XWPFDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            configureWordDocument(document);
            addWordTitle(document, "Cotizacion " + quote.getQuoteNumber(), "Ferreteria Peralta");
            addWordMetadata(document, List.of(
                    new String[]{"Cliente", quote.getCustomerName()},
                    new String[]{"RNC o cedula", text(quote.getCustomerFiscalId())},
                    new String[]{"Fecha", formatDate(quote.getIssueDate())},
                    new String[]{"Valida hasta", formatDate(quote.getValidUntil())},
                    new String[]{"Estado", quoteStatus(quote.getStatus())}
            ));
            List<List<String>> rows = quote.getItems().stream()
                    .map(item -> List.of(
                            item.getProductName(),
                            decimal(item.getQuantity()),
                            money(item.getUnitPrice()),
                            decimal(item.getTaxRate()) + "%",
                            money(item.getLineTotal())
                    ))
                    .toList();
            addWordTable(document, List.of("Producto", "Cantidad", "Precio", "ITBIS", "Total"), rows);
            addWordTotals(document, quote.getSubtotal(), quote.getTaxTotal(), quote.getTotal());
            if (quote.getNotes() != null) {
                addWordNote(document, "Condiciones: " + quote.getNotes());
            }
            document.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo crear la cotizacion Word", exception);
        }
    }

    @Transactional(readOnly = true)
    public byte[] quotePdf(UUID quoteId) {
        Quote quote = findQuote(quoteId);
        try {
            List<List<String>> rows = quote.getItems().stream()
                    .map(item -> List.of(
                            item.getProductName(),
                            decimal(item.getQuantity()),
                            money(item.getUnitPrice()),
                            decimal(item.getTaxRate()) + "%",
                            money(item.getSubtotal()),
                            money(item.getTaxAmount()),
                            money(item.getLineTotal())
                    ))
                    .toList();
            String subtitle = "Cliente: " + quote.getCustomerName()
                    + "   |   RNC/Cedula: " + text(quote.getCustomerFiscalId())
                    + "\nFecha: " + formatDate(quote.getIssueDate())
                    + "   |   Valida hasta: " + formatDate(quote.getValidUntil())
                    + "   |   Estado: " + quoteStatus(quote.getStatus());
            List<String> footer = new ArrayList<>(List.of(
                    "Subtotal: " + money(quote.getSubtotal()),
                    "ITBIS: " + money(quote.getTaxTotal()),
                    "TOTAL: " + money(quote.getTotal())
            ));
            if (quote.getNotes() != null) {
                footer.add("Condiciones: " + quote.getNotes());
            }
            JasperPrint print = createTableReport(
                    "COTIZACION " + quote.getQuoteNumber(),
                    subtitle,
                    List.of("Producto", "Cantidad", "Precio", "ITBIS", "Subtotal", "Impuesto", "Total"),
                    rows,
                    footer,
                    false
            );
            return JasperExportManager.exportReportToPdf(print);
        } catch (JRException exception) {
            throw new IllegalStateException("No se pudo crear la cotizacion PDF", exception);
        }
    }

    private JasperPrint createStockPdf(boolean includeCosts) throws JRException {
        List<String> headers = new ArrayList<>(List.of(
                "Codigo", "Producto", "Categoria", "Marca", "Unidad", "Stock", "Minimo", "Precio", "ITBIS", "Estado"
        ));
        if (includeCosts) {
            headers.add(7, "Costo");
            headers.add(8, "Valor costo");
        }
        List<List<String>> rows = productRepository.findAll(Sort.by("name")).stream().map(product -> {
            List<String> values = new ArrayList<>(List.of(
                    product.getSku(),
                    product.getName(),
                    text(product.getCategoryName()),
                    text(product.getBrandName()),
                    product.getUnit(),
                    decimal(product.getCurrentStock()),
                    decimal(product.getMinimumStock()),
                    money(product.getSalePrice()),
                    decimal(product.getTaxRate()) + "%",
                    product.isLowStock() ? "Stock bajo" : product.isActive() ? "Activo" : "Inactivo"
            ));
            if (includeCosts) {
                values.add(7, money(product.getCostPrice()));
                values.add(8, money(product.getCostPrice().multiply(product.getCurrentStock())));
            }
            return values;
        }).toList();
        return createTableReport(
                "CATALOGO DE PRODUCTOS",
                "Listado de productos al " + formatDate(LocalDate.now())
                        + "   |   Costos incluidos: " + (includeCosts ? "Si" : "No"),
                headers,
                rows,
                List.of("Productos registrados: " + rows.size()),
                true
        );
    }

    private JasperPrint createMovementPdf(boolean includeCosts) throws JRException {
        List<String> headers = new ArrayList<>(List.of("Fecha", "Codigo", "Producto", "Tipo", "Cantidad", "Referencia"));
        if (includeCosts) {
            headers.add(5, "Costo");
        }
        List<InventoryMovement> movements = recentMovements();
        List<List<String>> rows = movements.stream().map(movement -> {
            List<String> values = new ArrayList<>(List.of(
                    formatDateTime(movement.getCreatedAt()),
                    movement.getProduct().getSku(),
                    movement.getProduct().getName(),
                    movementType(movement),
                    decimal(movement.getQuantity()),
                    text(movement.getReference())
            ));
            if (includeCosts) {
                values.add(5, movement.getUnitCost() == null ? "" : money(movement.getUnitCost()));
            }
            return values;
        }).toList();
        return createTableReport(
                "MOVIMIENTOS DE INVENTARIO",
                "Ultimos " + movements.size() + " movimientos registrados",
                headers,
                rows,
                List.of(),
                true
        );
    }

    private JasperPrint createCountPdf() throws JRException {
        List<InventoryCount> counts = inventoryCountRepository.findTop20ByOrderByCountedAtDesc();
        List<List<String>> rows = counts.stream().map(count -> {
            long differences = count.getItems().stream().filter(item -> item.getDifference().signum() != 0).count();
            BigDecimal balance = count.getItems().stream()
                    .map(item -> item.getDifference())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return List.of(
                    count.getCountNumber(),
                    formatDateTime(count.getCountedAt()),
                    Integer.toString(count.getItems().size()),
                    Long.toString(differences),
                    signed(balance),
                    text(count.getNotes())
            );
        }).toList();
        return createTableReport(
                "CONTEOS FISICOS",
                "Ultimos conteos y diferencias aplicadas",
                List.of("Conteo", "Fecha", "Productos", "Diferencias", "Balance", "Notas"),
                rows,
                List.of(),
                true
        );
    }

    private JasperPrint createOrderListPdf() throws JRException {
        List<PurchaseOrder> orders = purchaseOrderRepository.findAll(
                Sort.by("orderDate").descending().and(Sort.by("createdAt").descending())
        );
        List<List<String>> rows = orders.stream().map(order -> List.of(
                order.getOrderNumber(),
                formatDate(order.getOrderDate()),
                order.getSupplier().getName(),
                orderStatus(order.getStatus()),
                Integer.toString(order.getItems().size()),
                formatDate(order.getExpectedDate()),
                money(order.getTotal())
        )).toList();
        return createTableReport(
                "ORDENES DE COMPRA",
                "Historial de reposicion y recepcion",
                List.of("Orden", "Fecha", "Suplidor", "Estado", "Productos", "Entrega", "Total"),
                rows,
                List.of(),
                true
        );
    }

    private JasperPrint createPurchaseInvoicePdf() throws JRException {
        List<PurchaseInvoice> invoices = purchaseInvoiceRepository.findAll(
                Sort.by("invoiceDate").descending().and(Sort.by("createdAt").descending())
        );
        List<List<String>> rows = invoices.stream().map(invoice -> List.of(
                formatDate(invoice.getInvoiceDate()),
                invoice.getDocumentNumber(),
                invoice.getSupplier().getName(),
                text(invoice.getSupplier().getRnc()),
                invoice.getPurchaseOrder() == null ? "Sin orden" : invoice.getPurchaseOrder().getOrderNumber(),
                paymentTerm(invoice.getPaymentTerm()),
                formatDate(invoice.getDueDate()),
                Integer.toString(invoice.getItems().size()),
                money(invoice.getSubtotal()),
                money(invoice.getTaxTotal()),
                money(invoice.getTotal())
        )).toList();
        BigDecimal total = invoices.stream()
                .map(PurchaseInvoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return createTableReport(
                "FACTURAS DE COMPRA",
                "Compras recibidas y registradas por suplidor",
                List.of("Fecha", "Factura", "Suplidor", "RNC", "Orden", "Condicion", "Vence", "Items", "Subtotal", "ITBIS", "Total"),
                rows,
                List.of("Facturas registradas: " + invoices.size(), "Total comprado: " + money(total)),
                true
        );
    }

    private JasperPrint createSupplierPdf() throws JRException {
        List<Supplier> suppliers = supplierRepository.findAll(Sort.by("name"));
        List<List<String>> rows = suppliers.stream().map(supplier -> List.of(
                supplier.getName(),
                text(supplier.getRnc()),
                text(supplier.getPhone()),
                text(supplier.getEmail()),
                text(supplier.getAddress()),
                supplier.isActive() ? "Activo" : "Inactivo"
        )).toList();
        long active = suppliers.stream().filter(Supplier::isActive).count();
        return createTableReport(
                "DIRECTORIO DE SUPLIDORES",
                "Contactos y estado comercial",
                List.of("Suplidor", "RNC", "Telefono", "Correo", "Direccion", "Estado"),
                rows,
                List.of("Suplidores registrados: " + suppliers.size(), "Suplidores activos: " + active),
                true
        );
    }

    private JasperPrint createTableReport(
            String title,
            String subtitle,
            List<String> headers,
            List<List<String>> rows,
            List<String> footerLines,
            boolean landscape
    ) throws JRException {
        JasperDesign design = new JasperDesign();
        design.setName("report_" + UUID.randomUUID().toString().replace("-", ""));
        int pageWidth = landscape ? 842 : 595;
        int pageHeight = landscape ? 595 : 842;
        int leftMargin = 30;
        int rightMargin = 30;
        int usableWidth = pageWidth - leftMargin - rightMargin;
        design.setPageWidth(pageWidth);
        design.setPageHeight(pageHeight);
        design.setOrientation(landscape
                ? net.sf.jasperreports.engine.type.OrientationEnum.LANDSCAPE
                : net.sf.jasperreports.engine.type.OrientationEnum.PORTRAIT);
        design.setLeftMargin(leftMargin);
        design.setRightMargin(rightMargin);
        design.setTopMargin(24);
        design.setBottomMargin(24);
        design.setColumnWidth(usableWidth);

        for (int index = 0; index < headers.size(); index++) {
            JRDesignField field = new JRDesignField();
            field.setName("c" + index);
            field.setValueClass(String.class);
            design.addField(field);
        }

        JRDesignBand titleBand = new JRDesignBand();
        titleBand.setHeight(76);
        titleBand.addElement(staticText("FERRETERIA PERALTA", 0, 0, usableWidth, 18, 13f, true, GREEN, null));
        titleBand.addElement(staticText(title, 0, 22, usableWidth, 24, 19f, true, DARK, null));
        titleBand.addElement(staticText(subtitle, 0, 49, usableWidth, 24, 9f, false, MUTED, null));
        design.setTitle(titleBand);

        JRDesignBand headerBand = new JRDesignBand();
        headerBand.setHeight(26);
        int[] widths = columnWidths(headers, usableWidth);
        headerBand.addElement(staticText("", 0, 0, usableWidth, 24, 8f, false, Color.WHITE, GREEN));
        int x = 0;
        float tableFontSize = headers.size() >= 10 ? 7.4f : 8.3f;
        for (int index = 0; index < headers.size(); index++) {
            JRDesignStaticText header = staticText(
                    headers.get(index),
                    x + 4,
                    0,
                    Math.max(widths[index] - 8, 1),
                    24,
                    tableFontSize,
                    true,
                    Color.WHITE,
                    null
            );
            header.setHorizontalTextAlign(isNumericHeader(headers.get(index))
                    ? HorizontalTextAlignEnum.RIGHT
                    : HorizontalTextAlignEnum.LEFT);
            headerBand.addElement(header);
            x += widths[index];
        }
        design.setColumnHeader(headerBand);

        JRDesignBand detailBand = new JRDesignBand();
        detailBand.setHeight(22);
        x = 0;
        for (int index = 0; index < headers.size(); index++) {
            JRDesignTextField field = new JRDesignTextField();
            field.setX(x + 4);
            field.setY(2);
            field.setWidth(Math.max(widths[index] - 8, 1));
            field.setHeight(18);
            field.setFontSize(tableFontSize);
            field.setForecolor(DARK);
            field.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
            field.setHorizontalTextAlign(isNumericHeader(headers.get(index))
                    ? HorizontalTextAlignEnum.RIGHT
                    : HorizontalTextAlignEnum.LEFT);
            field.setExpression(new JRDesignExpression("$F{c" + index + "}"));
            field.getLineBox().getBottomPen().setLineWidth(0.35f);
            field.getLineBox().getBottomPen().setLineColor(new Color(224, 229, 226));
            detailBand.addElement(field);
            x += widths[index];
        }
        ((JRDesignSection) design.getDetailSection()).addBand(detailBand);

        if (!footerLines.isEmpty()) {
            JRDesignBand summary = new JRDesignBand();
            summary.setHeight(footerLines.size() * 18 + 12);
            int y = 8;
            for (String line : footerLines) {
                summary.addElement(staticText(line, usableWidth / 2, y, usableWidth / 2, 16, 10f, true, DARK, null));
                y += 18;
            }
            design.setSummary(summary);
        }

        JRDesignBand footer = new JRDesignBand();
        footer.setHeight(20);
        footer.addElement(staticText(
                "Peralta POS | Generado " + formatDateTime(OffsetDateTime.now()),
                0,
                4,
                usableWidth - 100,
                14,
                8f,
                false,
                MUTED,
                null
        ));
        JRDesignTextField pageNumber = new JRDesignTextField();
        pageNumber.setX(usableWidth - 100);
        pageNumber.setY(4);
        pageNumber.setWidth(100);
        pageNumber.setHeight(14);
        pageNumber.setFontSize(8f);
        pageNumber.setForecolor(MUTED);
        pageNumber.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
        pageNumber.setExpression(new JRDesignExpression("\"Pagina \" + $V{PAGE_NUMBER}"));
        footer.addElement(pageNumber);
        design.setPageFooter(footer);

        List<Map<String, ?>> data = new ArrayList<>();
        for (List<String> row : rows) {
            Map<String, Object> values = new HashMap<>();
            for (int index = 0; index < headers.size(); index++) {
                values.put("c" + index, index < row.size() ? row.get(index) : "");
            }
            data.add(values);
        }
        if (data.isEmpty()) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("c0", "Sin registros para mostrar");
            data.add(empty);
        }

        Map<String, Object> parameters = new HashMap<>();
        return JasperFillManager.fillReport(
                JasperCompileManager.compileReport(design),
                parameters,
                new JRMapCollectionDataSource(data)
        );
    }

    private boolean isNumericHeader(String header) {
        String normalized = header.toLowerCase(Locale.ROOT);
        return normalized.contains("stock")
                || normalized.contains("minimo")
                || normalized.contains("cantidad")
                || normalized.contains("costo")
                || normalized.contains("precio")
                || normalized.contains("valor")
                || normalized.contains("itbis")
                || normalized.contains("total")
                || normalized.contains("subtotal")
                || normalized.contains("balance")
                || normalized.contains("diferencia")
                || normalized.contains("productos")
                || normalized.contains("items");
    }

    private JRDesignStaticText staticText(
            String value,
            int x,
            int y,
            int width,
            int height,
            float fontSize,
            boolean bold,
            Color foreground,
            Color background
    ) {
        JRDesignStaticText text = new JRDesignStaticText();
        text.setText(value == null ? "" : value);
        text.setX(x);
        text.setY(y);
        text.setWidth(width);
        text.setHeight(height);
        text.setFontSize(fontSize);
        text.setBold(bold);
        text.setForecolor(foreground);
        text.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
        if (background != null) {
            text.setBackcolor(background);
            text.setMode(ModeEnum.OPAQUE);
        }
        return text;
    }

    private int[] columnWidths(List<String> headers, int totalWidth) {
        int[] weights = headers.stream().mapToInt(header -> {
            String normalized = header.toLowerCase(Locale.ROOT);
            if (normalized.contains("producto") || normalized.contains("suplidor") || normalized.contains("notas")) {
                return 24;
            }
            if (normalized.contains("fecha") || normalized.contains("referencia") || normalized.contains("orden")) {
                return 16;
            }
            return 12;
        }).toArray();
        int weightTotal = java.util.Arrays.stream(weights).sum();
        int[] widths = new int[weights.length];
        int used = 0;
        for (int index = 0; index < weights.length; index++) {
            widths[index] = index == weights.length - 1
                    ? totalWidth - used
                    : totalWidth * weights[index] / weightTotal;
            used += widths[index];
        }
        return widths;
    }

    private void createStockSheet(XSSFWorkbook workbook, ExcelStyles styles, boolean includeCosts) {
        Sheet sheet = workbook.createSheet("Productos");
        List<String> headers = new ArrayList<>(List.of(
                "Codigo", "Codigo de barra", "Producto", "Categoria", "Marca", "Unidad",
                "Stock", "Minimo", "Precio venta", "ITBIS %", "Estado"
        ));
        if (includeCosts) {
            headers.add(8, "Costo");
            headers.add(9, "Valor costo");
        }
        createDocumentHeading(sheet, styles, "CATALOGO DE PRODUCTOS", "Listado comercial y existencias actuales", headers.size());
        Row header = sheet.createRow(4);
        writeHeaderRow(header, styles.header(), headers.toArray(String[]::new));
        int rowIndex = 5;
        for (Product product : productRepository.findAll(Sort.by("name"))) {
            Row row = sheet.createRow(rowIndex++);
            writeText(row, 0, product.getSku(), styles.body());
            writeText(row, 1, text(product.getBarcode()), styles.body());
            writeText(row, 2, product.getName(), styles.body());
            writeText(row, 3, text(product.getCategoryName()), styles.body());
            writeText(row, 4, text(product.getBrandName()), styles.body());
            writeText(row, 5, product.getUnit(), styles.body());
            writeNumber(row, 6, product.getCurrentStock(), product.isLowStock() ? styles.warningNumber() : styles.quantity());
            writeNumber(row, 7, product.getMinimumStock(), styles.quantity());
            int column = 8;
            if (includeCosts) {
                writeNumber(row, column++, product.getCostPrice(), styles.money());
                writeNumber(row, column++, product.getCostPrice().multiply(product.getCurrentStock()), styles.money());
            }
            writeNumber(row, column++, product.getSalePrice(), styles.money());
            writeNumber(row, column++, product.getTaxRate(), styles.quantity());
            writeText(row, column, product.isLowStock() ? "Stock bajo" : product.isActive() ? "Activo" : "Inactivo",
                    product.isLowStock() ? styles.warningText() : styles.body());
        }
        finishSheet(sheet, headers.size(), 4);
    }

    private void createMovementSheet(XSSFWorkbook workbook, ExcelStyles styles, boolean includeCosts) {
        Sheet sheet = workbook.createSheet("Movimientos");
        List<String> headers = new ArrayList<>(List.of("Fecha", "Codigo", "Producto", "Tipo", "Cantidad", "Referencia", "Notas"));
        if (includeCosts) {
            headers.add(5, "Costo");
        }
        createDocumentHeading(sheet, styles, "MOVIMIENTOS DE INVENTARIO", "Kardex reciente", headers.size());
        writeHeaderRow(sheet.createRow(4), styles.header(), headers.toArray(String[]::new));
        int rowIndex = 5;
        for (InventoryMovement movement : recentMovements()) {
            Row row = sheet.createRow(rowIndex++);
            writeText(row, 0, formatDateTime(movement.getCreatedAt()), styles.body());
            writeText(row, 1, movement.getProduct().getSku(), styles.body());
            writeText(row, 2, movement.getProduct().getName(), styles.body());
            writeText(row, 3, movementType(movement), styles.body());
            writeNumber(row, 4, movement.getQuantity(), styles.quantity());
            int column = 5;
            if (includeCosts) {
                writeNumber(row, column++, movement.getUnitCost(), styles.money());
            }
            writeText(row, column++, text(movement.getReference()), styles.body());
            writeText(row, column, text(movement.getNotes()), styles.body());
        }
        finishSheet(sheet, headers.size(), 4);
    }

    private void createCountSheet(XSSFWorkbook workbook, ExcelStyles styles) {
        Sheet sheet = workbook.createSheet("Conteos fisicos");
        String[] headers = {"Conteo", "Fecha", "Productos", "Diferencias", "Balance", "Notas"};
        createDocumentHeading(sheet, styles, "CONTEOS FISICOS", "Diferencias y ajustes aplicados", headers.length);
        writeHeaderRow(sheet.createRow(4), styles.header(), headers);
        int rowIndex = 5;
        for (InventoryCount count : inventoryCountRepository.findTop20ByOrderByCountedAtDesc()) {
            long differences = count.getItems().stream().filter(item -> item.getDifference().signum() != 0).count();
            BigDecimal balance = count.getItems().stream()
                    .map(item -> item.getDifference())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Row row = sheet.createRow(rowIndex++);
            writeText(row, 0, count.getCountNumber(), styles.body());
            writeText(row, 1, formatDateTime(count.getCountedAt()), styles.body());
            writeNumber(row, 2, BigDecimal.valueOf(count.getItems().size()), styles.quantity());
            writeNumber(row, 3, BigDecimal.valueOf(differences), styles.quantity());
            writeNumber(row, 4, balance, balance.signum() == 0 ? styles.quantity() : styles.warningNumber());
            writeText(row, 5, text(count.getNotes()), styles.body());
        }
        finishSheet(sheet, headers.length, 4);
    }

    private void createOrderSheet(XSSFWorkbook workbook, ExcelStyles styles) {
        Sheet sheet = workbook.createSheet("Ordenes de compra");
        String[] headers = {"Orden", "Fecha", "Suplidor", "Estado", "Productos", "Entrega esperada", "Total"};
        createDocumentHeading(sheet, styles, "ORDENES DE COMPRA", "Reposicion y recepcion", headers.length);
        writeHeaderRow(sheet.createRow(4), styles.header(), headers);
        int rowIndex = 5;
        for (PurchaseOrder order : purchaseOrderRepository.findAll(Sort.by("orderDate").descending())) {
            Row row = sheet.createRow(rowIndex++);
            writeText(row, 0, order.getOrderNumber(), styles.body());
            writeText(row, 1, formatDate(order.getOrderDate()), styles.body());
            writeText(row, 2, order.getSupplier().getName(), styles.body());
            writeText(row, 3, orderStatus(order.getStatus()), styles.body());
            writeNumber(row, 4, BigDecimal.valueOf(order.getItems().size()), styles.quantity());
            writeText(row, 5, formatDate(order.getExpectedDate()), styles.body());
            writeNumber(row, 6, order.getTotal(), styles.money());
        }
        finishSheet(sheet, headers.length, 4);
    }

    private void createPurchaseInvoiceSheet(XSSFWorkbook workbook, ExcelStyles styles) {
        Sheet sheet = workbook.createSheet("Facturas de compra");
        String[] headers = {
                "Fecha", "Factura", "Suplidor", "RNC", "Orden", "Condicion", "Vencimiento",
                "Productos", "Subtotal", "ITBIS", "Total", "Estado"
        };
        createDocumentHeading(sheet, styles, "FACTURAS DE COMPRA", "Compras recibidas de suplidores", headers.length);
        writeHeaderRow(sheet.createRow(4), styles.header(), headers);
        int rowIndex = 5;
        for (PurchaseInvoice invoice : purchaseInvoiceRepository.findAll(
                Sort.by("invoiceDate").descending().and(Sort.by("createdAt").descending())
        )) {
            Row row = sheet.createRow(rowIndex++);
            writeText(row, 0, formatDate(invoice.getInvoiceDate()), styles.body());
            writeText(row, 1, invoice.getDocumentNumber(), styles.body());
            writeText(row, 2, invoice.getSupplier().getName(), styles.body());
            writeText(row, 3, text(invoice.getSupplier().getRnc()), styles.body());
            writeText(row, 4, invoice.getPurchaseOrder() == null ? "Sin orden" : invoice.getPurchaseOrder().getOrderNumber(), styles.body());
            writeText(row, 5, paymentTerm(invoice.getPaymentTerm()), styles.body());
            writeText(row, 6, formatDate(invoice.getDueDate()), styles.body());
            writeNumber(row, 7, BigDecimal.valueOf(invoice.getItems().size()), styles.quantity());
            writeNumber(row, 8, invoice.getSubtotal(), styles.money());
            writeNumber(row, 9, invoice.getTaxTotal(), styles.money());
            writeNumber(row, 10, invoice.getTotal(), styles.money());
            writeText(row, 11, invoice.getStatus() == com.peraltapos.purchasing.PurchaseInvoiceStatus.POSTED
                    ? "Registrada" : "Cancelada", styles.body());
        }
        finishSheet(sheet, headers.length, 4);
    }

    private void createSupplierSheet(XSSFWorkbook workbook, ExcelStyles styles) {
        Sheet sheet = workbook.createSheet("Suplidores");
        String[] headers = {"Suplidor", "RNC", "Telefono", "Correo", "Direccion", "Estado"};
        createDocumentHeading(sheet, styles, "DIRECTORIO DE SUPLIDORES", "Contactos y estado comercial", headers.length);
        writeHeaderRow(sheet.createRow(4), styles.header(), headers);
        int rowIndex = 5;
        for (Supplier supplier : supplierRepository.findAll(Sort.by("name"))) {
            Row row = sheet.createRow(rowIndex++);
            writeText(row, 0, supplier.getName(), styles.body());
            writeText(row, 1, text(supplier.getRnc()), styles.body());
            writeText(row, 2, text(supplier.getPhone()), styles.body());
            writeText(row, 3, text(supplier.getEmail()), styles.body());
            writeText(row, 4, text(supplier.getAddress()), styles.body());
            writeText(row, 5, supplier.isActive() ? "Activo" : "Inactivo", styles.body());
        }
        finishSheet(sheet, headers.length, 4);
    }

    private void createDocumentHeading(
            Sheet sheet,
            ExcelStyles styles,
            String title,
            String subtitle,
            int columns
    ) {
        int lastColumn = Math.max(columns - 1, 0);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, lastColumn));
        Cell company = sheet.createRow(0).createCell(0);
        company.setCellValue("FERRETERIA PERALTA");
        company.setCellStyle(styles.company());
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, lastColumn));
        Cell titleCell = sheet.createRow(1).createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(styles.title());
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, lastColumn));
        Cell subtitleCell = sheet.createRow(2).createCell(0);
        subtitleCell.setCellValue(subtitle + " | Generado " + formatDateTime(OffsetDateTime.now()));
        subtitleCell.setCellStyle(styles.subtitle());
    }

    private int addExcelLabelValue(Sheet sheet, ExcelStyles styles, int rowIndex, String label, String value) {
        return addExcelLabelValue(sheet, styles, rowIndex, label, value, 5);
    }

    private int addExcelLabelValue(
            Sheet sheet,
            ExcelStyles styles,
            int rowIndex,
            String label,
            String value,
            int lastColumn
    ) {
        Row row = sheet.createRow(rowIndex);
        writeText(row, 0, label, styles.label());
        writeText(row, 1, value, styles.body());
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 1, lastColumn));
        return rowIndex + 1;
    }

    private int addExcelTotal(Sheet sheet, ExcelStyles styles, int rowIndex, String label, BigDecimal value) {
        return addExcelTotal(sheet, styles, rowIndex, label, value, 4, 5);
    }

    private int addExcelTotal(
            Sheet sheet,
            ExcelStyles styles,
            int rowIndex,
            String label,
            BigDecimal value,
            int labelColumn,
            int valueColumn
    ) {
        Row row = sheet.createRow(rowIndex);
        writeText(row, labelColumn, label, "TOTAL".equals(label) ? styles.totalLabel() : styles.label());
        writeNumber(row, valueColumn, value, "TOTAL".equals(label) ? styles.totalMoney() : styles.money());
        return rowIndex + 1;
    }

    private void writeHeaderRow(Row row, CellStyle style, String[] headers) {
        for (int index = 0; index < headers.length; index++) {
            writeText(row, index, headers[index], style);
        }
        row.setHeightInPoints(24);
    }

    private void writeText(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value == null ? "" : value);
        cell.setCellStyle(style);
    }

    private void writeNumber(Row row, int column, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value == null ? 0 : value.doubleValue());
        cell.setCellStyle(style);
    }

    private void finishSheet(Sheet sheet, int columns, int freezeRow) {
        sheet.createFreezePane(0, freezeRow + 1);
        sheet.setAutoFilter(new CellRangeAddress(freezeRow, freezeRow, 0, columns - 1));
        sheet.setPrintGridlines(false);
        sheet.setFitToPage(true);
        sheet.getPrintSetup().setFitWidth((short) 1);
        for (int index = 0; index < columns; index++) {
            sheet.autoSizeColumn(index);
            int width = Math.min(Math.max(sheet.getColumnWidth(index) + 800, 2800), 12000);
            sheet.setColumnWidth(index, width);
        }
    }

    private void configureWordDocument(XWPFDocument document) {
        CTSectPr section = document.getDocument().getBody().isSetSectPr()
                ? document.getDocument().getBody().getSectPr()
                : document.getDocument().getBody().addNewSectPr();
        CTPageSz pageSize = section.isSetPgSz() ? section.getPgSz() : section.addNewPgSz();
        pageSize.setW(BigInteger.valueOf(12240));
        pageSize.setH(BigInteger.valueOf(15840));

        CTPageMar margins = section.isSetPgMar() ? section.getPgMar() : section.addNewPgMar();
        BigInteger sideMargin = BigInteger.valueOf(1080);
        margins.setTop(sideMargin);
        margins.setRight(sideMargin);
        margins.setBottom(sideMargin);
        margins.setLeft(sideMargin);
        margins.setHeader(BigInteger.valueOf(720));
        margins.setFooter(BigInteger.valueOf(720));
        margins.setGutter(BigInteger.ZERO);
    }

    private void addWordStockSection(XWPFDocument document, boolean includeCosts) {
        addWordSectionHeading(document, "Productos");
        List<String> headers = new ArrayList<>(List.of(
                "Codigo", "Producto", "Categoria", "Unidad", "Stock", "Minimo", "Precio", "Estado"
        ));
        if (includeCosts) {
            headers.add(6, "Costo");
            headers.add(7, "Valor costo");
        }
        List<List<String>> rows = productRepository.findAll(Sort.by("name")).stream().map(product -> {
            List<String> values = new ArrayList<>(List.of(
                    product.getSku(),
                    product.getName(),
                    text(product.getCategoryName()),
                    product.getUnit(),
                    decimal(product.getCurrentStock()),
                    decimal(product.getMinimumStock()),
                    money(product.getSalePrice()),
                    product.isLowStock() ? "Stock bajo" : product.isActive() ? "Activo" : "Inactivo"
            ));
            if (includeCosts) {
                values.add(6, money(product.getCostPrice()));
                values.add(7, money(product.getCostPrice().multiply(product.getCurrentStock())));
            }
            return values;
        }).toList();
        addWordTable(document, headers, rows);
    }

    private void addWordMovementSection(XWPFDocument document, boolean includeCosts) {
        addWordSectionHeading(document, "Movimientos recientes");
        List<String> headers = new ArrayList<>(List.of("Fecha", "Producto", "Tipo", "Cantidad", "Referencia"));
        if (includeCosts) {
            headers.add(4, "Costo");
        }
        List<List<String>> rows = recentMovements().stream().map(movement -> {
            List<String> values = new ArrayList<>(List.of(
                    formatDateTime(movement.getCreatedAt()),
                    movement.getProduct().getName(),
                    movementType(movement),
                    decimal(movement.getQuantity()),
                    text(movement.getReference())
            ));
            if (includeCosts) {
                values.add(4, movement.getUnitCost() == null ? "" : money(movement.getUnitCost()));
            }
            return values;
        }).toList();
        addWordTable(document, headers, rows);
    }

    private void addWordCountSection(XWPFDocument document) {
        addWordSectionHeading(document, "Conteos fisicos");
        List<List<String>> rows = inventoryCountRepository.findTop20ByOrderByCountedAtDesc().stream()
                .map(count -> List.of(
                        count.getCountNumber(),
                        formatDateTime(count.getCountedAt()),
                        Integer.toString(count.getItems().size()),
                        Long.toString(count.getItems().stream().filter(item -> item.getDifference().signum() != 0).count())
                ))
                .toList();
        addWordTable(document, List.of("Conteo", "Fecha", "Productos", "Diferencias"), rows);
    }

    private void addWordOrderSection(XWPFDocument document) {
        addWordSectionHeading(document, "Ordenes de compra");
        List<List<String>> rows = purchaseOrderRepository.findAll(Sort.by("orderDate").descending()).stream()
                .map(order -> List.of(
                        order.getOrderNumber(),
                        formatDate(order.getOrderDate()),
                        order.getSupplier().getName(),
                        orderStatus(order.getStatus()),
                        money(order.getTotal())
                ))
                .toList();
        addWordTable(document, List.of("Orden", "Fecha", "Suplidor", "Estado", "Total"), rows);
    }

    private void addWordPurchaseInvoiceSection(XWPFDocument document) {
        addWordSectionHeading(document, "Facturas recibidas");
        List<List<String>> rows = purchaseInvoiceRepository.findAll(
                        Sort.by("invoiceDate").descending().and(Sort.by("createdAt").descending())
                ).stream()
                .map(invoice -> List.of(
                        formatDate(invoice.getInvoiceDate()),
                        invoice.getDocumentNumber(),
                        invoice.getSupplier().getName(),
                        paymentTerm(invoice.getPaymentTerm()),
                        formatDate(invoice.getDueDate()),
                        money(invoice.getTotal())
                ))
                .toList();
        addWordTable(
                document,
                List.of("Fecha", "Factura", "Suplidor", "Condicion", "Vencimiento", "Total"),
                rows
        );
    }

    private void addWordSupplierSection(XWPFDocument document) {
        addWordSectionHeading(document, "Suplidores");
        List<List<String>> rows = supplierRepository.findAll(Sort.by("name")).stream()
                .map(supplier -> List.of(
                        supplier.getName(),
                        text(supplier.getRnc()),
                        text(supplier.getPhone()),
                        text(supplier.getEmail()),
                        supplier.isActive() ? "Activo" : "Inactivo"
                ))
                .toList();
        addWordTable(document, List.of("Suplidor", "RNC", "Telefono", "Correo", "Estado"), rows);
    }

    private void addWordTitle(XWPFDocument document, String title, String subtitle) {
        XWPFParagraph brand = document.createParagraph();
        brand.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun brandRun = brand.createRun();
        brandRun.setText("FERRETERIA PERALTA");
        brandRun.setBold(true);
        brandRun.setColor("126B5D");
        brandRun.setFontSize(14);
        XWPFParagraph heading = document.createParagraph();
        heading.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun headingRun = heading.createRun();
        headingRun.setText(title);
        headingRun.setBold(true);
        headingRun.setFontSize(20);
        XWPFParagraph subheading = document.createParagraph();
        subheading.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun subheadingRun = subheading.createRun();
        subheadingRun.setText(subtitle + " | Generado " + formatDateTime(OffsetDateTime.now()));
        subheadingRun.setColor("5E6D67");
        subheadingRun.setFontSize(9);
    }

    private void addWordSectionHeading(XWPFDocument document, String title) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setSpacingBefore(220);
        XWPFRun run = paragraph.createRun();
        run.setText(title);
        run.setBold(true);
        run.setColor("17212B");
        run.setFontSize(14);
    }

    private void addWordMetadata(XWPFDocument document, List<String[]> metadata) {
        XWPFTable table = document.createTable(metadata.size(), 2);
        table.setWidth("100%");
        configureWordTableGrid(table, 2);
        for (int index = 0; index < metadata.size(); index++) {
            setWordCell(table.getRow(index).getCell(0), metadata.get(index)[0], true, "DCEAE5");
            setWordCell(table.getRow(index).getCell(1), metadata.get(index)[1], false, null);
        }
    }

    private void addWordTable(XWPFDocument document, List<String> headers, List<List<String>> rows) {
        XWPFTable table = document.createTable(Math.max(rows.size() + 1, 2), headers.size());
        table.setWidth("100%");
        configureWordTableGrid(table, headers.size());
        for (int index = 0; index < headers.size(); index++) {
            setWordCell(table.getRow(0).getCell(index), headers.get(index), true, "126B5D");
            table.getRow(0).getCell(index).getParagraphArray(0).getRuns().forEach(run -> run.setColor("FFFFFF"));
        }
        if (rows.isEmpty()) {
            setWordCell(table.getRow(1).getCell(0), "Sin registros", false, null);
            return;
        }
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            List<String> values = rows.get(rowIndex);
            for (int column = 0; column < headers.size(); column++) {
                setWordCell(
                        table.getRow(rowIndex + 1).getCell(column),
                        column < values.size() ? values.get(column) : "",
                        false,
                        rowIndex % 2 == 1 ? "F4F7F5" : null
                );
            }
        }
    }

    private void configureWordTableGrid(XWPFTable table, int columns) {
        CTTblGrid grid = table.getCTTbl().getTblGrid();
        if (grid == null) {
            grid = table.getCTTbl().addNewTblGrid();
        }
        BigInteger columnWidth = BigInteger.valueOf(10080L / Math.max(columns, 1));
        for (int index = 0; index < columns; index++) {
            grid.addNewGridCol().setW(columnWidth);
        }
    }

    private void setWordCell(XWPFTableCell cell, String value, boolean bold, String background) {
        cell.setText("");
        if (background != null) {
            cell.setColor(background);
        }
        XWPFParagraph paragraph = cell.getParagraphArray(0);
        XWPFRun run = paragraph.createRun();
        run.setText(value == null ? "" : value);
        run.setBold(bold);
        run.setFontSize(9);
    }

    private void addWordTotals(XWPFDocument document, BigDecimal subtotal, BigDecimal tax, BigDecimal total) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun run = paragraph.createRun();
        run.setText("Subtotal: " + money(subtotal)
                + "     ITBIS: " + money(tax)
                + "     TOTAL: " + money(total));
        run.setBold(true);
        run.setFontSize(11);
        run.setColor("17212B");
    }

    private void addWordNote(XWPFDocument document, String value) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(value);
        run.setItalic(true);
        run.setColor("5E6D67");
        run.setFontSize(9);
    }

    private PurchaseOrder findOrder(UUID orderId) {
        return purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Orden de compra no encontrada"));
    }

    private Quote findQuote(UUID quoteId) {
        return quoteRepository.findById(quoteId)
                .orElseThrow(() -> new IllegalArgumentException("Cotizacion no encontrada"));
    }

    private List<InventoryMovement> recentMovements() {
        return inventoryMovementRepository.findAll(
                PageRequest.of(0, 200, Sort.by("createdAt").descending())
        ).getContent();
    }

    private String movementType(InventoryMovement movement) {
        return switch (movement.getMovementType()) {
            case PURCHASE -> "Compra";
            case ADJUSTMENT_IN -> "Ajuste entrada";
            case ADJUSTMENT_OUT -> "Ajuste salida";
            case SALE -> "Venta";
            case RETURN -> "Devolucion";
        };
    }

    private String orderStatus(PurchaseOrderStatus status) {
        return switch (status) {
            case OPEN -> "Abierta";
            case RECEIVED -> "Recibida";
            case CANCELLED -> "Cancelada";
        };
    }

    private String paymentTerm(PurchasePaymentTerm paymentTerm) {
        return paymentTerm == PurchasePaymentTerm.CREDIT ? "Credito" : "Contado";
    }

    private String quoteStatus(QuoteStatus status) {
        return switch (status) {
            case DRAFT -> "Borrador";
            case SENT -> "Enviada";
            case APPROVED -> "Aprobada";
            case EXPIRED -> "Vencida";
            case CONVERTED -> "Convertida";
            case CANCELLED -> "Cancelada";
        };
    }

    private String money(BigDecimal value) {
        BigDecimal cleanValue = value == null ? BigDecimal.ZERO : value;
        return "RD$ " + String.format(Locale.US, "%,.2f", cleanValue);
    }

    private String decimal(BigDecimal value) {
        BigDecimal cleanValue = value == null ? BigDecimal.ZERO : value;
        return cleanValue.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    private String signed(BigDecimal value) {
        BigDecimal cleanValue = value == null ? BigDecimal.ZERO : value;
        return (cleanValue.signum() > 0 ? "+" : "") + decimal(cleanValue);
    }

    private String formatDate(LocalDate value) {
        return value == null ? "No definida" : value.format(DATE_FORMAT);
    }

    private String formatDateTime(OffsetDateTime value) {
        return value == null ? "" : value.format(DATE_TIME_FORMAT);
    }

    private String text(String value) {
        return value == null || value.isBlank() ? "No indicado" : value;
    }

    private record ExcelStyles(
            CellStyle company,
            CellStyle title,
            CellStyle subtitle,
            CellStyle header,
            CellStyle body,
            CellStyle label,
            CellStyle money,
            CellStyle quantity,
            CellStyle warningText,
            CellStyle warningNumber,
            CellStyle totalLabel,
            CellStyle totalMoney
    ) {
        private ExcelStyles(XSSFWorkbook workbook) {
            this(
                    createCompany(workbook),
                    createTitle(workbook),
                    createSubtitle(workbook),
                    createHeader(workbook),
                    createBody(workbook),
                    createLabel(workbook),
                    createNumber(workbook, "\"RD$\" #,##0.00", false),
                    createNumber(workbook, "#,##0.00", false),
                    createWarning(workbook, false),
                    createWarning(workbook, true),
                    createTotal(workbook, false),
                    createTotal(workbook, true)
            );
        }

        private static CellStyle createCompany(XSSFWorkbook workbook) {
            XSSFCellStyle style = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setBold(true);
            font.setColor(xlsxColor(18, 107, 93));
            font.setFontHeightInPoints((short) 13);
            style.setFont(font);
            return style;
        }

        private static CellStyle createTitle(XSSFWorkbook workbook) {
            XSSFCellStyle style = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setBold(true);
            font.setColor(xlsxColor(23, 33, 43));
            font.setFontHeightInPoints((short) 19);
            style.setFont(font);
            return style;
        }

        private static CellStyle createSubtitle(XSSFWorkbook workbook) {
            XSSFCellStyle style = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setColor(xlsxColor(94, 109, 103));
            font.setFontHeightInPoints((short) 9);
            style.setFont(font);
            return style;
        }

        private static CellStyle createHeader(XSSFWorkbook workbook) {
            XSSFCellStyle style = bordered(workbook);
            XSSFFont font = workbook.createFont();
            font.setBold(true);
            font.setColor(xlsxColor(255, 255, 255));
            style.setFont(font);
            style.setFillForegroundColor(xlsxColor(18, 107, 93));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            return style;
        }

        private static CellStyle createBody(XSSFWorkbook workbook) {
            XSSFCellStyle style = bordered(workbook);
            style.setAlignment(HorizontalAlignment.LEFT);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setDataFormat(workbook.createDataFormat().getFormat("@"));
            return style;
        }

        private static CellStyle createLabel(XSSFWorkbook workbook) {
            XSSFCellStyle style = (XSSFCellStyle) createBody(workbook);
            XSSFFont font = workbook.createFont();
            font.setBold(true);
            font.setColor(xlsxColor(23, 33, 43));
            style.setFont(font);
            style.setFillForegroundColor(xlsxColor(220, 234, 229));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            return style;
        }

        private static CellStyle createNumber(XSSFWorkbook workbook, String format, boolean warning) {
            XSSFCellStyle style = (XSSFCellStyle) (warning ? createWarning(workbook, true) : createBody(workbook));
            style.setDataFormat(workbook.createDataFormat().getFormat(format));
            style.setAlignment(HorizontalAlignment.RIGHT);
            return style;
        }

        private static CellStyle createWarning(XSSFWorkbook workbook, boolean number) {
            XSSFCellStyle style = bordered(workbook);
            XSSFFont font = workbook.createFont();
            font.setBold(true);
            font.setColor(xlsxColor(161, 54, 44));
            style.setFont(font);
            style.setFillForegroundColor(xlsxColor(251, 228, 226));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            if (number) {
                style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
                style.setAlignment(HorizontalAlignment.RIGHT);
            }
            return style;
        }

        private static CellStyle createTotal(XSSFWorkbook workbook, boolean money) {
            XSSFCellStyle style = bordered(workbook);
            XSSFFont font = workbook.createFont();
            font.setBold(true);
            font.setColor(xlsxColor(255, 255, 255));
            style.setFont(font);
            style.setFillForegroundColor(xlsxColor(23, 33, 43));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            if (money) {
                style.setDataFormat(workbook.createDataFormat().getFormat("\"RD$\" #,##0.00"));
                style.setAlignment(HorizontalAlignment.RIGHT);
            }
            return style;
        }

        private static XSSFCellStyle bordered(XSSFWorkbook workbook) {
            XSSFCellStyle style = workbook.createCellStyle();
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            XSSFColor border = xlsxColor(215, 222, 217);
            style.setBottomBorderColor(border);
            style.setTopBorderColor(border);
            style.setLeftBorderColor(border);
            style.setRightBorderColor(border);
            return style;
        }

        private static XSSFColor xlsxColor(int red, int green, int blue) {
            return new XSSFColor(new Color(red, green, blue), new DefaultIndexedColorMap());
        }
    }
}
