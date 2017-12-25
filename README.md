# Haverland Smartwave Prometheus Exporter

[![Build Status](https://travis-ci.org/trastle/haverland-smartwave-prometheus-exporter.svg?branch=master)](https://travis-ci.org/trastle/haverland-smartwave-prometheus-exporter)
[![Docker Build](https://img.shields.io/docker/automated/trastle/haverland-smartwave-prometheus-exporter.svg)](https://hub.docker.com/r/trastle/haverland-smartwave-prometheus-exporter/)

Export the status of your Haverland Smartwave Heaters as Prometheus metrics.

## Usage

### Usage requirements

* Docker
* Docker-Compose (optional)

### Running the exporter

1. Run the exporter...

   **with the Docker CLI:**

	```
	docker run -p 127.0.0.1:22200:22200 \
	   --env HAVERLAND_USERNAME=your_username \
       --env HAVERLAND_PASSWORD=your_password \
	   trastle/haverland-smartwave-prometheus-exporter
	    
	```
   
   **with Compose:**
    
   Add your credentials to ```docker-compose.yml```
   
   ```
   docker-compose up -d
   ```

2. Look at your metrics

    ```
    curl localhost:22200/metrics
    ```

## Development

### Devopment Requirements

* JDK8
* Maven 3
* Docker
* Docker-Compose

### Building and running the application locally

1. To build the application

	```
	mvn clean package
	```

2. To start application

	```
	java -jar target/HeaterTool-1.0-SNAPSHOT.jar server config.yml
	```

### Building the Docker Image

1. Build

	```
	docker-compose build
	```

