/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.fakeeventgenerator.config.rabbitmq;

import org.alfresco.fakeeventgenerator.CamelMessageProducer;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.rabbitmq.client.ConnectionFactory;

/**
 * @author Jamal Kaabi-Mofrad
 */
@Configuration
@EnableConfigurationProperties(RabbitMQProperties.class)
@Profile(value = "rabbitMQ")
public class RabbitMQConfig
{
    private final RabbitMQProperties properties;
    private final CamelContext camelContext;

    @Autowired
    public RabbitMQConfig(RabbitMQProperties properties, CamelContext camelContext)
    {
        this.properties = properties;
        this.camelContext = camelContext;
    }

    @Bean
    public CamelMessageProducer camelMessageProducer()
    {
        ProducerTemplate producer = camelContext.createProducerTemplate();
        return new CamelMessageProducer(producer, properties.getCamelRoute().getToRoute());
    }

    @Bean
    public ConnectionFactory rabbitmqConnectionFactory()
    {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(properties.getHost());
        factory.setPort(properties.getPort());
        factory.setUsername(properties.getUsername());
        factory.setPassword(properties.getPassword());
        factory.setVirtualHost(properties.getVirtualHost());
        return factory;
    }
}
