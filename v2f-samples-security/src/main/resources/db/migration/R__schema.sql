drop schema if exists security cascade;
drop schema if exists v2f_samples_security cascade;

create schema security;
create schema v2f_samples_security;

set search_path to security;

create table users (
  user_id                    varchar(100) primary key,
  password                   varchar(100) not null default '!',
  enabled                    boolean not null default true
);

insert into users(user_id) values
  ('alex'),
  ('admin'),
  ('peter'),
  ('paul'),
  ('mary');

set search_path to v2f_samples_security, security;

create view users as
  select user_id as _id,
         user_id as _as_string,
         user_id as user_name,
         password as password
  from   security.users;

create view _users as
  select user_id as username,
         password as password,
         enabled as enabled
  from   security.users;

create view _authorities as
  select user_id as username,
         'USER' as authority
  from   security.users;
