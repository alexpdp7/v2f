v2f is a Maven multimodule project with the following modules:

* v2f-core: containing the core framework
* v2f-samples-*: a sample applications which contain integration tests for the core framework
* v2f-coverage: a dummy module to collect coverage information over the core framework from the integration tests in the sample applications.

v2f is hosted in GitHub, and contributions should be sent as a pull request. v2f is checked by Travis which enforces tests passing and other static checks. Coverage is also published with Coveralls.

All features should be covered by an integration test in the sample applications.

Currently the code is a mess as I am developing this as a prototype/proof-of-concept in my limited spare time. Unit tests are mostly absent and the code is terrible and needs more "object orientation" (i.e. using strongly typed classes instead of lists and maps).
