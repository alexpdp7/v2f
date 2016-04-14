[![Build Status](https://travis-ci.org/alexpdp7/v2f.svg?branch=master)](https://travis-ci.org/alexpdp7/v2f)
[![Coverage Status](https://coveralls.io/repos/github/alexpdp7/v2f/badge.svg?branch=master)](https://coveralls.io/github/alexpdp7/v2f?branch=master)

# v2f

The View-View Framework (v2f) is an attempt to make developing web CRUD functionality over an SQL database simple, efficient and powerful. It is not a full web framework nor it intends to fulfill any other need. It should work with complex schemas and provide an efficient and usable web UI for it.

v2f consumes a schema designed for it. Database views in the schema result in master-detail views in the UI, allowing maximum flexibility on what is displayed and how it is edited- which is inferred from the view definition and specific hints in the form of columns, additional views and database comments.

v2f is implemented as a servlet application using Spring, jOOQ, SchemaCrawler and Thymeleaf, although using v2f should entail only writing some SQL and pointing v2f to the desired schema.

See [the roadmap](ROADMAP.md) for future plans.
See [the schema spec](SCHEMA_SPEC.md) to learn how to implement a schema for v2f.
See [the guide for v2f development](HACKING.md) to learn how to contribute and extend v2f.
