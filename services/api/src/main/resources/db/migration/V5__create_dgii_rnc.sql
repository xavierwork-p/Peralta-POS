create table dgii_rnc (
    rnc varchar(20) primary key,
    razon_social varchar(220) not null,
    nombre_comercial varchar(220),
    categoria varchar(120),
    regimen_pago varchar(120),
    estado varchar(80),
    actividad_economica varchar(300),
    fecha_constitucion varchar(40),
    administracion_local varchar(160),
    fuente varchar(40) not null default 'DGII',
    actualizado_en datetime(6) not null default current_timestamp(6),
    creado_en datetime(6) not null default current_timestamp(6)
);

create index idx_dgii_rnc_estado on dgii_rnc(estado);
