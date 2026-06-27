alter table customers
    add column fiscal_profile varchar(40) not null default 'STANDARD' after fiscal_id;

update customers
set fiscal_profile = 'TAX_CREDIT'
where type = 'EMPRESARIAL';
