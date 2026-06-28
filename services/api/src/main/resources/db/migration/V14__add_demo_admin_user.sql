insert into employees (
    id,
    first_name,
    last_name,
    document_id,
    position,
    department,
    phone,
    email,
    hire_date,
    salary,
    commission_rate,
    active
)
select
    '90000000-0000-0000-0000-000000000001',
    'Admin',
    'Sistema',
    '00000000000',
    'Administrador general',
    'Administracion',
    '809-555-0000',
    'admin@peraltapos.local',
    current_date,
    0.00,
    0.00,
    true
where not exists (
    select 1
    from employees
    where id = '90000000-0000-0000-0000-000000000001'
);

insert into employee_user_accounts (
    employee_id,
    username,
    password_hash,
    active,
    allow_web_access,
    must_change_password
)
values (
    '90000000-0000-0000-0000-000000000001',
    'admin',
    '$2a$10$ke/dRe.CPhC5x4flZimKauGDOzij0IjTOZYAxB0H5eiPIcddHyIye',
    true,
    true,
    false
)
on duplicate key update
    employee_id = '90000000-0000-0000-0000-000000000001',
    password_hash = '$2a$10$ke/dRe.CPhC5x4flZimKauGDOzij0IjTOZYAxB0H5eiPIcddHyIye',
    active = true,
    allow_web_access = true,
    must_change_password = false;

insert into employee_permissions (employee_id, module_key, access_level)
select '90000000-0000-0000-0000-000000000001', module_key, 'WRITE'
from (
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
on duplicate key update
    access_level = 'WRITE';
