MAVEN := "./mvnw"

.PHONY: all
all: clone-submodule format build

.PHONY: format
format:
	${MAVEN} prettier:write

.PHONY: build
build: ## build java applications
	${MAVEN} clean install

.PHONY: publish
publish: ## build docker images
	${MAVEN} jib:build

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
	curl http://localhost:8080/hello/service

.PHONY: hello-translation
hello-translation: ## starts hello service
	docker-compose -f docker-compose.yml -f docker-compose-hello.yml up -d hello-translation-service

.PHONY: hello-client
hello-client: ## start hello client
	docker-compose -f docker-compose.yml -f docker-compose-hello.yml up -d hello-batch-client

.PHONY: hello-consumer
hello-consumer: ## starts hello consumer
	docker-compose -f docker-compose.yml -f docker-compose-hello.yml up -d hello-consumer

.PHONY: start-twitter
start-twitter: start ## start twitter demo services
	docker-compose -f docker-compose.yml -f docker-compose-twitter.yml up -d

.PHONY: start-haystack
start-all-haystack: start ## start haystack demo services
	docker-compose -f docker-compose.yml -f docker-compose-twitter.yml -f docker-compose-hello.yml -f docker-compose-haystack.yml up -d

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
	curl -XPUT -H 'Content-Type:application/json' -d @twitter-source.json http://localhost:8083/connectors/connector-twitter-source/config

.PHONY: twitter-jdbc
twitter-jdbc: ## deploys kafka jdbc sink connector to postgres
	cd twitter-jdbc-sink-connector/; \
	curl -XPUT -H 'Content-Type:application/json' -d @jdbc-sink.json http://localhost:8084/connectors/connector-jdbc-sink/config

.PHONY: twitter-console
twitter-console: ## starts kafka console consumer
	docker-compose -f docker-compose.yml -f docker-compose-twitter.yml restart twitter-console-consumer

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

.PHONY: kafka-topics
kafka-topics:
	docker-compose exec kafka kafka-topics \
		--zookeeper zookeeper:2181 --create --topic zipkin-spans --partitions 1 --replication-factor 1 --if-not-exists
	docker-compose exec kafka kafka-topics \
		--zookeeper zookeeper:2181 --create --topic zipkin-trace --partitions 1 --replication-factor 1 --if-not-exists
	docker-compose exec kafka kafka-topics \
		--zookeeper zookeeper:2181 --create --topic zipkin-dependency --partitions 1 --replication-factor 1 --if-not-exists
	docker-compose exec kafka kafka-topics \
		--zookeeper zookeeper:2181 --create --topic twitter-json --partitions 1 --replication-factor 1 --if-not-exists
	docker-compose exec kafka kafka-topics \
		--zookeeper zookeeper:2181 --create --topic twitter-avro --partitions 1 --replication-factor 1 --if-not-exists
