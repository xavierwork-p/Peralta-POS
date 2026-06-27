create table fiscal_ecf_documents (
    id char(36) primary key default (uuid()),
    sale_id char(36) not null unique,
    status varchar(40) not null default 'READY_TO_SIGN',
    unsigned_xml longtext not null,
    signed_xml longtext,
    xml_hash varchar(128),
    signature_value varchar(512),
    track_id varchar(80),
    security_code varchar(80),
    acknowledgement_xml longtext,
    generated_at datetime(6) not null default current_timestamp(6),
    submitted_at datetime(6),
    accepted_at datetime(6),
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint fk_fiscal_ecf_documents_sale foreign key (sale_id) references sales(id)
) comment = 'e-CF simulado: XML, firma simulada, trackId y acuse de prueba para preparar la integracion DGII real';

create table accounting_accounts (
    id char(36) primary key default (uuid()),
    code varchar(30) not null unique,
    name varchar(160) not null,
    account_type varchar(40) not null,
    normal_balance varchar(10) not null,
    allow_reconciliation boolean not null default false,
    active boolean not null default true,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6)
) comment = 'Catalogo de cuentas base, inspirado en la estructura de contabilidad completa tipo ERP';

create table accounting_journals (
    id char(36) primary key default (uuid()),
    code varchar(20) not null unique,
    name varchar(120) not null,
    journal_type varchar(40) not null,
    active boolean not null default true,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6)
) comment = 'Diarios contables: ventas, compras, caja, banco y operaciones varias';

create table accounting_entries (
    id char(36) primary key default (uuid()),
    journal_id char(36) not null,
    entry_number varchar(40) not null unique,
    entry_date date not null,
    reference varchar(160),
    source_type varchar(40),
    source_id char(36),
    status varchar(30) not null default 'POSTED',
    total_debit decimal(14,2) not null default 0.00,
    total_credit decimal(14,2) not null default 0.00,
    notes varchar(600),
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint fk_accounting_entries_journal foreign key (journal_id) references accounting_journals(id),
    constraint chk_accounting_entries_balanced check (total_debit = total_credit)
) comment = 'Asientos contables posteados con debitos y creditos balanceados';

create table accounting_entry_lines (
    id char(36) primary key default (uuid()),
    entry_id char(36) not null,
    account_id char(36) not null,
    label varchar(220) not null,
    partner_name varchar(160),
    debit decimal(14,2) not null default 0.00,
    credit decimal(14,2) not null default 0.00,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint fk_accounting_entry_lines_entry foreign key (entry_id) references accounting_entries(id) on delete cascade,
    constraint fk_accounting_entry_lines_account foreign key (account_id) references accounting_accounts(id),
    constraint chk_accounting_entry_lines_amount check (debit >= 0 and credit >= 0 and not (debit > 0 and credit > 0))
) comment = 'Lineas de asientos contables para mayor general y balance de comprobacion';

create index idx_fiscal_ecf_documents_sale on fiscal_ecf_documents(sale_id);
create index idx_accounting_entries_source on accounting_entries(source_type, source_id);
create index idx_accounting_entries_date on accounting_entries(entry_date);
create index idx_accounting_entry_lines_account on accounting_entry_lines(account_id);

insert into accounting_accounts (code, name, account_type, normal_balance, allow_reconciliation, active) values
('1101', 'Caja general', 'BANK_CASH', 'DEBIT', true, true),
('1102', 'Banco general', 'BANK_CASH', 'DEBIT', true, true),
('1103', 'Tarjetas por cobrar', 'RECEIVABLE', 'DEBIT', true, true),
('1201', 'Cuentas por cobrar clientes', 'RECEIVABLE', 'DEBIT', true, true),
('1301', 'Inventario de mercancia', 'CURRENT_ASSET', 'DEBIT', false, true),
('2101', 'Cuentas por pagar suplidores', 'PAYABLE', 'CREDIT', true, true),
('2201', 'ITBIS por pagar', 'TAX', 'CREDIT', true, true),
('2202', 'ITBIS adelantado en compras', 'TAX', 'DEBIT', true, true),
('3101', 'Capital', 'EQUITY', 'CREDIT', false, true),
('4101', 'Ingresos por ventas', 'INCOME', 'CREDIT', false, true),
('5101', 'Costo de ventas', 'EXPENSE', 'DEBIT', false, true),
('5201', 'Gastos generales', 'EXPENSE', 'DEBIT', false, true),
('5301', 'Descuentos sobre ventas', 'EXPENSE', 'DEBIT', false, true);

insert into accounting_journals (code, name, journal_type, active) values
('VEN', 'Diario de ventas', 'CUSTOMER_INVOICE', true),
('COM', 'Diario de compras', 'VENDOR_BILL', true),
('CAJ', 'Diario de caja', 'CASH', true),
('BAN', 'Diario de banco', 'BANK', true),
('MISC', 'Operaciones varias', 'GENERAL', true);
