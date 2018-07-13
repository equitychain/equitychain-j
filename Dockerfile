FROM daocloud.io/brave8/maven-jdk8

MAINTAINER AronWu <smartbit@inesv.com>
CMD mkdir /production
ADD target/passportj-core-1.0.jar /production/passportj-core-1.0.jar
VOLUME /production
EXPOSE 8080
ENTRYPOINT ["java","-jar","/production/passportj-core-1.0.jar"]