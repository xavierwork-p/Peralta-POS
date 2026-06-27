alter table payments
    add column processor varchar(40) null after amount;
