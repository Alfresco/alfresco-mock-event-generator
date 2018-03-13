/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.fakeeventgenerator.config.kafka;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.alfresco.fakeeventgenerator.AbstractCamelTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author Jamal Kaabi-Mofrad
 */
@ActiveProfiles("kafka")
public class KafkaTest extends AbstractCamelTest
{
    private static final String TOPIC_NAME = "faker.event.kafka.test";

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
