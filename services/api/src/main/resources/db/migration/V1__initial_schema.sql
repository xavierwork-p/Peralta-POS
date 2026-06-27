-- Peralta POS - esquema inicial para MySQL 8.
-- Esta migracion crea la base empresarial del sistema: empresa, inventario, clientes,
-- empleados, NCF, cotizaciones, ventas, cuentas y auditoria.

create table companies (
    id char(36) primary key default (uuid()),
    name varchar(160) not null,
    commercial_name varchar(160),
    rnc varchar(30),
    phone varchar(40),
    email varchar(160),
    address varchar(300),
    logo_url varchar(500),
    currency_code varchar(10) not null default 'DOP',
    tax_rate decimal(5,2) not null default 18.00,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6)
) comment = 'Datos generales de la empresa configurada en el sistema';

create table branches (
    id char(36) primary key default (uuid()),
    company_id char(36),
    name varchar(120) not null,
    phone varchar(40),
    address varchar(300),
    active boolean not null default true,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint fk_branches_company foreign key (company_id) references companies(id)
);

create table warehouses (
    id char(36) primary key default (uuid()),
    branch_id char(36),
    name varchar(120) not null,
    active boolean not null default true,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint fk_warehouses_branch foreign key (branch_id) references branches(id)
);

create table cash_registers (
    id char(36) primary key default (uuid()),
    branch_id char(36),
    name varchar(120) not null,
    active boolean not null default true,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint fk_cash_registers_branch foreign key (branch_id) references branches(id)
);

create table products (
    id char(36) primary key default (uuid()),
    sku varchar(40) not null unique,
    barcode varchar(80),
    name varchar(160) not null,
    description varchar(600),
    category_name varchar(120),
    brand_name varchar(120),
    unit varchar(40) not null,
    cost_price decimal(14,2) not null default 0.00,
    sale_price decimal(14,2) not null default 0.00,
    tax_rate decimal(5,2) not null default 18.00,
    current_stock decimal(14,2) not null default 0.00,
    minimum_stock decimal(14,2) not null default 0.00,
    active boolean not null default true,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6)
) comment = 'Catalogo principal de productos con stock actual para la primera version';

create table inventory_movements (
    id char(36) primary key default (uuid()),
    product_id char(36) not null,
    warehouse_id char(36),
    movement_type varchar(40) not null,
    quantity decimal(14,2) not null,
    unit_cost decimal(14,2),
    reference varchar(120),
    notes varchar(500),
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint fk_inventory_movements_product foreign key (product_id) references products(id),
    constraint fk_inventory_movements_warehouse foreign key (warehouse_id) references warehouses(id)
) comment = 'Kardex: entradas, salidas, ajustes y transferencias de inventario';

create table customers (
    id char(36) primary key default (uuid()),
    name varchar(160) not null,
    type varchar(30) not null default 'FINAL',
    fiscal_id varchar(30),
    phone varchar(40),
    email varchar(160),
    address varchar(300),
    credit_limit decimal(14,2) not null default 0.00,
    active boolean not null default true,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6)
);

create table employees (
    id char(36) primary key default (uuid()),
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    document_id varchar(30),
    position varchar(80) not null,
    department varchar(80),
    phone varchar(40),
    email varchar(160),
    hire_date date,
    salary decimal(14,2),
    commission_rate decimal(5,2),
    active boolean not null default true,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6)
);

create table suppliers (
    id char(36) primary key default (uuid()),
    name varchar(160) not null,
    rnc varchar(30),
    phone varchar(40),
    email varchar(160),
    address varchar(300),
    active boolean not null default true,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6)
);

create table ncf_sequences (
    id char(36) primary key default (uuid()),
    document_type varchar(40) not null,
    prefix varchar(10) not null,
    start_number bigint not null,
    current_number bigint not null,
    end_number bigint not null,
    valid_until date not null,
    active boolean not null default true,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint ncf_sequence_range check (start_number <= current_number and current_number <= end_number)
) comment = 'Secuencias NCF dominicanas configurables por tipo de comprobante';

