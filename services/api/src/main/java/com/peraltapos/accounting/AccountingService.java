package com.peraltapos.accounting;

import com.peraltapos.accounting.payable.AccountPayableRepository;
import com.peraltapos.accounting.payable.AccountPayableStatus;
import com.peraltapos.accounting.receivable.AccountReceivableRepository;
import com.peraltapos.accounting.receivable.AccountReceivableStatus;
import com.peraltapos.common.web.BusinessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AccountingService {

    private final AccountReceivableRepository receivableRepository;
    private final AccountPayableRepository payableRepository;
    private final AccountingAccountRepository accountRepository;
    private final AccountingJournalRepository journalRepository;
    private final JournalEntryRepository entryRepository;
    private final JournalEntryLineRepository entryLineRepository;
    private final AccountingPaymentRepository paymentRepository;
    private final AccountingPostingService postingService;

    public AccountingService(
            AccountReceivableRepository receivableRepository,
            AccountPayableRepository payableRepository,
            AccountingAccountRepository accountRepository,
            AccountingJournalRepository journalRepository,
            JournalEntryRepository entryRepository,
            JournalEntryLineRepository entryLineRepository,
            AccountingPaymentRepository paymentRepository,
            AccountingPostingService postingService
    ) {
        this.receivableRepository = receivableRepository;
        this.payableRepository = payableRepository;
        this.accountRepository = accountRepository;
        this.journalRepository = journalRepository;
        this.entryRepository = entryRepository;
        this.entryLineRepository = entryLineRepository;
        this.paymentRepository = paymentRepository;
        this.postingService = postingService;
    }

    @Transactional(readOnly = true)
    public AccountingSummaryResponse summary() {
        BigDecimal receivablesBalance = receivableRepository.sumBalanceByStatusIn(List.of(
                AccountReceivableStatus.PENDING,
                AccountReceivableStatus.OVERDUE
        ));
        BigDecimal payablesBalance = payableRepository.sumBalanceByStatusIn(List.of(
                AccountPayableStatus.PENDING,
                AccountPayableStatus.OVERDUE
        ));

        return new AccountingSummaryResponse(
                receivablesBalance,
                payablesBalance,
                receivablesBalance.subtract(payablesBalance),
                receivableRepository.findAll(Sort.by("dueDate").ascending()).stream()
                        .limit(20)
                        .map(AccountingDocumentResponse::from)
                        .toList(),
                payableRepository.findAll(Sort.by("dueDate").ascending()).stream()
                        .limit(20)
                        .map(AccountingDocumentResponse::from)
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public List<AccountingAccountResponse> accounts() {
        return accountRepository.findAllByActiveTrueOrderByCodeAsc()
                .stream()
                .map(AccountingAccountResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AccountingJournalResponse> journals() {
        return journalRepository.findAllByActiveTrueOrderByCodeAsc()
                .stream()
                .map(AccountingJournalResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<JournalEntryResponse> entries() {
        return entryRepository.findAll(Sort.by("entryDate").descending().and(Sort.by("entryNumber").descending()))
                .stream()
                .limit(50)
                .map(JournalEntryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TrialBalanceLineResponse> trialBalance() {
        return entryLineRepository.trialBalance();
    }

    @Transactional(readOnly = true)
    public List<AccountingPaymentResponse> payments() {
        return paymentRepository.findAll(Sort.by("paymentDate").descending().and(Sort.by("createdAt").descending()))
                .stream()
                .limit(100)
                .map(AccountingPaymentResponse::from)
                .toList();
    }

    @Transactional
    public AccountingAccountResponse createAccount(AccountingAccountRequest request) {
        validateAccountRequest(request);
        String code = request.code().trim().toUpperCase();
        accountRepository.findByCode(code).ifPresent(existing -> {
            throw new BusinessException("Ya existe una cuenta con ese codigo");
        });
        return AccountingAccountResponse.from(accountRepository.save(AccountingAccount.from(request)));
    }

    @Transactional
    public AccountingAccountResponse updateAccount(UUID id, AccountingAccountRequest request) {
        validateAccountRequest(request);
        AccountingAccount account = accountRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cuenta contable no encontrada"));
        String code = request.code().trim().toUpperCase();
        accountRepository.findByCode(code)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Ya existe otra cuenta con ese codigo");
                });
        account.updateFrom(request);
        return AccountingAccountResponse.from(accountRepository.save(account));
    }

    @Transactional
    public AccountingJournalResponse createJournal(AccountingJournalRequest request) {
        validateJournalRequest(request);
        String code = request.code().trim().toUpperCase();
        journalRepository.findByCode(code).ifPresent(existing -> {
            throw new BusinessException("Ya existe un diario con ese codigo");
        });
        return AccountingJournalResponse.from(journalRepository.save(AccountingJournal.from(request)));
    }

    @Transactional
    public AccountingJournalResponse updateJournal(UUID id, AccountingJournalRequest request) {
        validateJournalRequest(request);
        AccountingJournal journal = journalRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Diario contable no encontrado"));
        String code = request.code().trim().toUpperCase();
        journalRepository.findByCode(code)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Ya existe otro diario con ese codigo");
                });
        journal.updateFrom(request);
        return AccountingJournalResponse.from(journalRepository.save(journal));
    }

    @Transactional
    public JournalEntryResponse createManualEntry(ManualJournalEntryRequest request) {
        return JournalEntryResponse.from(postingService.postManualEntry(request));
    }

    @Transactional
    public AccountingPaymentResponse registerReceivablePayment(UUID receivableId, AccountingPaymentRequest request) {
        com.peraltapos.accounting.receivable.AccountReceivable receivable = receivableRepository.findById(receivableId)
                .orElseThrow(() -> new BusinessException("Cuenta por cobrar no encontrada"));
        if (receivable.getStatus() == AccountReceivableStatus.PAID || receivable.getStatus() == AccountReceivableStatus.CANCELLED) {
            throw new BusinessException("Esta cuenta por cobrar ya no admite pagos");
        }

        AccountingPaymentRequest cleanRequest = normalizePaymentRequest(request);
        validatePaymentAmount(cleanRequest.amount(), receivable.getBalance());
        AccountingPayment payment = paymentRepository.save(AccountingPayment.customerPayment(
                receivable,
                journalForPayment(cleanRequest),
                cleanRequest
        ));
        receivable.applyPayment(cleanRequest.amount());
        postingService.postCustomerPayment(payment);
        return AccountingPaymentResponse.from(payment);
    }

    @Transactional
    public AccountingPaymentResponse registerPayablePayment(UUID payableId, AccountingPaymentRequest request) {
        com.peraltapos.accounting.payable.AccountPayable payable = payableRepository.findById(payableId)
                .orElseThrow(() -> new BusinessException("Cuenta por pagar no encontrada"));
        if (payable.getStatus() == AccountPayableStatus.PAID) {
            throw new BusinessException("Esta cuenta por pagar ya no admite pagos");
        }

        AccountingPaymentRequest cleanRequest = normalizePaymentRequest(request);
        validatePaymentAmount(cleanRequest.amount(), payable.getBalance());
        AccountingPayment payment = paymentRepository.save(AccountingPayment.vendorPayment(
                payable,
                journalForPayment(cleanRequest),
                cleanRequest
        ));
        payable.applyPayment(cleanRequest.amount());
        postingService.postVendorPayment(payment);
        return AccountingPaymentResponse.from(payment);
    }

    private void validateAccountRequest(AccountingAccountRequest request) {
        if (request.code() == null || request.code().isBlank()) {
            throw new BusinessException("El codigo de la cuenta es obligatorio");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new BusinessException("El nombre de la cuenta es obligatorio");
        }
        if (request.accountType() == null) {
            throw new BusinessException("El tipo de cuenta es obligatorio");
        }
        if (request.normalBalance() == null) {
            throw new BusinessException("El balance normal es obligatorio");
        }
    }

    private void validateJournalRequest(AccountingJournalRequest request) {
        if (request.code() == null || request.code().isBlank()) {
            throw new BusinessException("El codigo del diario es obligatorio");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new BusinessException("El nombre del diario es obligatorio");
        }
        if (request.journalType() == null) {
            throw new BusinessException("El tipo de diario es obligatorio");
        }
    }

    private AccountingPaymentRequest normalizePaymentRequest(AccountingPaymentRequest request) {
        if (request == null) {
            throw new BusinessException("Datos de pago requeridos");
        }
        if (request.method() == null) {
            throw new BusinessException("Selecciona el metodo de pago");
        }
        return new AccountingPaymentRequest(
                request.amount(),
                request.paymentDate() == null ? LocalDate.now() : request.paymentDate(),
                request.method(),
                request.journalCode(),
                request.reference(),
                request.notes()
        );
    }

    private void validatePaymentAmount(BigDecimal amount, BigDecimal balance) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto del pago debe ser mayor a cero");
        }
        if (amount.compareTo(balance) > 0) {
            throw new BusinessException("El pago no puede exceder el balance pendiente");
        }
    }

    private AccountingJournal journalForPayment(AccountingPaymentRequest request) {
        String journalCode = request.journalCode();
        if (journalCode == null || journalCode.isBlank()) {
            journalCode = request.method() == AccountingPaymentMethod.CASH ? "CAJ" : "BAN";
        }
        String cleanJournalCode = journalCode.trim().toUpperCase();
        return journalRepository.findByCode(cleanJournalCode)
                .orElseThrow(() -> new BusinessException("Diario contable no configurado: " + cleanJournalCode));
    }
}
