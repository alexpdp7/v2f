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
