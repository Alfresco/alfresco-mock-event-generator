# Alfresco Mock Event Generator

The Alfresco Mock Event Generator is a Spring Boot application that generates random events and sends them to the Message Broker.

The generated events are selected from one of the following events categories:

* ACS_RAW_EVENT
* ACS_PUBLIC_EVENT
* ACTIVITI_RAW_EVENT
* ACTIVITI_PUBLIC_EVENT

The default events category is **_ACS_PUBLIC_EVENT_**.
To override the default you can use System property:

    -Dgenerator.eventCategory=<one-of-the-above-event-categories> 

The default Message Broker is ActiveMQ, but RabbitMQ and Kafka are also supported and can be activated by the use of Spring Profile.

See [application.yml](src/main/resources/application.yml) for the available properties and their default values.

# Build and Run

**Note**: Before running the application make sure that you have started a supported Message Broker.

For ActiveMQ and RabbitMQ the default host and port, that the application listens to are: localhost:5672

You will need to update the relevant properties if your broker is not using the default host and port.

To start the application with ActiveMQ:

    mvn spring-boot:run

To start the application with ActiveMQ and different port. E.g. 5682:

    mvn spring-boot:run -Dmessaging.to.activemq.port=5682

To start the application with ActiveMQ and ACS_RAW_EVENT category:

    mvn spring-boot:run -Dgenerator.eventCategory=ACS_RAW_EVENT

To start the application with RabbitMQ:

    mvn spring-boot:run -Dspring.profiles.active=rabbitMQ

To start the application with RabbitMQ and different port. E.g. 5682:

    mvn spring-boot:run -Dspring.profiles.active=rabbitMQ -Dmessaging.to.rabbitmq.port=5682

To start the application with RabbitMQ and ACTIVITI_RAW_EVENT category:

    mvn spring-boot:run -Dspring.profiles.active=rabbitMQ -Dgenerator.eventCategory=ACTIVITI_RAW_EVENT

To start the application with Kafka:

    mvn spring-boot:run -Dspring.profiles.active=kafka

Executing any of the above commands will start the application, connect to the given broker and send **10** messages with a pause time of **1** second between each message.
The messages are sent to the defined topic. See _**camelRoute.destinationName**_ in the application.yml file.

# Scheduled Run

The scheduled option allows you to send **_n_** number of messages per **_s_** seconds for the duration of **_t_** seconds.

To start the application with the scheduler:

    mvn spring-boot:run -Dgenerator.scheduled.enabled=true

The above command will start the application, connect to **ActiveMQ** and **_try_** to send **1000** messages per second for the duration of **10** seconds.

Note: Depending on your hardware, you may not be able to achieve such a number per second.
Try disabling ActiveMQ **persistence** and increasing the JVM memory of the broker for the desired number.

For example, using ActiveMQ docker image:

    docker run --name='activemq' -it --rm -e 'ACTIVEMQ_CONFIG_MINMEMORY=1024' -e 'ACTIVEMQ_CONFIG_MAXMEMORY=2048' -p 8161:8161 -p 5672:5672 -P webcenter/activemq:latest

# Build the Docker image

If you want to compile the source code and build the Docker image, you need to run the following at the root of the project:

    mvn clean package docker:build

This will build the Docker image locally.







