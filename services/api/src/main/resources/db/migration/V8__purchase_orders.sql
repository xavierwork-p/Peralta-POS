create table purchase_orders (
    id char(36) primary key default (uuid()),
    supplier_id char(36) not null,
    order_number varchar(40) not null unique,
    order_date date not null,
    expected_date date,
    status varchar(30) not null default 'OPEN',
    subtotal decimal(14,2) not null default 0.00,
    tax_total decimal(14,2) not null default 0.00,
    total decimal(14,2) not null default 0.00,
    notes varchar(500),
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint fk_purchase_orders_supplier foreign key (supplier_id) references suppliers(id)
);

create table purchase_order_items (
    id char(36) primary key default (uuid()),
    purchase_order_id char(36) not null,
    product_id char(36) not null,
    product_sku varchar(40) not null,
    product_name varchar(160) not null,
    quantity decimal(14,2) not null,
    unit_cost decimal(14,2) not null,
    tax_rate decimal(5,2) not null,
    subtotal decimal(14,2) not null,
    tax_amount decimal(14,2) not null,
    line_total decimal(14,2) not null,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint fk_purchase_order_items_order
        foreign key (purchase_order_id) references purchase_orders(id) on delete cascade,
    constraint fk_purchase_order_items_product
        foreign key (product_id) references products(id)
);

alter table purchase_invoices
    add column purchase_order_id char(36),
    add constraint fk_purchase_invoices_order
        foreign key (purchase_order_id) references purchase_orders(id),
    add constraint uk_purchase_invoice_order unique (purchase_order_id);

create index idx_purchase_orders_date on purchase_orders(order_date);
create index idx_purchase_orders_status on purchase_orders(status);
create index idx_purchase_order_items_product on purchase_order_items(product_id);
