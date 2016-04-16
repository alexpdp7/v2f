# Roadmap

## List editing

The framework is pretty ready to handle editing in lists; detail form widgets and the save handler are completely self-contained, using a form element naming pattern which is completely compatible with editing many different rows from different tables in the same form.

New meta-columns such as `_list_editable` could be added to customize this, on a per-row, per-column and per-user basis.

## Inline related list editing

Once list editing is implemented, one can create secondary detail views `{main_table}__{secondary_table}` which contains a `_parent_id` column to join it to the main table. This could be displayed inline in the master view's detail view to allow easy editing of related objects.

## Authorization

Authorization can be added simply by introducing a new `_user_id` meta-column; logged in users get all their views filtered by `_user_id`, giving them a personalized view of the database.

## Authorization modules

Simple ready-to-use authorization modules could be provided- a self-contained authorization schema, an LDAP-integration module (perhaps using Postgres' FDW and other similar dblink features) or any other integration with authorization solutions.

## Per-row, per-column editing restrictions

With `_user_id` columns and the addition of `_editable` metacolumns, certain columns could be editing-restricted on a granular basis.

## Workflow support

For certain columns (such as "state" columns), a secondary metaview could be provided to determine which transitions are allowed, even on a per-user basis.

## Extension hooks

Mechanisms to plug into v2f should be provided. Parameterized actions on rows could be implemented in many fashions, from stored procedures/triggers, which should be pretty simple to implement to Java code which could be plugged into specific views updates and actions. Actions could be applied by selecting specific rows in a list and choosing the action and parameters or by an UI in detail views.

## Advanced search

In addition to `_plain_text_search`, other similar columns could be added to tie into RDBMSs' specific text search features. Also, list views could have column-based filtering (e.g. date columns could get calendar-widgets to filter ranges). These could be inferred automatically from columns' datatypes.

## REST/RPC frontend

If the main handlers are enhanced to be able to receive and produce JSON, for instance, an automatic programmatic frontend could be built automatically from the same v2f schema with all the features of the traditional frontend.

## Progressive enhancement UI

Once the REST/RPC frontend is implemented, it would be very simple to enhance the pure-server-side UI rendering with Javascript to simplify and streamline the UI.

## Themes

Aspect of the web interface should be easily customizable by some mechanism. It should be easy to package themes which could be pulled as a dependency to a v2f project. A responsive usable default theme should be provided. It should be possible to choose the theme for each user dynamically (e.g. with a `_user` metaview with a `theme` column.

## Localization

All elements of the interface should be localizable; users should provide resource bundles with the strings for tables/columns/etc. This mechanism can also be used to provide help hints for the application (e.g. a column's description).

## Schema design UI

While v2f's focus is software developers, a UI for designing a v2f schema (and the underlying data schema) could be provided to make v2f usable for non-developers.
