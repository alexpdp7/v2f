from maven:latest
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN mvn -Dmaven.test.skip=true install ; cd /usr/src/app/v2f-samples-petclinic ; mvn -Dmaven.test.skip=true package spring-boot:repackage

