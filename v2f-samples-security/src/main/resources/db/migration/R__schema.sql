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

create table private_items (
  item_id                    serial primary key,
  name                       varchar(100) not null
);

create table private_item_permissions (
  item_id                    integer not null references private_items(item_id),
  user_id                    varchar(100) not null references users(user_id),
  primary key(item_id, user_id)
);

insert into private_items(name) values ('everyone'), ('only_peter'), ('no one');

insert into private_item_permissions(item_id, user_id) select item_id, user_id from private_items, users where private_items.name = 'everyone';
insert into private_item_permissions(item_id, user_id) select item_id, 'peter' from private_items where private_items.name = 'only_peter';

set search_path to v2f_samples_security, security;

create view users as
  select user_id as _id,
         user_id as _as_string,
         user_id as user_name,
         password as password
  from   security.users;

create view private_items as
  select item_id as _id,
         name as _as_string,
         name as name
  from   security.private_items;

create view private_items__nested__permissions as
  select item_id as _parent_id,
         item_id || '/' || user_id as _id,
         user_id as _as_string
  from   private_item_permissions;

create view private_items__permissions as
  select item_id as _id,
         user_id as _user_id
  from   private_item_permissions;

create view _users as
  select user_id as username,
         password as password,
         enabled as enabled
  from   security.users;

create view _authorities as
  select user_id as username,
         'USER' as authority
  from   security.users;
