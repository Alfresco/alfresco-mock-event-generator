/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.mockeventgenerator.config.rabbitmq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.alfresco.mockeventgenerator.AbstractCamelTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author Jamal Kaabi-Mofrad
 */
@ActiveProfiles("rabbitMQ")
public class RabbitMQTest extends AbstractCamelTest
{
    private static final String TOPIC_NAME = "generator.event.rabbitmq.test";

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
        assertNotNull(properties.getCamelRoute());
        assertEquals(TOPIC_NAME, properties.getCamelRoute().getDestinationName());
        assertEquals("direct:" + TOPIC_NAME, properties.getCamelRoute().getToRoute());
    }

    @Override
    protected String getRoute()
    {
        return properties.getCamelRoute().getToRoute();
    }
}
