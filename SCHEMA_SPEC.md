# Schema spec

The following describes how to create a schema suitable for v2f.

v2f connects to a schema specially designed for it. The schema should *only* contain objects designed to be consumed by v2f.

## Master-detail views

Views not prefixed by `_` get a master-detail pair of views at `/tablename/` and `/tablename/detail/id` or `/tablename/new` for adding new rows.

Column names of master-detail views *must not* start with `_` nor contain `__` except in the special cases noted below. The `_*` namespace is reserved for view-wide extra information (such as the `_id` and `_as_string` columns described below) and `column_name__extra` are reserved to add column-specific extra information.

### `_{viewname}_editable` view

Automatically updatable views make using v2f simpler; under many circumnstances the database will make a master-detail view updatable so you do not have to create triggers to make it updatable. Outside those circumstances (if you use a join, for instance), the database will not be able to make your view updatable. In that situation, you can create a `_{viewname}_editable` parallel view which only contains the updatable columns and does not have joins- v2f will then use that view for inserts/updates.

### `{viewname}__nested__{otherviewname}` nested view

If you create a `{viewname}__nested__{otherviewname}` view, it will be displayed within `{viewname}`'s detail view as a list like regular top-level lists. Use a `link_{viewname}` comment to set which top-level view should elements in this table link to.

### `_id` column

Each master-detail view *must* have an `_id` column which is used to identify each particular entity in the view. The actual primary key in the referenced table or tables is not important- if you are referencing a table with a composite primary key, the master-detail view should adopt a strategy such as constructing the `_id` column by concatenating the primary key values.

Each row in the master table will link to a detail view using the `_id` field.

### `_as_string` column

Each master-detail view *must* have an `_as_string` column, which is used to create the default user-friendly representation of the row.

### `_plain_text_search` column

If a master-detail view has a `_plain_text_search` column, a search box will be rendered on the list that will perform a case-insensitive search of all terms entered into this field.

### Other columns

All other columns will be displayed and editable in the detail view.

#### List display columns

Columns named `column__list` will be shown in the list view.

#### Comments

Comments on columns configure how the column is displayed and edited:

* `dropdown_{viewname}` will create a field to edit a foreign key column as a dropdown of choices on target `viewname`.
* `lookup_{viewname}` will create a field to edit a foreign key column as a lookup on target `viewname`; target must have a `_plain_text_search` column.
