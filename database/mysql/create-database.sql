-- Ejecutar con un usuario administrador de MySQL, por ejemplo root.
-- Crea la base y un usuario de desarrollo para Peralta POS.

create database if not exists peralta_pos
  character set utf8mb4
  collate utf8mb4_unicode_ci;

create user if not exists 'peralta'@'localhost'
  identified by 'peralta123';

grant all privileges on peralta_pos.* to 'peralta'@'localhost';

flush privileges;
