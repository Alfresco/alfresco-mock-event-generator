/*
 * Copyright 2018 Alfresco Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
