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

create or replace view visits as
  select visit_id as _id,
         pets.name || ' at ' || during || ' by ' || vets.name as _as_string,
         pets.name as pet__list,
         vets.name as vet__list,
         during as during__list,
         visits.pet_id as pet,
         visits.vet_id as vet,
         visits.during
  from   petclinic.visits
  join   petclinic.pets on visits.pet_id = pets.pet_id
  join   petclinic.vets on visits.vet_id = vets.vet_id;

comment on column visits.pet is 'lookup_pets';
comment on column visits.vet is 'dropdown_vets';

create or replace view _visits_editable as
  select visit_id as _id,
         visits.pet_id as pet,
         visits.vet_id as vet,
         visits.during
  from   petclinic.visits;

create or replace view vets__nested__upcoming_visits as
  select vet_id as _parent_id,
         visit_id as _id,
         pets.name || ' at ' || lower(during) as _as_string,
         pets.name as pet__list,
         during as during__list
  from   petclinic.visits
  join   petclinic.pets on visits.pet_id = pets.pet_id
  where  upper(during) > now();

comment on view vets__nested__upcoming_visits is 'link_visits';

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

insert into visits(vet_id, pet_id, during, notes)
  select vet_id, pet_id, tstzrange(visit_start, visit_start + random() * interval '1 hours') as during, notes
  from (
    select random() as selectivity,
           vet_id,
           pet_id,
           now() - (interval '800 days') + (random() * interval '1600 days') as visit_start,
           random()::varchar as notes
    from   vets,
           pets)
    as visits
  where selectivity < 0.3;
