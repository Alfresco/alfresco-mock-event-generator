/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.fakeeventgenerator;

import java.util.Arrays;

import org.alfresco.event.databind.EventObjectMapperFactory;
import org.alfresco.event.model.internal.BaseInternalEvent;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Jamal Kaabi-Mofrad
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class AbstractCamelTest
{
    private static final ObjectMapper MAPPER = EventObjectMapperFactory.createInstance();

    @Autowired
    protected CamelContext camelContext;

    @EndpointInject(uri = "mock:resultEndpoint")
    protected MockEndpoint mockEndpoint;

    @Autowired
    protected EventSender eventSender;

    @Test
    public void testSendAndReceiveMockEvent() throws Exception
    {
        // Configure route
        configureRoute();

        // Generate random events
        BaseInternalEvent event1 = EventMaker.getRandomEvent();
        BaseInternalEvent event2 = EventMaker.getRandomEvent();

        // Set the expected messages
        mockEndpoint.expectedBodiesReceived(Arrays.asList(MAPPER.writeValueAsString(event1), MAPPER.writeValueAsString(event2)));
        // Set the expected number of messages
        mockEndpoint.expectedMessageCount(2);

        // Send the 1st event
        eventSender.sendEvent(event1);
        // Send the 2nd event
        eventSender.sendEvent(event2);

        // Checks that the received message count is equal to the number of messages sent
        // Also, checks the received message body is equal to the sent message
        mockEndpoint.assertIsSatisfied();
    }

    protected void configureRoute() throws Exception
    {
        camelContext.addRoutes(new RouteBuilder()
        {
            @Override
            public void configure()
            {
                from(getRoute()).to(mockEndpoint);
            }
        });
    }

    protected abstract String getRoute();
}
