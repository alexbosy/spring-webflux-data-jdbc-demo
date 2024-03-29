# Running and building the project

## Requirements

- Java 17
- Docker 20.10+
- Docker Compose 1.28.5

## Running in terminal

You need to start the local PostgreSQL container with your database in a separate shell:

```shell
docker-compose up 
```

Then in another terminal run the application using gradle wrapper:

```shell
./gradlew bootRun
```

## Building the local docker image

```shell
./gradlew bootBuildImage
```

### To run this local image:

You need to start the local PostgreSQL container with your database:

```shell
docker-compose up 
```

And then run container with our application:

```shell
./run-local-docker-image.sh
```

## Running all tests

You need to start the local PostgreSQL container with your database in a separate shell:

```shell
docker-compose up 
```

Then in another terminal run the tests using gradle wrapper:

```shell
./gradlew t it at
```

- **t** - run unit tests
- **it** - run integration tests
- **at** - run acceptance tests

# Open API links

1. Open API v3 scheme - http://localhost:8080/v3/api-docs
2. Swagger UI - http://localhost:8080/swagger-ui.html

# GraphQL info

Embedded GraphQL client - http://localhost:8080/graphiql

### Example GraphQL queries

**Create new system user:**

```graphql
mutation {
    createUser(systemUserInput: {
        login: "graphql-login",
        name: "some name",
        surname: "some surname",
        email: "some@email.com",
        password: "some password",
        type: ADMIN

    }) {
        id
        login
        name
        surname
        email
        type
    }
}
```

**Get user by id:**

```graphql
query {
    userById(id: 918) {
        id
        login
        name
        surname
        email
        type
    }
}
```

**Get all system users with pagination:**

```graphql
query {
    allUsers(offset: 0, limit: 5) {
        id
        login
        name
        surname
        email
        type
    }
}
```

# Sample API requests via curl or httpie

```shell
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"login":"some login","name":"some name",
  "surname":"some surname","email": "some email",
  "password":"some password","type":"ADMIN"}' \
  http://localhost:8080/user
```

```shell
http POST http://localhost:8080/user \
login="some login" \
name="some name" \
surname="some surname" \
email="skdjsk" \
password="some password" \
type="ADMIN"
```

```shell
http GET http://localhost:8080/user/35
```

```shell
http PUT http://localhost:8080/user/26 \
Authorization:"Bearer ${JWT}"
login="some login" \
name="some name" \
surname="some surname" \
email="new@new.com"  \
type="ADMIN"
```

```shell
http GET http://localhost:8080/users\?offset\=0\&limit\=2
```

```shell
http GET http://localhost:8080/customers\?offset\=0\&limit\=2
```

```shell
http POST http://localhost:8080/customer/registration \
X-Forwarded-For:88.23.45.55 \
login="some login555" \
name="some name" \
surname="some surname" \
email="email222@ddd.lv" \
password="some password" \
dateOfBirth="12-12-1981" \
countryOfResidence="LV" \
identityNumber="identity number" \
passportNumber="passport number"
```

```shell
http GET http://localhost:8080/customer/some-login49
```

```shell
http DELETE http://localhost:8080/customer/666
```

```shell
http GET http://localhost:8080/customer/profile/some-login
```

```shell
http POST http://localhost:8080/auth \
login="adminlogin" \
password="some password"
```

```shell
http GET http://localhost:8080/me \
Authorization:"Bearer ${JWT}"
```

```shell
http GET http://localhost:8080/customer/my/profile \
Authorization:"Bearer ${JWT}"
```

```shell
http PUT http://localhost:8080/customer/my/profile \
Authorization:"Bearer ${JWT}" \
name="some name" \
surname="some surname" \
email="new@new.com" \
dateOfBirth="11-11-1970" \
countryOfResidence="US" \
identityNumber="Identity number" \
passportNumber="Passport number"
```