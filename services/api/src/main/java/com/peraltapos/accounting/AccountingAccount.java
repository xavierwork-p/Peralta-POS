package com.peraltapos.accounting;

import com.peraltapos.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounting_accounts")
public class AccountingAccount extends BaseEntity {

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 160)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 40)
    private AccountingAccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "normal_balance", nullable = false, length = 10)
    private NormalBalance normalBalance;

    @Column(name = "allow_reconciliation", nullable = false)
    private boolean allowReconciliation;

    @Column(nullable = false)
    private boolean active = true;

    public static AccountingAccount from(AccountingAccountRequest request) {
        AccountingAccount account = new AccountingAccount();
        account.updateFrom(request);
        return account;
    }

    public void updateFrom(AccountingAccountRequest request) {
        code = clean(request.code()).toUpperCase();
        name = clean(request.name());
        accountType = request.accountType();
        normalBalance = request.normalBalance();
        allowReconciliation = request.allowReconciliation();
        active = request.active();
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public AccountingAccountType getAccountType() {
        return accountType;
    }

    public NormalBalance getNormalBalance() {
        return normalBalance;
    }

    public boolean isAllowReconciliation() {
        return allowReconciliation;
    }

    public boolean isActive() {
        return active;
    }
}
