# Server Module

* interceptor enhancement : register an interceptor with annotation.

```
@Component
@GServerInterceptor(AuthenticationServerInterceptor.class)
@GServerInterceptor(LogServerInterceptor.class)
public class PersonServiceImpl extends PersonServiceImplBase {}
```

* Spring/JPA integration

# Client Module

can be anything including primefaces, spring boot etc

* json serialization

# proto

contains all proto file generates java code


# How to build

1. build proto project (cd grpc-proto)

```
mvn clean install
```

2. build grpc-server
3. build grpc-client
