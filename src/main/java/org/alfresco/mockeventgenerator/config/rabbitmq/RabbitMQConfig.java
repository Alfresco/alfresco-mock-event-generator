/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.mockeventgenerator.config.rabbitmq;

import org.alfresco.mockeventgenerator.config.RouteConfig;
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
public class RabbitMQConfig extends RouteConfig
{
    private final RabbitMQProperties properties;

    @Autowired
    public RabbitMQConfig(RabbitMQProperties properties)
    {
        super(properties.getCamelRoute());
        this.properties = properties;
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
