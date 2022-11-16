# Running and building the project

## Requirements

- Java 17
- Docker 20.10+
- Docker Compose 1.28.5

## Running in CMD

You need to start the local PostgreSQL container with your database in a separate shell:

```
docker-compose up 
```

Then in another shell run the application using gradle wrapper:

```
./gradlew bootRun
```

## Building the local docker image

```
./gradlew bootBuildImage
```

### To run this local image:

You need to start the local PostgreSQL container with your database:

```
docker-compose up 
```

And then run container with our application:

```
./run-local-docker-image.sh
```