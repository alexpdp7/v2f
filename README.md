# v2f

The View-View Framework (v2f) is a web framework designed to develop CRUD applications over an SQL database with a web interface. The central idea of the framework is to use database views to define the interface of the application, allowing us to express how data is visualized and edited using SQL, perhaps the most well-suited tool to do so. Additionally, this allows the framework to be very simple.

v2f connects to a schema specially designed for it. The schema should *only* contain objects designed to be consumed by v2f.

## Master-detail views

Views not prefixed by `_` get a master-detail pair of views at `/tablename/` and `/tablename/id`.

Column names of master-detail views *must not* start with `_` except in the special cases noted below. The `_*` namespace is reserved for further expansion of the framework.

### `_id` column

Each master-detail view *must* have an `_id` column which is used to identify each particular entity in the view. The actual primary key in the referenced table or tables is not important- if you are referencing a table with a composite primary key, the master-detail view should adopt a strategy such as constructing the `_id` column by concatenating the primary key values.

Each row in the master table will link to a detail view using the `_id` field.

### `_as_string` column

Each master-detail view *must* have an `_as_string` column, which is used to create the default user-friendly representation of the row.

### TODO: other columns

Other column names *must not* contain `__` in their names except in the special cases noted below. The `column_name__*` namespace is reserved for further expansion of the framework.

Except as described below, columns will be shown plainly in the master-detail views.
