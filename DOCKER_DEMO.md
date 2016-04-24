# Howto setup a Petclinic Docker instance

First compile the application and generate a self-contained jar:

    $ mvn install -pl v2f-core
    $ mvn -pl v2f-samples-petclinic package spring-boot:repackage

Then set up a Postgres instance and a connected Java instance running the application:

    $ docker run --name petclinic-postgres -e POSTGRES_USER=v2f_samples_petclinic -e POSTGRES_PASSWORD=v2f_samples_petclinic -d postgres
    $ docker build -t petclinic-v2f .
    $ docker run -d -p 8080:8080 --link petclinic-postgres petclinic-v2f java -Djava.security.egd=file:/dev/urandom -jar v2f-samples-petclinic-master.jar --spring.datasource.url=jdbc:postgresql://petclinic-postgres/
