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
import org.alfresco.event.model.internal.InternalEvent;
import org.alfresco.sync.events.types.RepositoryEvent;
import org.alfresco.fakeeventgenerator.config.EventConfig;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.After;
import org.junit.Before;
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
    private static final String ROUTE_ID = "FAKE-ID";
    private static final ObjectMapper MAPPER = EventObjectMapperFactory.createInstance();

    @Autowired
    protected CamelContext camelContext;

    @EndpointInject(uri = "mock:resultEndpoint")
    protected MockEndpoint mockEndpoint;

    @Autowired
    protected EventSender eventSender;

    @Autowired
    protected CamelMessageProducer camelMessageProducer;

    @Before
    public void setUp() throws Exception
    {
        // Configure route
        configureRoute();
    }

    @After
    public void tearDown() throws Exception
    {
        MockEndpoint.resetMocks(camelContext);
        camelContext.removeRoute(ROUTE_ID);
    }

    @Test
    public void testSendAndReceiveMockInternalEvent() throws Exception
    {
        // Generate random events
        InternalEvent event1 = EventMaker.getRandomInternalEvent();
        InternalEvent event2 = EventMaker.getRandomInternalEvent();

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

    @Test
    public void testSendAndReceiveMockAcsRawEvent() throws Exception
    {
        ObjectMapper originalMapper = camelMessageProducer.getObjectMapper();
        try
        {
            ObjectMapper acsRawEventMapper = EventConfig.createAcsRawEventObjectMapper();
            //Override camelMessageProducer mapper
            camelMessageProducer.setObjectMapper(acsRawEventMapper);

            // Generate random events
            RepositoryEvent event1 = EventMaker.getRandomRawAcsEvent();
            RepositoryEvent event2 = EventMaker.getRandomRawAcsEvent();

            // Set the expected messages
            mockEndpoint.expectedBodiesReceived(
                        Arrays.asList(acsRawEventMapper.writeValueAsString(event1), acsRawEventMapper.writeValueAsString(event2)));
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
        finally
        {
            camelMessageProducer.setObjectMapper(originalMapper);
        }
    }

    protected void configureRoute() throws Exception
    {
        camelContext.addRoutes(new RouteBuilder()
        {
            @Override
            public void configure()
            {
                from(getRoute())
                            .id(ROUTE_ID)
                            .to(mockEndpoint);
            }
        });
    }

    protected abstract String getRoute();
}
