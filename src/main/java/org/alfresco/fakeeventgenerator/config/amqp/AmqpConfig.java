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
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.amqp.AMQPConnectionDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
        ProducerTemplate producer = camelContext.createProducerTemplate();
        return new CamelMessageProducer(producer, properties.getCamelRoute().getToRoute());
    }

    @Bean
    public AMQPConnectionDetails amqpConnection()
    {
        return new AMQPConnectionDetails(properties.getUrl(), properties.getUsername(), properties.getPassword());
    }
}
