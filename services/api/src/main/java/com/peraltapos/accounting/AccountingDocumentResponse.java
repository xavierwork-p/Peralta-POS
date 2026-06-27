package com.peraltapos.accounting;

import com.peraltapos.accounting.payable.AccountPayable;
import com.peraltapos.accounting.receivable.AccountReceivable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AccountingDocumentResponse(
        UUID id,
        String partyName,
        String documentNumber,
        BigDecimal amount,
        BigDecimal balance,
        LocalDate dueDate,
        String status
) {
    public static AccountingDocumentResponse from(AccountReceivable account) {
        return new AccountingDocumentResponse(
                account.getId(),
                account.getCustomer() == null ? "Cliente no registrado" : account.getCustomer().getName(),
                account.getSale() == null ? null : account.getSale().getInvoiceNumber(),
                account.getAmount(),
                account.getBalance(),
                account.getDueDate(),
                account.getStatus().name()
        );
    }

    public static AccountingDocumentResponse from(AccountPayable account) {
        return new AccountingDocumentResponse(
                account.getId(),
                account.getSupplier() == null ? "Suplidor no registrado" : account.getSupplier().getName(),
                account.getDocumentNumber(),
                account.getAmount(),
                account.getBalance(),
                account.getDueDate(),
                account.getStatus().name()
        );
    }
}
