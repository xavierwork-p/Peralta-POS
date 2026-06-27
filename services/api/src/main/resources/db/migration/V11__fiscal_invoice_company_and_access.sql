alter table quote_items
    add column product_id char(36) null after quote_id,
    add constraint fk_quote_items_product foreign key (product_id) references products(id);

alter table quotes
    add column converted_sale_id char(36) null after notes,
    add column converted_at datetime(6) null after converted_sale_id,
    add constraint fk_quotes_converted_sale foreign key (converted_sale_id) references sales(id);

alter table sales
    add column fiscal_document_type varchar(40) not null default 'CONSUMO' after ncf,
    add column source_quote_id char(36) null after fiscal_document_type,
    add column ecf_status varchar(40) not null default 'NOT_SUBMITTED' after source_quote_id,
    add column ecf_track_id varchar(80) null after ecf_status,
    add column ecf_security_code varchar(80) null after ecf_track_id,
    add column ecf_signed_xml_path varchar(500) null after ecf_security_code,
    add constraint fk_sales_source_quote foreign key (source_quote_id) references quotes(id);

alter table employee_user_accounts
    add column allow_web_access boolean not null default true after active;

update quote_items qi
join products p on lower(p.name) = lower(qi.product_name)
set qi.product_id = p.id
where qi.product_id is null;

create index idx_quote_items_product on quote_items(product_id);
create index idx_sales_ncf on sales(ncf);
create index idx_sales_source_quote on sales(source_quote_id);
