package com.peraltapos.accounting;

import com.peraltapos.common.web.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounting")
public class AccountingController {

    private final AccountingService accountingService;

    public AccountingController(AccountingService accountingService) {
        this.accountingService = accountingService;
    }

    @GetMapping("/summary")
    public ApiResponse<AccountingSummaryResponse> summary() {
        return ApiResponse.ok(accountingService.summary());
    }

    @GetMapping("/accounts")
    public ApiResponse<List<AccountingAccountResponse>> accounts() {
        return ApiResponse.ok(accountingService.accounts());
    }

    @GetMapping("/journals")
    public ApiResponse<List<AccountingJournalResponse>> journals() {
        return ApiResponse.ok(accountingService.journals());
    }

    @GetMapping("/entries")
    public ApiResponse<List<JournalEntryResponse>> entries() {
        return ApiResponse.ok(accountingService.entries());
    }

    @GetMapping("/trial-balance")
    public ApiResponse<List<TrialBalanceLineResponse>> trialBalance() {
        return ApiResponse.ok(accountingService.trialBalance());
    }

    @GetMapping("/payments")
    public ApiResponse<List<AccountingPaymentResponse>> payments() {
        return ApiResponse.ok(accountingService.payments());
    }

    @PostMapping("/accounts")
    public ApiResponse<AccountingAccountResponse> createAccount(@RequestBody AccountingAccountRequest request) {
        return ApiResponse.ok(accountingService.createAccount(request));
    }

    @PutMapping("/accounts/{id}")
    public ApiResponse<AccountingAccountResponse> updateAccount(@PathVariable UUID id, @RequestBody AccountingAccountRequest request) {
        return ApiResponse.ok(accountingService.updateAccount(id, request));
    }

    @PostMapping("/journals")
    public ApiResponse<AccountingJournalResponse> createJournal(@RequestBody AccountingJournalRequest request) {
        return ApiResponse.ok(accountingService.createJournal(request));
    }

    @PutMapping("/journals/{id}")
    public ApiResponse<AccountingJournalResponse> updateJournal(@PathVariable UUID id, @RequestBody AccountingJournalRequest request) {
        return ApiResponse.ok(accountingService.updateJournal(id, request));
    }

    @PostMapping("/entries/manual")
    public ApiResponse<JournalEntryResponse> createManualEntry(@RequestBody ManualJournalEntryRequest request) {
        return ApiResponse.ok(accountingService.createManualEntry(request));
    }

    @PostMapping("/receivables/{id}/payments")
    public ApiResponse<AccountingPaymentResponse> registerReceivablePayment(@PathVariable UUID id, @RequestBody AccountingPaymentRequest request) {
        return ApiResponse.ok(accountingService.registerReceivablePayment(id, request));
    }

    @PostMapping("/payables/{id}/payments")
    public ApiResponse<AccountingPaymentResponse> registerPayablePayment(@PathVariable UUID id, @RequestBody AccountingPaymentRequest request) {
        return ApiResponse.ok(accountingService.registerPayablePayment(id, request));
    }
}
