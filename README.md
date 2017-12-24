# HeaterTool

## Requirements

* Java JDK8
* Maven 3
* Docker

## Building and running the application locally

1. To build the application

	```
	mvn clean package
	```

2. To start application

	```
	java -jar target/HeaterTool-1.0-SNAPSHOT.jar server config.yml
	```

## Running the application in Docker

1. Build

	```
	docker-compose build
	```

2. Run

	```
	docker-compose up -d
	```

3. Bring it down

    ```
    docker-compose stop
    docker-compose rm
    ```
