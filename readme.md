# Welcome to PassportJ

[![pipeline status](http://hq88888888.i234.me:30000/BlockChain/PassportJ/badges/master/pipeline.svg)](http://hq88888888.i234.me:30000/BlockChain/PassportJ/commits/master)
[![coverage report](http://hq88888888.i234.me:30000/BlockChain/PassportJ/badges/master/coverage.svg)](http://hq88888888.i234.me:30000/BlockChain/PassportJ/commits/master)

# About
PassportJ is a pure-Java implementation of the passport protocol. For high-level information about passport and its goals, visit [bcp.vip](http://bcp.vip).

# Running PassportJ

```
 find springboot application App class, run it.
```

##### Building an executable JAR
```
git clone https://github.com/bcp/PassportJ
cd passportj-core
./mvn clean package -DskipTests
java -jar passportj-core/target/passportj-core-1.0.jar
```

##### Running from command line:
```
> git clone https://github.com/bcp/PassportJ
> cd passportj-core
> ./mvn spring-boot:run
```


##### Importing project to IntelliJ IDEA: 
```
> git clone https://github.com/bcp/PassportJ
> cd passportj-core
> import project
```
  IDEA: 
* File -> New -> Project from existing sources…
* Select passportj-core/pom.xml
* Dialog “Import Project from maven”: press “OK”
* After building run photonchain `com.passport.App`, one of `org.passport.samples.*` or create your own main. 


# Contact
Chat with us .

# License
photonchainj is released under the [LGPL-V3 license](LICENSE).

