# Reactive Application with Webflux, DynamoDB and Spring Native

Simple reactive REST application implemented with [webflux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html) and DynamoDbAsyncClient ([AWS SDK 2](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html)) compiled as native image using [Spring Native](https://spring.io/blog/2021/03/11/announcing-spring-native-beta)



## Dynamodb local

Start a service local of DynamoDB through docker-compose (see [aws-dynamodb-local/docker-compose.yml](aws-dynamodb-local/docker-compose.yml))

``` 
$ docker-compose up -d

Creating dynamodb-local ... done
Attaching to dynamodb-local
dynamodb-local    | Initializing DynamoDB Local with the following configuration:
dynamodb-local    | Port:	8000
dynamodb-local    | InMemory:	false
dynamodb-local    | DbPath:	./data
dynamodb-local    | SharedDb:	true
dynamodb-local    | shouldDelayTransientStatuses:	false
dynamodb-local    | CorsParams:	*
dynamodb-local    |
```

Create the tables on local instance (see [scripts DB](db/db-scripts.txt))

``` 
$ aws dynamodb create-table \
--endpoint-url http://localhost:8000 \
--profile localstack \
--cli-input-json file://create-table-events.json
``` 

``` 
$ aws dynamodb put-item \
--endpoint-url http://localhost:8000 \
--profile localstack \
--table-name 'events' \
--item file://add-item-table-events.json
```


## Native image

Build an image native from spring application 

``` 
$ ./mvnw spring-boot:build-image

...
[INFO]     [creator]     [/layers/paketo-buildpacks_native-image/native-image/com.jpolivo.awsdynamodb.AwsDynamodbApplication:164]      [total]: 315,120.64 ms,  4.60 GB
[INFO]     [creator]       Removing bytecode
[INFO]     [creator]       Process types:
[INFO]     [creator]         native-image: /workspace/com.jpolivo.awsdynamodb.AwsDynamodbApplication (direct)
[INFO]     [creator]         task:         /workspace/com.jpolivo.awsdynamodb.AwsDynamodbApplication (direct)
[INFO]     [creator]         web:          /workspace/com.jpolivo.awsdynamodb.AwsDynamodbApplication (direct)
[INFO]     [creator]     ===> EXPORTING
[INFO]     [creator]     Adding 1/1 app layer(s)
[INFO]     [creator]     Reusing layer 'launcher'
[INFO]     [creator]     Adding layer 'config'
[INFO]     [creator]     Adding layer 'process-types'
[INFO]     [creator]     Adding label 'io.buildpacks.lifecycle.metadata'
[INFO]     [creator]     Adding label 'io.buildpacks.build.metadata'
[INFO]     [creator]     Adding label 'io.buildpacks.project.metadata'
[INFO]     [creator]     Adding label 'org.opencontainers.image.title'
[INFO]     [creator]     Adding label 'org.opencontainers.image.version'
[INFO]     [creator]     Adding label 'org.springframework.boot.spring-configuration-metadata.json'
[INFO]     [creator]     Adding label 'org.springframework.boot.version'
[INFO]     [creator]     Setting default process type 'web'
[INFO]     [creator]     Saving docker.io/library/aws-dynamodb:0.0.1-SNAPSHOT...
[INFO]     [creator]     *** Images (e846a8ae7c5c):
[INFO]     [creator]           docker.io/library/aws-dynamodb:0.0.1-SNAPSHOT
[INFO]     [creator]     Adding cache layer 'paketo-buildpacks/graalvm:jdk'
[INFO]     [creator]     Adding cache layer 'paketo-buildpacks/native-image:native-image'
[INFO]
[INFO] Successfully built image 'docker.io/library/aws-dynamodb:0.0.1-SNAPSHOT'
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  07:59 min
[INFO] Finished at: 2021-04-06T13:52:58-03:00
[INFO] ------------------------------------------------------------------------
```

Creates a container from [native image](#native-image)

```
$ docker run --name aws-dynamodb-native -p 8080:8080 --network aws-dynamodb_default -e AWS_REGION=us-east-1 aws-dynamodb:0.0.1-SNAPSHOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.4)

2021-04-06 16:41:45.317  INFO 1 --- [           main] c.j.awsdynamodb.AwsDynamodbApplication   : Starting AwsDynamodbApplication using Java 11.0.10 on c9ab3cd101ab with PID 1 (/workspace/com.jpolivo.awsdynamodb.AwsDynamodbApplication started by cnb in /workspace)
2021-04-06 16:41:45.317  INFO 1 --- [           main] c.j.awsdynamodb.AwsDynamodbApplication   : No active profile set, falling back to default profiles: default
2021-04-06 16:41:45.481  INFO 1 --- [           main] c.j.awsdynamodb.config.DynamoDBConfig    : Configuring DynamoDbAsyncClientBuilder with credentials -> accessKey:key - secretKey: key2
2021-04-06 16:41:45.482  INFO 1 --- [           main] c.j.awsdynamodb.config.DynamoDBConfig    : Overriding dynamoEndpoint on DynamoDbAsyncClientBuilder with http://dynamodb-local:8000/
2021-04-06 16:41:45.482  INFO 1 --- [           main] c.j.awsdynamodb.config.DynamoDBConfig    : System Property aws.region: null
2021-04-06 16:41:45.482  INFO 1 --- [           main] c.j.awsdynamodb.config.DynamoDBConfig    : Environment Var AWS_REGION: us-east-1
2021-04-06 16:41:45.617  WARN 1 --- [           main] i.m.c.i.binder.jvm.JvmGcMetrics          : GC notifications will not be available because MemoryPoolMXBeans are not provided by the JVM
2021-04-06 16:41:45.623  INFO 1 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 13 endpoint(s) beneath base path '/actuator'
2021-04-06 16:41:45.660  INFO 1 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080
2021-04-06 16:41:45.662  INFO 1 --- [           main] c.j.awsdynamodb.AwsDynamodbApplication   : Started AwsDynamodbApplication in 0.419 seconds (JVM running for 0.443)
```

Note: AwsDynamodbApplication compiled as native image started in 0.419 seconds!! :neckbeard:

## Test

Test get item

```
$ curl --location --request GET 'http://localhost:8080/eventfn/1'

{"id":"1","body":"An simple event"}%
```

Test save new item

```
$ curl --location --request POST 'http://localhost:8080/eventfn/' \
--header 'Content-Type: application/json' \
--data-raw '{
    "id":"2",
    "body":"my event 2"
}'

{"completedExceptionally":false,"numberOfDependents":1,"done":false,"cancelled":false}%
```



### Resources

* [Accessing Data on DynamoDB with async Client](https://dzone.com/articles/java-microservice-async-rest-client-to-dynamodb-us)

* [Test Spring Applications Using AWS With Testcontainers and LocalStack](https://rieckpil.de/test-spring-applications-using-aws-with-testcontainers-and-localstack/)

* [Test Containers Local Stack module](https://www.testcontainers.org/modules/localstack/)

* [Unit testing DynamoDB applications using JUnit5](https://www.javacodegeeks.com/2019/01/testing-dynamodb-using-junit5.html)

* [Get DynamoDB Local up and running in 3 minutes with Docker](https://dev.to/risafj/get-dynamodb-local-up-and-running-in-3-minutes-with-docker-5ec6)

* [AWS CLI DynamoDB Query Example](https://medium.com/@corymaklin/tutorial-aws-part-5-the-basics-of-dynamo-db-amazons-non-relational-database-9be0cf500e6e)

* [Deploying DynamoDB Locally](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html)

* [Spring Native](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#overview)

------



