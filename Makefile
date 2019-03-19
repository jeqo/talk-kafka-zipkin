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
start-hello: start ## start all hello services
	docker-compose -f docker-compose.yml -f docker-compose-hello.yml up -d

.PHONY: hello-server
hello-server: ## start only hello server
	docker-compose -f docker-compose.yml -f docker-compose-hello.yml up -d hello-service

.PHONY: test-hello
test-hello:
	curl http://localhost:18000/hello/service

.PHONY: hello-translation
hello-translation: ## starts hello service
	docker-compose -f docker-compose.yml -f docker-compose-hello.yml up -d hello-translation

.PHONY: hello-client
hello-client: ## start hello client
	docker-compose -f docker-compose.yml -f docker-compose-hello.yml up -d hello-client

.PHONY: hello-consumer
hello-consumer: ## starts hello consumer
	docker-compose -f docker-compose.yml -f docker-compose-hello.yml up -d hello-consumer

.PHONY: start-twitter
start-twitter: start ## start twitter demo services
	docker-compose -f docker-compose.yml -f docker-compose-twitter.yml up -d

.PHONY: clone-submodule
clone-submodule: ## clone submodules
	git submodule update --init

.PHONY: destroy
destroy: ## cleanup environment
	docker-compose down --remove-orphans

.PHONY: twitter-stream
twitter-stream: ## starts twitter source connector
	docker-compose -f docker-compose.yml -f docker-compose-twitter.yml restart twitter-stream-processor

.PHONY: twitter-source
twitter-source: ## starts twitter source connector
	cd twitter-tweets-source-connector/; \
	sh deploy.sh

.PHONY: twitter-jdbc
twitter-jdbc: ## deploys kafka jdbc sink connector to postgres
	cd twitter-jdbc-sink-connector/; \
	sh deploy.sh

.PHONY: twitter-console
twitter-console: ## starts the kafka streams processor to parse json to avro
	cd twitter-console-consumer/; \
	java -jar target/twitter-console-consumer.jar

.PHONY: download-deps
download-deps:
	wget -O lib/kafka-interceptor-zipkin.jar https://repo1.maven.org/maven2/no/sysco/middleware/kafka/kafka-interceptor-zipkin/0.3.1/kafka-interceptor-zipkin-0.3.1.jar

.PHONY: spigo-ui
spigo-ui: ## start spigo ui
	cd spigo/ui/; \
	npm install; \
	npm run dev

.PHONY: vizceral
vizceral: ## start vizceral example
	cd vizceral-example/; \
	npm install; \
	npm run dev
