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
package org.alfresco.mockeventgenerator.config.kafka;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.alfresco.mockeventgenerator.AbstractCamelTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author Jamal Kaabi-Mofrad
 */
@ActiveProfiles("kafka")
public class KafkaTest extends AbstractCamelTest
{
    private static final String TOPIC_NAME = "generator.event.kafka.test";

    @Autowired
    private KafkaProperties properties;

    @Test
    public void testKafkaProperties()
    {
        assertEquals("localhost", properties.getHost());
        assertEquals(9092, properties.getPort());
        assertNotNull(properties.getCamelRoute());
        assertEquals(TOPIC_NAME, properties.getCamelRoute().getDestinationName());
        assertEquals("direct:topic=" + TOPIC_NAME, properties.getCamelRoute().getToRoute());
    }

    @Override
    protected String getRoute()
    {
        return properties.getCamelRoute().getToRoute();
    }
}
