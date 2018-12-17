.PHONY: all
all: clone-submodule format build

.PHONY: format
format:
	sh mvnw spring-javaformat:apply

.PHONY: build
build: ## build java applications
	sh mvnw clean install

.PHONY: start
start: ## start docker-compose environment
	docker-compose up -d

.PHONY: start-hello
start-hello: start
	docker-compose -f docker-compose.yml -f docker-compose-hello.yml up -d

.PHONY: start-hello-client
start-hello-client:
	docker-compose -f docker-compose.yml -f docker-compose-hello.yml start hello-client

.PHONY: start-twitter
start-twitter: start
	docker-compose -f docker-compose.yml -f docker-compose-twitter.yml up -d

.PHONY: clone-submodule
clone-submodule: # clone submodules
	git submodule update --init

.PHONY: destroy
destroy:
	docker-compose down --remove-orphans

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
    java -cp target/twitter-stream-processor.jar:lib/brave-instrumentation-kafka-streams-5.4.3-SNAPSHOT.jar io.github.jeqo.talk.TwitterStreamProcessor

.PHONY: twitter-jdbc
twitter-jdbc: # deploys kafka jdbc sink connector to postgres
	cd twitter-jdbc-sink-connector/; \
	sh deploy.sh

.PHONY: twitter-console
twitter-console: # starts the kafka streams processor to parse json to avro
	cd twitter-console-consumer/; \
	java -jar target/twitter-console-consumer.jar

download-deps:
	wget -O lib/kafka-interceptor-zipkin.jar https://repo1.maven.org/maven2/no/sysco/middleware/kafka/kafka-interceptor-zipkin/0.3.1/kafka-interceptor-zipkin-0.3.1.jar

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
