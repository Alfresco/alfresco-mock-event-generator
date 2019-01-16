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
package org.alfresco.mockeventgenerator.config.amqp;

import org.alfresco.mockeventgenerator.config.RouteConfig;
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
public class AmqpConfig extends RouteConfig
{
    private final AmqpProperties properties;

    @Autowired
    public AmqpConfig(AmqpProperties properties)
    {
        super(properties.getCamelRoutes());
        this.properties = properties;
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
