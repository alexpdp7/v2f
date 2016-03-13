create or replace view species as
  select species_id as _id,
         name as _as_string,
         name
  from   petclinic.species;
