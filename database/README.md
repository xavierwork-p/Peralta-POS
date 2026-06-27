# Base de datos MySQL

Las migraciones principales viven dentro del backend para que Flyway las ejecute automaticamente:

```text
services/api/src/main/resources/db/migration
```

Si usas MySQL Workbench, primero puedes ejecutar:

```text
database/mysql/create-database.sql
```

Luego el backend ejecuta automaticamente las migraciones `V1`, `V2`, etc. al iniciar.

Si ya creaste la base pero no ves tablas o datos, abre:

```text
database/mysql/check-and-seed-demo.sql
```

Ese archivo te indica que revisar y que migraciones ejecutar manualmente si Flyway aun no corrio.