create table quotes (
    id char(36) primary key default (uuid()),
    quote_number varchar(40) not null unique,
    customer_name varchar(160) not null,
    customer_fiscal_id varchar(30),
    issue_date date not null,
    valid_until date not null,
    status varchar(30) not null default 'DRAFT',
    subtotal decimal(14,2) not null default 0.00,
    tax_total decimal(14,2) not null default 0.00,
    total decimal(14,2) not null default 0.00,
    notes varchar(800),
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6)
) comment = 'Cotizaciones comerciales A4 que luego podran convertirse en venta';

create table quote_items (
    id char(36) primary key default (uuid()),
    quote_id char(36) not null,
    product_name varchar(160) not null,
    quantity decimal(14,2) not null,
    unit_price decimal(14,2) not null,
    tax_rate decimal(5,2) not null default 18.00,
    subtotal decimal(14,2) not null default 0.00,
    tax_amount decimal(14,2) not null default 0.00,
    line_total decimal(14,2) not null default 0.00,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint fk_quote_items_quote foreign key (quote_id) references quotes(id) on delete cascade
);

create table sales (
    id char(36) primary key default (uuid()),
    invoice_number varchar(40) not null unique,
    ncf varchar(30),
    customer_id char(36),
    customer_name varchar(160),
    customer_fiscal_id varchar(30),
    status varchar(30) not null default 'ISSUED',
    subtotal decimal(14,2) not null default 0.00,
    tax_total decimal(14,2) not null default 0.00,
    discount_total decimal(14,2) not null default 0.00,
    total decimal(14,2) not null default 0.00,
    issued_at datetime(6) not null default current_timestamp(6),
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint fk_sales_customer foreign key (customer_id) references customers(id)
) comment = 'Facturas o ventas emitidas desde el POS';

create table sale_items (
    id char(36) primary key default (uuid()),
    sale_id char(36) not null,
    product_id char(36),
    product_name varchar(160) not null,
    quantity decimal(14,2) not null,
    unit_price decimal(14,2) not null,
    tax_rate decimal(5,2) not null default 18.00,
    subtotal decimal(14,2) not null default 0.00,
    tax_amount decimal(14,2) not null default 0.00,
    line_total decimal(14,2) not null default 0.00,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint fk_sale_items_sale foreign key (sale_id) references sales(id) on delete cascade,
    constraint fk_sale_items_product foreign key (product_id) references products(id)
);

create table payments (
    id char(36) primary key default (uuid()),
    sale_id char(36) not null,
    method varchar(40) not null,
    amount decimal(14,2) not null,
    reference varchar(120),
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint fk_payments_sale foreign key (sale_id) references sales(id) on delete cascade
);

create table accounts_receivable (
    id char(36) primary key default (uuid()),
    customer_id char(36),
    sale_id char(36),
    amount decimal(14,2) not null,
    balance decimal(14,2) not null,
    due_date date,
    status varchar(30) not null default 'PENDING',
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint fk_accounts_receivable_customer foreign key (customer_id) references customers(id),
    constraint fk_accounts_receivable_sale foreign key (sale_id) references sales(id)
);

create table accounts_payable (
    id char(36) primary key default (uuid()),
    supplier_id char(36),
    document_number varchar(80),
    amount decimal(14,2) not null,
    balance decimal(14,2) not null,
    due_date date,
    status varchar(30) not null default 'PENDING',
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint fk_accounts_payable_supplier foreign key (supplier_id) references suppliers(id)
);

create table expenses (
    id char(36) primary key default (uuid()),
    category varchar(120) not null,
    description varchar(300) not null,
    amount decimal(14,2) not null,
    expense_date date not null,
    payment_method varchar(40),
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6)
);

create table audit_logs (
    id char(36) primary key default (uuid()),
    user_name varchar(160),
    action varchar(80) not null,
    entity_name varchar(120) not null,
    entity_id char(36),
    details text,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6)
) comment = 'Historial de acciones importantes realizadas por usuarios';

create index idx_products_name on products(name);
create index idx_products_sku on products(sku);
create index idx_customers_name on customers(name);
create index idx_customers_fiscal_id on customers(fiscal_id);
create index idx_employees_name on employees(first_name, last_name);
create index idx_quotes_number on quotes(quote_number);
create index idx_sales_issued_at on sales(issued_at);
