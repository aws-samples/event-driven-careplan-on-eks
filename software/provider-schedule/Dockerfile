FROM amazoncorretto:17 as builder

RUN yum -y update && yum install -y shadow-utils tar gzip

ENV MVN_VERSION=3.9.4
ENV MVN_FOLDERNAME=apache-maven-${MVN_VERSION}
ENV MVN_FILENAME=apache-maven-${MVN_VERSION}-bin.tar.gz
RUN curl -4 -L https://archive.apache.org/dist/maven/maven-3/${MVN_VERSION}/binaries/${MVN_FILENAME} | tar -xvz
RUN mv $MVN_FOLDERNAME /usr/lib/maven
RUN rm -rf $MVN_FOLDERNAME
RUN ln -s /usr/lib/maven/bin/mvn /usr/bin/mvn

COPY ./pom.xml ./pom.xml
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -f ./pom.xml

COPY src ./src/
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests=true && mv target/provider-schedule-1.2.0.jar provider-schedule.jar

FROM amazoncorretto:17-alpine-jdk

COPY --from=builder provider-schedule.jar provider-schedule.jar

RUN addgroup -S spring && adduser spring -G spring --disabled-password

USER spring:spring

EXPOSE 8090
ENTRYPOINT ["java","-jar","-Dserver.port=8090","/provider-schedule.jar"]
