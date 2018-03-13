/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.fakeeventgenerator.config.kafka;

import org.alfresco.fakeeventgenerator.CamelMessageProducer;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Auto-configuration of the Kafka route.
 *
 * @author Jamal Kaabi-Mofrad
 */
@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@Profile(value = "kafka")
public class KafkaConfig
{
    private final KafkaProperties properties;
    private final CamelContext camelContext;

    @Autowired
    public KafkaConfig(KafkaProperties properties, CamelContext camelContext)
    {
        this.properties = properties;
        this.camelContext = camelContext;
    }

    @Bean
    public CamelMessageProducer camelMessageProducer()
    {
        return new CamelMessageProducer(camelContext, properties.getCamelRoute().getToRoute());
    }
}
