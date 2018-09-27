.PHONY: all
all: clone-submodule build docker-compose-up

.PHONY: build
build: ## build java applications
	sh mvnw clean install

.PHONY: docker-compose-up
docker-compose-up: ## start docker-compose environment
	docker-compose up -d

.PHONY: clone-submodule
clone-submodule: # clone submodules
	git submodule update --init

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

.PHONY: twitter-source
twitter-source: # starts twitter source connector
	cd twitter-tweets-source-connector/; \
	sh deploy.sh

.PHONY: twitter-stream
twitter-stream: # starts the kafka streams processor to parse json to avro
	cd twitter-stream-processor/; \
    java -cp target/twitter-stream-processor.jar:lib/brave-instrumentation-kafka-streams-5.3.4-SNAPSHOT.jar io.github.jeqo.talk.TwitterStreamProcessor

.PHONY: twitter-jdbc
twitter-jdbc: # deploys kafka jdbc sink connector to postgres
	cd twitter-jdbc-sink-connector/; \
	sh deploy.sh

.PHONY: twitter-console
twitter-console: # starts the kafka streams processor to parse json to avro
	cd twitter-console-consumer/; \
	java -jar target/twitter-console-consumer.jar

.PHONY: down
down: # stop docker-compose
	docker-compose down

.PHONY: spigo-ui
spigo-ui: # start spigo ui
	cd spigo/ui/; \
	npm install; \
	npm run dev

.PHONY: vizceral
vizceral: # start vizceral example
	cd vizceral-example/; \
	npm install; \
	npm run dev
