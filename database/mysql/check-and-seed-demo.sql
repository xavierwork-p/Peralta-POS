-- Ejecuta este archivo en MySQL Workbench si la base existe pero no ves datos.
-- Primero selecciona la base:

use peralta_pos;

-- Revisa si Flyway ya creo tablas y cargo datos.
show tables;
select count(*) as productos from products;
select count(*) as clientes from customers;
select count(*) as empleados from employees;

-- Si las tablas NO existen:
-- 1. Ejecuta services/api/src/main/resources/db/migration/V1__initial_schema.sql
-- 2. Ejecuta services/api/src/main/resources/db/migration/V2__demo_seed.sql
--
-- Si las tablas existen pero estan vacias:
-- Ejecuta services/api/src/main/resources/db/migration/V2__demo_seed.sql
--
-- Normalmente no deberias hacer esto manualmente; al iniciar el backend,
-- Flyway debe ejecutar V1 y V2 automaticamente.
