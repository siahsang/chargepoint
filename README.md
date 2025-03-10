## Required tools

To build and run, make sure that you have Java and Docker installed on your system. The following items are required:

- Java Development Kit (JDK 21)
- Docker 



## How to run it ?

### Option 1: From the source code

By default, this project uses an embedded H2 database and an embedded RabbitMQ instance(Using Test-Container), which is very convenient for evaluation and development purposes.

```
$ ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Option 2: Using docker
If you have installed docker on your system simply run the following command:
```
$ docker compose -f docker-compose.yml up --build
```


## Running the tests ###

You can run all tests with:

``` 
$ ./mvnw clean test
```

## REST API

Please access the Swagger documentation for the REST API by visiting http://localhost:8080/swagger-ui.html.
Following are a short description of each REST API

---
POST   /charge-session  
**Summary:** If the charging session is successfully validated, this method will return a 201 status code and a success message in the response body.

**Status**:
* 201: OK
* 400: Bad Request, invalid format of the charging session. See response message for more information

**Sample Request Body:**
``` 
{
    "station_id": "d546676f-0786-4fb7-a32c-17618a2b9e38",
    "driver_token": "driver_token_test_123456",
    "call_back_url": "http://httpecho.com:8080"
}
``` 

**Sample Response Body:**
```
{
    "status": "accepted",
    "message": "Request is being processed asynchronously. The result will be sent to the provided callback URL."
}
```
---
