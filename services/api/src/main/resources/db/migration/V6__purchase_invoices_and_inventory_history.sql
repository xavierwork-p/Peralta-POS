create table purchase_invoices (
    id char(36) primary key default (uuid()),
    supplier_id char(36) not null,
    document_number varchar(80) not null,
    invoice_date date not null,
    due_date date,
    payment_term varchar(30) not null,
    status varchar(30) not null default 'POSTED',
    subtotal decimal(14,2) not null default 0.00,
    tax_total decimal(14,2) not null default 0.00,
    total decimal(14,2) not null default 0.00,
    notes varchar(500),
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint fk_purchase_invoices_supplier foreign key (supplier_id) references suppliers(id),
    constraint uk_purchase_invoice_supplier_document unique (supplier_id, document_number)
);

create table purchase_invoice_items (
    id char(36) primary key default (uuid()),
    purchase_invoice_id char(36) not null,
    product_id char(36) not null,
    product_name varchar(160) not null,
    quantity decimal(14,2) not null,
    unit_cost decimal(14,2) not null,
    tax_rate decimal(5,2) not null,
    subtotal decimal(14,2) not null,
    tax_amount decimal(14,2) not null,
    line_total decimal(14,2) not null,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint fk_purchase_items_invoice foreign key (purchase_invoice_id) references purchase_invoices(id) on delete cascade,
    constraint fk_purchase_items_product foreign key (product_id) references products(id)
);

alter table accounts_payable
    add column purchase_invoice_id char(36),
    add constraint fk_accounts_payable_purchase_invoice
        foreign key (purchase_invoice_id) references purchase_invoices(id);

create index idx_purchase_invoices_date on purchase_invoices(invoice_date);
create index idx_purchase_items_product on purchase_invoice_items(product_id);
create index idx_inventory_movements_created on inventory_movements(created_at);
create index idx_inventory_movements_reference on inventory_movements(reference);

insert into inventory_movements (
    id,
    product_id,
    movement_type,
    quantity,
    unit_cost,
    reference,
    notes,
    created_at,
    updated_at
)
select
    uuid(),
    sale_item.product_id,
    'SALE',
    sale_item.quantity,
    product.cost_price,
    sale.invoice_number,
    'Salida recuperada del historial de ventas',
    sale.issued_at,
    sale.issued_at
from sale_items sale_item
join sales sale on sale.id = sale_item.sale_id
join products product on product.id = sale_item.product_id
where sale_item.product_id is not null
  and not exists (
      select 1
      from inventory_movements movement
      where movement.product_id = sale_item.product_id
        and movement.movement_type = 'SALE'
        and movement.reference = sale.invoice_number
  );
