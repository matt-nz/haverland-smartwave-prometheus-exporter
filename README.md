# Haverland Smartwave Prometheus Exporter

[![Build Status](https://travis-ci.org/trastle/haverland-smartwave-prometheus-exporter.svg?branch=master)](https://travis-ci.org/trastle/haverland-smartwave-prometheus-exporter)

Export the status of your Haverland Smartwave heaters as Prometheus metrics.

## Requirements

* JDK8
* Maven 3
* Docker
* Docker-Compose

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

3. Look at your metrics

    ```
    curl localhost:22200/metrics
    ```

4. Bring it down

    ```
    docker-compose stop
    docker-compose rm
    ```
