insert into employee_user_accounts (employee_id, username, password_hash, active, must_change_password)
select e.id, 'ana.peralta', '$2a$10$tKyCxS/.KIvKwKXaSV/VVunshNYTGzYXW3zfamPHTRlCt6di7iDRe', true, true
from employees e
left join employee_user_accounts existing on existing.username = 'ana.peralta'
where e.first_name = 'Ana'
  and e.last_name = 'Peralta'
  and existing.id is null;

insert into employee_user_accounts (employee_id, username, password_hash, active, must_change_password)
select e.id, 'luis.mateo', '$2a$10$tKyCxS/.KIvKwKXaSV/VVunshNYTGzYXW3zfamPHTRlCt6di7iDRe', true, true
from employees e
left join employee_user_accounts existing on existing.username = 'luis.mateo'
where e.first_name = 'Luis'
  and e.last_name = 'Mateo'
  and existing.id is null;

insert into employee_user_accounts (employee_id, username, password_hash, active, must_change_password)
select e.id, 'rosa.jimenez', '$2a$10$tKyCxS/.KIvKwKXaSV/VVunshNYTGzYXW3zfamPHTRlCt6di7iDRe', true, true
from employees e
left join employee_user_accounts existing on existing.username = 'rosa.jimenez'
where e.first_name = 'Rosa'
  and e.last_name = 'Jimenez'
  and existing.id is null;
