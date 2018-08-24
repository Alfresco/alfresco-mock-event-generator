/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.mockeventgenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.event.databind.EventObjectMapperFactory;
import org.alfresco.event.model.EventV1;
import org.alfresco.event.model.ResourceV1;
import org.alfresco.mockeventgenerator.EventMaker.PublicActivitiEventInstance;
import org.alfresco.mockeventgenerator.config.EventConfig;
import org.alfresco.sync.events.types.RepositoryEvent;
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
    private static final String ROUTE_ID = "MOCK-ID";
    private static final ObjectMapper PUBLIC_OBJECT_MAPPER = EventObjectMapperFactory.createInstance();
    private static final ObjectMapper RAW_OBJECT_MAPPER = EventConfig.createAcsRawEventObjectMapper();

    @Autowired
    protected CamelContext camelContext;

    @EndpointInject(uri = "mock:resultEndpoint")
    protected MockEndpoint mockEndpoint;

    @Autowired
    protected EventSender eventSender;

    @Autowired
    protected CamelMessageProducer camelMessageProducer;

    private ObjectMapper defaultObjectMapper;

    @Before
    public void setUp() throws Exception
    {
        this.defaultObjectMapper = camelMessageProducer.getObjectMapper();
        // Configure route
        configureRoute();
    }

    @After
    public void tearDown() throws Exception
    {
        MockEndpoint.resetMocks(camelContext);
        camelContext.removeRoute(ROUTE_ID);
        camelMessageProducer.setObjectMapper(defaultObjectMapper);
    }

    @Test
    public void testSendAndReceiveMockAcsRawEvent() throws Exception
    {
        // Generate random events
        RepositoryEvent event1 = EventMaker.getRandomRawAcsEvent();
        RepositoryEvent event2 = EventMaker.getRandomRawAcsEvent();

        // Set the expected messages
        mockEndpoint.expectedBodiesReceived(
                    Arrays.asList(RAW_OBJECT_MAPPER.writeValueAsString(event1), RAW_OBJECT_MAPPER.writeValueAsString(event2)));
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
    public void testSendAndReceiveMockAcsPublicEvent() throws Exception
    {
        // Override camelMessageProducer mapper
        camelMessageProducer.setObjectMapper(PUBLIC_OBJECT_MAPPER);

        // Generate random events
        EventV1<? extends ResourceV1> event1 = EventMaker.getRandomPublicAcsEvent();
        EventV1<? extends ResourceV1> event2 = EventMaker.getRandomPublicAcsEvent();

        // Set the expected messages
        mockEndpoint.expectedBodiesReceived(
                    Arrays.asList(PUBLIC_OBJECT_MAPPER.writeValueAsString(event1), PUBLIC_OBJECT_MAPPER.writeValueAsString(event2)));
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
    public void testSendAndReceiveMockActivitiRawEvent() throws Exception
    {
        // Generate random events
        String event1 = EventMaker.getRandomRawActivitiEvent();
        String event2 = EventMaker.getRandomRawActivitiEvent();

        // Set the expected messages
        mockEndpoint.expectedBodiesReceived(Arrays.asList(event1, event2));
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
    public void testSendAndReceiveMockActivitiPublicEvent() throws Exception
    {
        // Override camelMessageProducer mapper
        camelMessageProducer.setObjectMapper(PUBLIC_OBJECT_MAPPER);

        // Generate random events
        List<EventV1<? extends ResourceV1>> events1 = PublicActivitiEventInstance.PROCESS_CREATED.getEvents();
        List<EventV1<? extends ResourceV1>> events2 = PublicActivitiEventInstance.TASK_ASSIGNED.getEvents();

        List<EventV1<? extends ResourceV1>> allEvents = new ArrayList<>(events1);
        allEvents.addAll(events2);
        List<String> expectedBodies = new ArrayList<>();
        for (EventV1<? extends ResourceV1> event : allEvents)
        {
            expectedBodies.add(PUBLIC_OBJECT_MAPPER.writeValueAsString(event));
        }

        // Set the expected messages
        mockEndpoint.expectedBodiesReceived(expectedBodies);
        // Set the expected number of messages
        mockEndpoint.expectedMessageCount(12);

        // Send the 1st event. This should generate 11 events.
        eventSender.sendEvent(events1);
        // Send the 2nd event. This should generate 1 event
        eventSender.sendEvent(events2);

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
                from(getRoute())
                            .id(ROUTE_ID)
                            .to(mockEndpoint);
            }
        });
    }

    protected abstract String getRoute();
}
