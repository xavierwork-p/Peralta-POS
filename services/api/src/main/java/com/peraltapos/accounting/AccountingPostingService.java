package com.peraltapos.accounting;

import com.peraltapos.common.web.BusinessException;
import com.peraltapos.purchasing.PurchaseInvoice;
import com.peraltapos.purchasing.PurchasePaymentTerm;
import com.peraltapos.sales.sale.Payment;
import com.peraltapos.sales.sale.PaymentMethod;
import com.peraltapos.sales.sale.Sale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountingPostingService {

    private static final String SALE_SOURCE = "SALE";
    private static final String PURCHASE_SOURCE = "PURCHASE_INVOICE";
    private static final String PAYMENT_SOURCE = "ACCOUNTING_PAYMENT";
    private static final String MANUAL_SOURCE = "MANUAL";
    private static final DateTimeFormatter ENTRY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final AccountingAccountRepository accountRepository;
    private final AccountingJournalRepository journalRepository;
    private final JournalEntryRepository entryRepository;

    public AccountingPostingService(
            AccountingAccountRepository accountRepository,
            AccountingJournalRepository journalRepository,
            JournalEntryRepository entryRepository
    ) {
        this.accountRepository = accountRepository;
        this.journalRepository = journalRepository;
        this.entryRepository = entryRepository;
    }

    @Transactional
    public void postSale(Sale sale) {
        if (entryRepository.existsBySourceTypeAndSourceId(SALE_SOURCE, sale.getId())) {
            return;
        }

        AccountingJournal journal = journal("VEN");
        String partnerName = sale.getCustomerName() == null || sale.getCustomerName().isBlank()
                ? "Cliente de contado"
                : sale.getCustomerName();
        String reference = sale.getInvoiceNumber();

        List<JournalEntryLine> lines = new ArrayList<>();
        Map<PaymentMethod, BigDecimal> payments = paymentTotals(sale);

        addPaymentDebit(lines, payments, PaymentMethod.CASH, "1101", "Pago en efectivo " + reference, partnerName);
        addPaymentDebit(lines, payments, PaymentMethod.TRANSFER, "1102", "Transferencia recibida " + reference, partnerName);
        addPaymentDebit(lines, payments, PaymentMethod.CARD, "1103", "Tarjeta pendiente de liquidar " + reference, partnerName);
        addPaymentDebit(lines, payments, PaymentMethod.CREDIT, "1201", "Cuenta por cobrar " + reference, partnerName);

        if (positive(sale.getDiscountTotal())) {
            lines.add(JournalEntryLine.debit(account("5301"), "Descuento concedido " + reference, partnerName, sale.getDiscountTotal()));
        }

        if (positive(sale.getSubtotal())) {
            lines.add(JournalEntryLine.credit(account("4101"), "Venta de mercancia " + reference, partnerName, sale.getSubtotal()));
        }

        if (positive(sale.getTaxTotal())) {
            lines.add(JournalEntryLine.credit(account("2201"), "ITBIS facturado " + reference, partnerName, sale.getTaxTotal()));
        }

        LocalDate entryDate = sale.getIssuedAt().toLocalDate();
        postEntry(
                journal,
                entryDate,
                reference,
                SALE_SOURCE,
                sale.getId(),
                "Asiento automatico generado desde venta/factura",
                lines
        );
    }

    @Transactional
    public void postPurchaseInvoice(PurchaseInvoice invoice) {
        if (entryRepository.existsBySourceTypeAndSourceId(PURCHASE_SOURCE, invoice.getId())) {
            return;
        }

        String reference = invoice.getDocumentNumber();
        String partnerName = invoice.getSupplier().getName();
        List<JournalEntryLine> lines = new ArrayList<>();

        if (positive(invoice.getSubtotal())) {
            lines.add(JournalEntryLine.debit(account("1301"), "Compra de inventario " + reference, partnerName, invoice.getSubtotal()));
        }

        if (positive(invoice.getTaxTotal())) {
            lines.add(JournalEntryLine.debit(account("2202"), "ITBIS adelantado compra " + reference, partnerName, invoice.getTaxTotal()));
        }

        if (invoice.getPaymentTerm() == PurchasePaymentTerm.CREDIT) {
            lines.add(JournalEntryLine.credit(account("2101"), "Cuenta por pagar " + reference, partnerName, invoice.getTotal()));
        } else {
            lines.add(JournalEntryLine.credit(account("1101"), "Pago de compra contado " + reference, partnerName, invoice.getTotal()));
        }

        postEntry(
                journal("COM"),
                invoice.getInvoiceDate(),
                reference,
                PURCHASE_SOURCE,
                invoice.getId(),
                "Asiento automatico generado desde factura de compra",
                lines
        );
    }

    @Transactional
    public void postCustomerPayment(AccountingPayment payment) {
        String reference = payment.getReference() == null || payment.getReference().isBlank()
                ? "Cobro " + payment.getId()
                : payment.getReference();
        String partnerName = payment.getPartyName();
        List<JournalEntryLine> lines = List.of(
                JournalEntryLine.debit(account(paymentAccountCode(payment.getMethod(), payment.getDirection())), "Cobro recibido " + reference, partnerName, payment.getAmount()),
                JournalEntryLine.credit(account("1201"), "Conciliacion cuenta por cobrar " + reference, partnerName, payment.getAmount())
        );

        postEntry(
                payment.getJournal(),
                payment.getPaymentDate(),
                reference,
                PAYMENT_SOURCE,
                payment.getId(),
                "Cobro aplicado y conciliado contra cuenta por cobrar",
                lines
        );
    }

    @Transactional
    public void postVendorPayment(AccountingPayment payment) {
        String reference = payment.getReference() == null || payment.getReference().isBlank()
                ? "Pago " + payment.getId()
                : payment.getReference();
        String partnerName = payment.getPartyName();
        List<JournalEntryLine> lines = List.of(
                JournalEntryLine.debit(account("2101"), "Conciliacion cuenta por pagar " + reference, partnerName, payment.getAmount()),
                JournalEntryLine.credit(account(paymentAccountCode(payment.getMethod(), payment.getDirection())), "Pago realizado " + reference, partnerName, payment.getAmount())
        );

        postEntry(
                payment.getJournal(),
                payment.getPaymentDate(),
                reference,
                PAYMENT_SOURCE,
                payment.getId(),
                "Pago aplicado y conciliado contra cuenta por pagar",
                lines
        );
    }

    @Transactional
    public JournalEntry postManualEntry(ManualJournalEntryRequest request) {
        if (request.lines() == null || request.lines().size() < 2) {
            throw new BusinessException("Un asiento manual necesita al menos dos lineas");
        }

        List<JournalEntryLine> lines = request.lines()
                .stream()
                .map(this::manualLine)
                .toList();

        return postEntry(
                journal(cleanReference(request.journalCode(), "MISC").toUpperCase()),
                request.entryDate() == null ? LocalDate.now() : request.entryDate(),
                cleanReference(request.reference(), "Asiento manual"),
                MANUAL_SOURCE,
                null,
                request.notes(),
                lines
        );
    }

    private Map<PaymentMethod, BigDecimal> paymentTotals(Sale sale) {
        Map<PaymentMethod, BigDecimal> totals = new EnumMap<>(PaymentMethod.class);
        for (Payment payment : sale.getPayments()) {
            totals.merge(payment.getMethod(), payment.getAmount(), BigDecimal::add);
        }
        return totals;
    }

    private void addPaymentDebit(
            List<JournalEntryLine> lines,
            Map<PaymentMethod, BigDecimal> payments,
            PaymentMethod method,
            String accountCode,
            String label,
            String partnerName
    ) {
        BigDecimal amount = payments.getOrDefault(method, BigDecimal.ZERO);
        if (positive(amount)) {
            lines.add(JournalEntryLine.debit(account(accountCode), label, partnerName, amount));
        }
    }

    private AccountingAccount account(String code) {
        return accountRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException("Cuenta contable no configurada: " + code));
    }

    private AccountingJournal journal(String code) {
        return journalRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException("Diario contable no configurado: " + code));
    }

    private boolean positive(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    private JournalEntry postEntry(
            AccountingJournal journal,
            LocalDate entryDate,
            String reference,
            String sourceType,
            java.util.UUID sourceId,
            String notes,
            List<JournalEntryLine> lines
    ) {
        BigDecimal totalDebit = lines.stream().map(JournalEntryLine::getDebit).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredit = lines.stream().map(JournalEntryLine::getCredit).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new BusinessException("El asiento no cuadra. Debitos: " + totalDebit + ", creditos: " + totalCredit);
        }
        if (totalDebit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El asiento debe tener monto mayor a cero");
        }

        JournalEntry entry = new JournalEntry();
        entry.post(
                journal,
                generateEntryNumber(entryDate),
                entryDate,
                reference,
                sourceType,
                sourceId,
                notes,
                lines
        );
        return entryRepository.save(entry);
    }

    private JournalEntryLine manualLine(ManualJournalEntryLineRequest request) {
        BigDecimal debit = zeroIfNull(request.debit());
        BigDecimal credit = zeroIfNull(request.credit());
        if (debit.compareTo(BigDecimal.ZERO) > 0 && credit.compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("Una linea no puede tener debito y credito a la vez");
        }
        if (debit.compareTo(BigDecimal.ZERO) <= 0 && credit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Cada linea debe tener debito o credito");
        }

        AccountingAccount account = account(request.accountCode());
        if (debit.compareTo(BigDecimal.ZERO) > 0) {
            return JournalEntryLine.debit(account, cleanReference(request.label(), "Movimiento contable"), request.partnerName(), debit);
        }
        return JournalEntryLine.credit(account, cleanReference(request.label(), "Movimiento contable"), request.partnerName(), credit);
    }

    private String paymentAccountCode(AccountingPaymentMethod method, AccountingPaymentDirection direction) {
        if (method == AccountingPaymentMethod.CASH) {
            return "1101";
        }
        if (method == AccountingPaymentMethod.CARD && direction == AccountingPaymentDirection.CUSTOMER) {
            return "1103";
        }
        return "1102";
    }

    private BigDecimal zeroIfNull(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private String cleanReference(String value, String fallback) {
        String cleanValue = value == null ? null : value.trim();
        return cleanValue == null || cleanValue.isBlank() ? fallback : cleanValue;
    }

    private String generateEntryNumber(LocalDate entryDate) {
        long next = entryRepository.count() + 1;
        return "AST-" + entryDate.format(ENTRY_DATE_FORMAT) + "-" + String.format("%05d", next);
    }
}
