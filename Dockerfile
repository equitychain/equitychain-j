FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD /target/passportj-core-1.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]