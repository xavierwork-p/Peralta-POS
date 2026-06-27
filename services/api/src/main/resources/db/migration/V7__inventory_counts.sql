create table inventory_counts (
    id char(36) primary key default (uuid()),
    count_number varchar(40) not null unique,
    counted_at datetime(6) not null,
    status varchar(30) not null default 'POSTED',
    notes varchar(500),
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6)
);

create table inventory_count_items (
    id char(36) primary key default (uuid()),
    inventory_count_id char(36) not null,
    product_id char(36) not null,
    product_sku varchar(40) not null,
    product_name varchar(160) not null,
    expected_stock decimal(14,2) not null,
    counted_stock decimal(14,2) not null,
    difference decimal(14,2) not null,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint fk_inventory_count_items_count
        foreign key (inventory_count_id) references inventory_counts(id) on delete cascade,
    constraint fk_inventory_count_items_product
        foreign key (product_id) references products(id)
);

create index idx_inventory_counts_counted_at on inventory_counts(counted_at);
create index idx_inventory_count_items_product on inventory_count_items(product_id);
