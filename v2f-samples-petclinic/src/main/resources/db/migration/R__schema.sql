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
         contact_information as contact_information__list,
         email_address,
         email_address as email_address__list,
         name || ' ' || email_address || ' ' || contact_information as _plain_text_search
  from   petclinic.owners;

create or replace view vets as
  select vet_id as _id,
         name as _as_string,
         name
  from   petclinic.vets;

create or replace view pets as
  select pet_id as _id,
         name as _as_string,
         name,
         species_id as species,
         birth,
         owner_id as owner,
         name as _plain_text_search
  from   petclinic.pets;

comment on column pets.species is 'dropdown_species';
comment on column pets.owner is 'lookup_owners';

set search_path to petclinic;

insert into species(name) values ('Cat'), ('Dog'), ('Iguana'), ('Lizard');

insert into vets(name) values ('Joe Bar'), ('Lana Jones'), ('Jules Qux');

insert into owners(name, contact_information, email_address)
  select name || ' ' || surname as name,
         street || ' ' || (random()*100)::int as contact_information,
         lower(name || '.' || surname || (random()*100)::int || '@example.com') as email_address
  from   (values('John'), ('Peter'), ('Louis'), ('Mary'), ('Stuart'), ('Helen')) as names(name)
  join   (values('Smith'), ('Jordan'), ('Flynn'), ('Long'), ('Bird'), ('Doe')) as surnames(surname)
  on     true
  join   (values('Fifth Avenue'), ('Parliament Square'), ('Long Street'), ('Main Street'),
                ('Jordan Avenue'), ('High Street')) as streets(street)
  on     true;

insert into pets(owner_id, species_id, name, birth)
  select owner_id, species_id, name, birth
  from   (
    select random() as selectivity,
           owners.owner_id as owner_id,
           species.species_id as species_id,
           pet_name as name,
           '1970-01-01'::date + (random()*(current_date - '1970-01-01'::date)) * interval '1 day' as birth
    from   owners
    join   species on true
    join   (values('Cleo'), ('Fido'), ('Spot'), ('Scully'), ('Dot'), ('Tiger')) as pet_names(pet_name) on true)
    as pets
  where selectivity < 0.03;
