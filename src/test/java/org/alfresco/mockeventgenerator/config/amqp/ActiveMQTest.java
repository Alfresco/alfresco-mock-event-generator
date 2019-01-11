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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.alfresco.mockeventgenerator.AbstractCamelTest;
import org.alfresco.mockeventgenerator.config.CamelRouteProperties;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * @author Jamal Kaabi-Mofrad
 */
@ActiveProfiles("activeMQ")
public class ActiveMQTest extends AbstractCamelTest
{
    private static final String TOPIC_NAME = "generator.event.activemq.test";

    @Autowired
    private AmqpProperties properties;

    @Test
    public void testAmqpProperties()
    {
        assertEquals("localhost", properties.getHost());
        assertEquals(5672, properties.getPort());
        assertEquals("amqp://" + properties.getHost() + ":" + properties.getPort(), properties.getUrl());
        assertNull(properties.getUsername());
        assertNull(properties.getPassword());
        assertNotNull(properties.getCamelRoutes());
        assertEquals(1, properties.getCamelRoutes().size());
        assertEquals(TOPIC_NAME, properties.getCamelRoutes().get(0).getDestinationName());
        assertEquals("direct:" + TOPIC_NAME, properties.getCamelRoutes().get(0).getToRoute());
    }

    @Override
    protected List<CamelRouteProperties> getRoutes()
    {
        return properties.getCamelRoutes();
    }
}
