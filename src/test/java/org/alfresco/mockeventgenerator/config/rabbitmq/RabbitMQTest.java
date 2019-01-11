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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.alfresco.mockeventgenerator.AbstractCamelTest;
import org.alfresco.mockeventgenerator.config.CamelRouteProperties;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * @author Jamal Kaabi-Mofrad
 */
@ActiveProfiles("rabbitMQ")
public class RabbitMQTest extends AbstractCamelTest
{
    private static final String TOPIC_NAME1 = "generator.event.rabbitmq.test1";
    private static final String TOPIC_NAME2 = "generator.event.rabbitmq.test2";

    @Autowired
    private RabbitMQProperties properties;

    @Test
    public void testRabbitmqProperties()
    {
        assertEquals("localhost", properties.getHost());
        assertEquals(5672, properties.getPort());
        assertEquals("/", properties.getVirtualHost());
        assertEquals("guest", properties.getUsername());
        assertEquals("guest", properties.getPassword());
        assertNotNull(properties.getCamelRoutes());
        assertEquals(2, properties.getCamelRoutes().size());
        assertEquals(TOPIC_NAME1, properties.getCamelRoutes().get(0).getDestinationName());
        assertEquals(TOPIC_NAME2, properties.getCamelRoutes().get(1).getDestinationName());
        assertEquals("direct:" + TOPIC_NAME1, properties.getCamelRoutes().get(0).getToRoute());
        assertEquals("direct:" + TOPIC_NAME2, properties.getCamelRoutes().get(1).getToRoute());
    }

    @Override
    protected List<CamelRouteProperties> getRoutes()
    {
        return properties.getCamelRoutes();
    }
}
