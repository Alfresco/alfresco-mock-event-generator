/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.fakeeventgenerator.config.amqp;

import org.alfresco.fakeeventgenerator.CamelMessageProducer;
import org.apache.camel.CamelContext;
import org.apache.camel.component.amqp.AMQPComponent;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.connection.CachingConnectionFactory;

/**
 * @author Jamal Kaabi-Mofrad
 */
@Configuration
@EnableConfigurationProperties(AmqpProperties.class)
@Profile({ "default", "activeMQ" })
public class AmqpConfig
{
    private final AmqpProperties properties;
    private final CamelContext camelContext;

    @Autowired
    public AmqpConfig(AmqpProperties properties, CamelContext camelContext)
    {
        this.properties = properties;
        this.camelContext = camelContext;
    }

    @Bean
    public CamelMessageProducer camelMessageProducer()
    {
        return new CamelMessageProducer(camelContext, properties.getCamelRoute().getToRoute());
    }

    @Bean
    public AMQPComponent amqpConnection()
    {
        JmsConnectionFactory jmsConnectionFactory = new JmsConnectionFactory();
        jmsConnectionFactory.setRemoteURI(properties.getUrl());
        jmsConnectionFactory.setUsername(properties.getUsername());
        jmsConnectionFactory.setPassword(properties.getPassword());

        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setTargetConnectionFactory(jmsConnectionFactory);

        JmsConfiguration jmsConfiguration = new JmsConfiguration();
        jmsConfiguration.setConnectionFactory(cachingConnectionFactory);
        jmsConfiguration.setCacheLevelName("CACHE_CONSUMER");

        return new AMQPComponent(jmsConfiguration);
    }
}
