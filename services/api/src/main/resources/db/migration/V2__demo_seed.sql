-- Datos demo para que el portafolio abra con informacion realista desde la primera corrida.
-- Si en el futuro se usa una base productiva, esta migracion puede reemplazarse por un cargador de datos demo.

insert into companies (id, name, commercial_name, rnc, phone, email, address)
values (
    '10000000-0000-0000-0000-000000000001',
    'Peralta S.A.',
    'Ferreteria Peralta',
    '131000001',
    '809-555-0101',
    'contacto@peraltapos.local',
    'Av. Principal #25, Santo Domingo, Republica Dominicana'
);

insert into branches (id, company_id, name, phone, address)
values (
    '10000000-0000-0000-0000-000000000002',
    '10000000-0000-0000-0000-000000000001',
    'Sucursal Principal',
    '809-555-0101',
    'Av. Principal #25, Santo Domingo'
);

insert into warehouses (id, branch_id, name)
values (
    '10000000-0000-0000-0000-000000000003',
    '10000000-0000-0000-0000-000000000002',
    'Almacen Principal'
);

insert into cash_registers (id, branch_id, name)
values (
    '10000000-0000-0000-0000-000000000004',
    '10000000-0000-0000-0000-000000000002',
    'Caja 01'
);

insert into products (sku, barcode, name, description, category_name, brand_name, unit, cost_price, sale_price, tax_rate, current_stock, minimum_stock)
values
    ('FT-001', '7460000000011', 'Cemento gris 42.5kg', 'Cemento para construccion general', 'Construccion', 'CibaoMix', 'saco', 385.00, 465.00, 18.00, 84.00, 25.00),
    ('FT-014', '7460000000141', 'Varilla 3/8 x 20 pies', 'Varilla corrugada para obra civil', 'Hierros', 'AceroMax', 'unidad', 250.00, 315.00, 18.00, 18.00, 30.00),
    ('EL-204', '7460000002046', 'Cable electrico THHN #12', 'Cable electrico por pie', 'Electricidad', 'ElectroPlus', 'pie', 17.00, 28.00, 18.00, 320.00, 120.00),
    ('PL-077', '7460000000776', 'Tubo PVC 1/2 pulgada', 'Tubo PVC presion para plomeria', 'Plomeria', 'AguaFlex', 'unidad', 62.00, 95.00, 18.00, 11.00, 40.00);

insert into customers (id, name, type, fiscal_id, phone, email, address, credit_limit)
values
    ('20000000-0000-0000-0000-000000000001', 'Constructora Duarte SRL', 'EMPRESARIAL', '131456789', '809-555-0201', 'compras@duarte.local', 'Santo Domingo Este', 250000.00),
    ('20000000-0000-0000-0000-000000000002', 'Maria Rodriguez', 'FINAL', '00112345678', '809-555-0202', 'maria@example.local', 'Santo Domingo', 0.00),
    ('20000000-0000-0000-0000-000000000003', 'Ferreteria Los Pinos', 'EMPRESARIAL', '132987654', '809-555-0203', 'admin@lospinos.local', 'Santiago', 175000.00);

insert into employees (first_name, last_name, document_id, position, department, phone, email, hire_date, salary, commission_rate)
values
    ('Ana', 'Peralta', '00100000001', 'Administradora', 'Administracion', '809-555-0301', 'ana@peraltapos.local', '2024-01-15', 65000.00, 0.00),
    ('Luis', 'Mateo', '00100000002', 'Cajero', 'Ventas', '809-555-0302', 'luis@peraltapos.local', '2024-03-10', 32000.00, 1.50),
    ('Rosa', 'Jimenez', '00100000003', 'Encargada de almacen', 'Almacen', '809-555-0303', 'rosa@peraltapos.local', '2023-09-05', 38000.00, 0.00);

insert into suppliers (name, rnc, phone, email, address)
values
    ('Distribuidora Nacional SRL', '130222222', '809-555-0401', 'ventas@distnacional.local', 'Santo Domingo'),
    ('Importadora del Norte', '130333333', '809-555-0402', 'compras@importnorte.local', 'Santiago');

insert into ncf_sequences (document_type, prefix, start_number, current_number, end_number, valid_until)
values
    ('CONSUMO', 'B02', 1, 1, 5000, '2026-12-31'),
    ('CREDITO_FISCAL', 'B01', 1, 1, 2500, '2026-12-31'),
    ('NOTA_CREDITO', 'B04', 1, 1, 1000, '2026-12-31');

insert into quotes (id, quote_number, customer_name, customer_fiscal_id, issue_date, valid_until, status, subtotal, tax_total, total, notes)
values (
    '30000000-0000-0000-0000-000000000001',
    'COT-20260616-00001',
    'Constructora Duarte SRL',
    '131456789',
    '2026-06-16',
    '2026-06-24',
    'SENT',
    71627.12,
    12892.88,
    84520.00,
    'Precios validos hasta la fecha indicada. No incluye transporte fuera del Gran Santo Domingo.'
);

insert into quote_items (quote_id, product_name, quantity, unit_price, tax_rate, subtotal, tax_amount, line_total)
values
    ('30000000-0000-0000-0000-000000000001', 'Cemento gris 42.5kg', 80.00, 465.00, 18.00, 37200.00, 6696.00, 43896.00),
    ('30000000-0000-0000-0000-000000000001', 'Varilla 3/8 x 20 pies', 92.00, 315.00, 18.00, 28980.00, 5216.40, 34196.40),
    ('30000000-0000-0000-0000-000000000001', 'Tubo PVC 1/2 pulgada', 57.34, 95.00, 18.00, 5447.12, 980.48, 6427.60);
