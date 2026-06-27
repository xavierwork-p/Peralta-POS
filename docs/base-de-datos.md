# Estrategia de base de datos

## Decision actual

Peralta POS usara **MySQL** como base principal.

La razon es practica: ya tienes MySQL instalado, el profesor puede revisar una base SQL real, y el proyecto queda mas facil de defender con herramientas conocidas como MySQL Workbench.

## Arquitectura elegida

```text
Spring Boot -> MySQL
```

La URL decide donde vive la base:

```text
MySQL local:
jdbc:mysql://localhost:3306/peralta_pos

MySQL remoto:
jdbc:mysql://host-remoto:3306/peralta_pos
```

## Ventaja

El backend no queda amarrado a un proveedor especifico. Puede correr contra:

- MySQL instalado localmente.
- MySQL en Docker.
- MySQL remoto para una demo web.
- Un servidor propio de la empresa.

## Crear base local

En MySQL Workbench puedes ejecutar:

```text
database/mysql/create-database.sql
```

Ese archivo crea:

- Base `peralta_pos`.
- Usuario `peralta`.
- Password `peralta123`.
- Permisos sobre la base del sistema.

## Datos demo

La migracion `V2__demo_seed.sql` carga datos iniciales para que el dashboard no abra vacio:

- Empresa.
- Sucursal.
- Almacen.
- Caja.
- Productos.
- Clientes.
- Empleados.
- Proveedores.
- Secuencias NCF.
- Cotizacion demo.
