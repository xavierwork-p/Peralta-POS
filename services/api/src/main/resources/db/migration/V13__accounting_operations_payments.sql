create table accounting_payments (
    id char(36) primary key default (uuid()),
    direction varchar(30) not null,
    receivable_id char(36),
    payable_id char(36),
    journal_id char(36) not null,
    payment_date date not null,
    method varchar(40) not null,
    party_name varchar(160),
    amount decimal(14,2) not null,
    reference varchar(160),
    notes varchar(600),
    reconciled boolean not null default true,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint fk_accounting_payments_receivable foreign key (receivable_id) references accounts_receivable(id),
    constraint fk_accounting_payments_payable foreign key (payable_id) references accounts_payable(id),
    constraint fk_accounting_payments_journal foreign key (journal_id) references accounting_journals(id),
    constraint chk_accounting_payment_direction_document check (
        (direction = 'CUSTOMER' and receivable_id is not null and payable_id is null)
        or (direction = 'VENDOR' and payable_id is not null and receivable_id is null)
    )
) comment = 'Pagos y cobros contables aplicados a cuentas por cobrar o por pagar';

create index idx_accounting_payments_receivable on accounting_payments(receivable_id);
create index idx_accounting_payments_payable on accounting_payments(payable_id);
create index idx_accounting_payments_date on accounting_payments(payment_date);
