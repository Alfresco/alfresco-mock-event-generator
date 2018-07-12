/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.fakeeventgenerator.config.kafka;

import org.alfresco.fakeeventgenerator.config.RouteConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
public class KafkaConfig extends RouteConfig
{
    @Autowired
    public KafkaConfig(KafkaProperties properties)
    {
        super(properties.getCamelRoute());
    }
}
