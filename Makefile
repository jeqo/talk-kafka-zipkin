.PHONY: all
all: build docker-compose-up

.PHONY: build
build: ## build java applications
	sh mvnw clean install

.PHONY: docker-compose-up
docker-compose-up: ## start docker-compose environment
	docker-compose up -d

.PHONY: hello-service
hello-service: # starts hello service
	cd hello-service/; \
	java -jar target/hello-service.jar server application.yml

.PHONY: hello-translation
hello-translation: # starts hello service
	cd hello-translation/; \
	java -jar target/hello-translation.jar server application.yml

.PHONY: hello-client
hello-client: # starts hello client
	cd hello-client/; \
	java -jar target/hello-client.jar

.PHONY: hello-consumer
hello-consumer: # starts hello consumer
	cd hello-consumer/; \
	java -jar target/hello-consumer.jar

