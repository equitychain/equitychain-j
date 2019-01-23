# Welcome to equitychain-j


# About
equitychain-j is a pure-Java implementation of the equchain protocol. For high-level information about equitychain and its goals.

# Running equitychain-j

```
 find springboot application App class, run it.
```

##### Building an executable JAR
```
git clone https://github.com/equitychain/equitychain-j
cd equitychain-j
./mvn clean package -DskipTests
java -jar equitychain-j/target/equitychain-j-core-1.0.jar
```

##### Running from command line:
```
> git clone https://github.com/equitychain/equitychain-j
> cd equitychain-j
> ./mvn spring-boot:run
```


##### Importing project to IntelliJ IDEA: 
```
> git clone https://github.com/equitychain/equitychain-j
> cd equitychain-j
> import project
```
  IDEA: 
* File -> New -> Project from existing sources…
* Select equitychain-j/pom.xml
* Dialog “Import Project from maven”: press “OK”
* After building run equitychain-j `com.passport.App`, one of `org.passport.samples.*` or create your own main. 


# Contact
Chat with us .

# License
equitychain-j is released under the [LGPL-V3 license](LICENSE).

