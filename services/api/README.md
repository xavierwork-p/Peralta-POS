# Peralta POS API

Backend Java/Spring Boot para Peralta POS.

## Ejecutar

```bash
mvn -f services/api/pom.xml spring-boot:run
```

## Variables principales

```text
DB_URL
DB_USER
DB_PASSWORD
JWT_SECRET
```

## Rutas iniciales

- `GET /api/health`
- `GET /api/dashboard/summary`
- `GET /api/products`
- `POST /api/products`
- `GET /api/customers`
- `POST /api/customers`
- `GET /api/employees`
- `POST /api/employees`
- `GET /api/quotes`
- `POST /api/quotes`
- `GET /api/sales`
- `POST /api/sales`
- `GET /api/sales/{id}`

## Nota de seguridad

La configuracion actual deja abiertas las rutas de API para facilitar el desarrollo inicial. El modulo de autenticacion y roles se cerrara en una fase posterior con JWT y permisos por rol.
