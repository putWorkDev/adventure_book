# Read Me First
The following was discovered as part of building this project:

* The original package name 'com.game.adventure-book' is invalid and this project uses 'com.game.adventure_book' instead.

## Bundled books

Flyway's `V2__seed_books.sql` is the source of truth for the four bundled books.
It installs their sections, choices, consequences, and validation reports on a
fresh database. The files in `../books/` are reference examples, not runtime
inputs; editing them does not change an existing database. Create a new Flyway
migration to change the bundled catalog. The `source_json` column introduced
by V1 is not populated for these books: their stored representation is the
normalized tables. The empty Dragon Quest file is stored as a book with an
`UNPARSEABLE` validation error and no sections.
V1 enforces complete optional consequences. V2 normalizes numeric-string
section ids before checking goto targets; The Prisoner's only error is its
optionless section. These migrations are intended for a fresh database:
editing them changes Flyway checksums on any database that has already
applied the previous versions.

Book and section changes in the domain recalculate the four story-rule
diagnostics (beginning, ending, valid targets, and non-ending choices);
import/parsing diagnostics can be supplied separately. V1 defers uniqueness
checks on ordered sections, choices, and validation errors until transaction
commit so Hibernate can replace a saved book's rows without transient
position collisions. Only books without validation errors can be played.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/4.1.1/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.1.1/gradle-plugin/packaging-oci-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/4.1.1/reference/web/servlet.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/4.1.1/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/4.1.1/reference/using/devtools.html)
* [Flyway Migration](https://docs.spring.io/spring-boot/4.1.1/how-to/data-initialization.html#howto.data-initialization.migration-tool.flyway)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)
