# Alfresco Fake Event Generator

The Alfresco Fake Event Generator is a Spring Boot application that generates random events and sends them to the Message Broker.

The generated events are selected from the available events in the [Alfresco-Event-Model](https://github.com/Alfresco/alfresco-event-model).

The default Message Broker is ActiveMQ, but RabbitMQ and Kafka are also supported and they can be activated by the use of Spring Profile.

See [application.yml](src/main/resources/application.yml) for the available properties and their default values.

# Build and Run

Note: Before running the application make sure that you have started a supported Message Broker.

For ActiveMQ and RabbitMQ the default host and port, that the application is listening to are: localhost:5672

You will need to update the relevant properties if your broker is not using the default host and port.

To start the application with ActiveMQ:

    mvn spring-boot:run

To start the application with RabbitMQ:

    mvn spring-boot:run -Dspring.profiles.active=rabbitMQ
    
To start the application with Kafka:

    mvn spring-boot:run -Dspring.profiles.active=kafka

Executing any of the above commands will start the application, connects to the given broker and sends **10** messages with a pause time of **1** second between each message.
The messages are sent to the defined topic. See _**camelRoute.destinationName**_ in the application.yml file.

# Scheduled Run

The scheduled option allows you to send **_n_** number of messages per **_s_** seconds for the duration of **_t_** seconds.

To start the application with the scheduler:

    mvn spring-boot:run -Dfaker.scheduled.enabled=true

The above command will start the application, connects to **ActiveMQ** and **_tries_** to send **1000** messages per second for the duration of **10** seconds.

Note: Depending on your hardware, you may not be able to achieve such a number per second.
Try disabling ActiveMQ **persistence** and increasing the JVM memory of the broker for the desired number.

For example, using ActiveMQ docker image:

    docker run --name='activemq' -it --rm -e 'ACTIVEMQ_CONFIG_MINMEMORY=1024' -e 'ACTIVEMQ_CONFIG_MAXMEMORY=2048' -p 8161:8161 -p 5672:5672 -P webcenter/activemq:latest
    
I was able to achieve way more than **1000** messages per second with my 3 years old laptop.






