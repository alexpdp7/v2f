drop schema if exists petclinic cascade;
drop schema if exists v2f_samples_petclinic cascade;

create schema petclinic;
create schema v2f_samples_petclinic;

set search_path to petclinic;

create extension if not exists btree_gist;

create table species (
  species_id                 varchar(100) not null primary key,
  name                       text not null
);

create or replace function species_auto_id() returns trigger as $$
  begin
    if new.species_id is null then
      new.species_id = lower(left(new.name, 100));
    end if;
    return new;
  end;
$$ language plpgsql;

drop trigger if exists species_auto_id on species;
create trigger species_auto_id before insert on species for each row execute procedure species_auto_id();

create table owners (
  owner_id                   serial primary key,
  name                       text not null,
  contact_information        text not null,
  email_address              varchar(100)
);

create table pets (
  pet_id                     serial primary key,
  name                       text not null,
  species_id                 varchar(100) not null references species(species_id),
  birth                      date not null,
  owner_id                   integer not null references owners(owner_id)
);

create table vets (
  vet_id                     serial primary key,
  name                       text not null
);

create table visits (
  visit_id                   serial primary key,
  vet_id                     integer not null references vets(vet_id),
  pet_id                     integer not null references pets(pet_id),
  during                     tstzrange not null,
  notes                      text,
  exclude using gist (vet_id with =, during with &&)
);

set search_path to v2f_samples_petclinic, petclinic;

create or replace view species as
  select species_id as _id,
         name as _as_string,
         name
  from   petclinic.species;

create or replace view owners as
  select owner_id as _id,
         name as _as_string,
         name,
         contact_information,
         email_address
  from   petclinic.owners;

set search_path to petclinic;

insert into species(name) values ('Cat'), ('Dog'), ('Iguana'), ('Lizard');
