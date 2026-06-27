create table employee_user_accounts (
    id char(36) primary key default (uuid()),
    employee_id char(36) not null,
    username varchar(80) not null,
    password_hash varchar(255) not null,
    active boolean not null default true,
    must_change_password boolean not null default true,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint uq_employee_user_accounts_employee unique (employee_id),
    constraint uq_employee_user_accounts_username unique (username),
    constraint fk_employee_user_accounts_employee foreign key (employee_id) references employees(id)
);

create table employee_permissions (
    id char(36) primary key default (uuid()),
    employee_id char(36) not null,
    module_key varchar(60) not null,
    access_level varchar(20) not null,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    constraint uq_employee_permissions_module unique (employee_id, module_key),
    constraint fk_employee_permissions_employee foreign key (employee_id) references employees(id)
);

create index idx_employee_permissions_employee on employee_permissions(employee_id);

insert into employee_permissions (employee_id, module_key, access_level)
select id, module_key, 'WRITE'
from employees
cross join (
    select 'POINT_OF_SALE' module_key union all
    select 'SALES_HISTORY' union all
    select 'PRODUCTS' union all
    select 'REPLENISHMENT' union all
    select 'PURCHASES' union all
    select 'MOVEMENTS' union all
    select 'COUNTS' union all
    select 'ACCOUNTING' union all
    select 'CUSTOMERS' union all
    select 'SUPPLIERS' union all
    select 'QUOTES' union all
    select 'EMPLOYEES' union all
    select 'SETTINGS' union all
    select 'REPORTS'
) modules
where first_name = 'Ana' and last_name = 'Peralta';

insert into employee_permissions (employee_id, module_key, access_level)
select id, module_key, access_level
from employees
join (
    select 'POINT_OF_SALE' module_key, 'WRITE' access_level union all
    select 'SALES_HISTORY', 'READ' union all
    select 'PRODUCTS', 'READ' union all
    select 'CUSTOMERS', 'WRITE' union all
    select 'QUOTES', 'WRITE' union all
    select 'REPORTS', 'READ'
) modules
where first_name = 'Luis' and last_name = 'Mateo';

insert into employee_permissions (employee_id, module_key, access_level)
select id, module_key, access_level
from employees
join (
    select 'PRODUCTS' module_key, 'WRITE' access_level union all
    select 'REPLENISHMENT', 'WRITE' union all
    select 'MOVEMENTS', 'WRITE' union all
    select 'COUNTS', 'WRITE' union all
    select 'SUPPLIERS', 'READ' union all
    select 'REPORTS', 'READ'
) modules
where first_name = 'Rosa' and last_name = 'Jimenez';
