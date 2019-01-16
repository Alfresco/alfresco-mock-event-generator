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
package org.alfresco.mockeventgenerator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.alfresco.event.databind.EventObjectMapperFactory;
import org.alfresco.event.model.EventV1;
import org.alfresco.event.model.ResourceV1;
import org.alfresco.mockeventgenerator.EventController.CloudConnectorPayload;
import org.alfresco.mockeventgenerator.EventController.EventRequestPayload;
import org.alfresco.mockeventgenerator.EventMaker.PublicActivitiEventInstance;
import org.alfresco.mockeventgenerator.config.CamelRouteProperties;
import org.alfresco.mockeventgenerator.config.EventConfig;
import org.alfresco.mockeventgenerator.model.CloudConnectorIntegrationRequest;
import org.alfresco.sync.events.types.RepositoryEvent;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Jamal Kaabi-Mofrad
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AbstractCamelTest
{
    private static final ObjectMapper PUBLIC_OBJECT_MAPPER = EventObjectMapperFactory.createInstance();
    private static final ObjectMapper RAW_OBJECT_MAPPER = EventConfig.createAcsRawEventObjectMapper();
    private static final String BASE_URL = "http://localhost:{0}/alfresco/mock/";
    private static final MessageFormat MESSAGE_FORMAT = new MessageFormat(BASE_URL);

    @Autowired
    protected CamelContext camelContext;

    @Autowired
    protected EventSender eventSender;

    @Autowired
    protected CamelMessageProducer camelMessageProducer;

    @Autowired
    protected TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    protected List<MockEndpoint> mockEndpoints = new ArrayList<>();
    private ObjectMapper defaultObjectMapper;
    private String baseUrl;

    @Before
    public void setUp()
    {
        this.defaultObjectMapper = camelMessageProducer.getObjectMapper();
        this.baseUrl = MESSAGE_FORMAT.format(new String[] { Integer.toString(port) });

        // Construct MockEndpoints
        getRoutes().forEach(p -> mockEndpoints.add(getMockEndpoint(p.getToRoute())));
    }

    @After
    public void tearDown()
    {
        MockEndpoint.resetMocks(camelContext);
        mockEndpoints.clear();
        camelMessageProducer.setObjectMapper(defaultObjectMapper);
    }

    @Test
    public void testSendAndReceiveMockAcsRawEvent() throws Exception
    {
        // Override camelMessageProducer mapper
        camelMessageProducer.setObjectMapper(RAW_OBJECT_MAPPER);

        // Generate random events
        RepositoryEvent event1 = EventMaker.getRandomRawAcsEvent();
        RepositoryEvent event2 = EventMaker.getRandomRawAcsEvent();

        // Set the expected messages
        setExpectedBodiesReceived(RAW_OBJECT_MAPPER.writeValueAsString(event1), RAW_OBJECT_MAPPER.writeValueAsString(event2));
        // Set the expected number of messages
        setExpectedMessageCount(2);

        // Send the 1st event
        eventSender.sendEvent(event1);
        // Send the 2nd event
        eventSender.sendEvent(event2);

        // Checks that the received message count is equal to the number of messages sent
        // Also, checks the received message body is equal to the sent message
        assertIsSatisfied();
    }

    @Test
    public void testSendAndReceiveMockAcsPublicEvent() throws Exception
    {
        // Generate random events
        EventV1<? extends ResourceV1> event1 = EventMaker.getRandomPublicAcsEvent();
        EventV1<? extends ResourceV1> event2 = EventMaker.getRandomPublicAcsEvent();

        // Set the expected messages
        setExpectedBodiesReceived(PUBLIC_OBJECT_MAPPER.writeValueAsString(event1), PUBLIC_OBJECT_MAPPER.writeValueAsString(event2));
        // Set the expected number of messages
        setExpectedMessageCount(2);

        // Send the 1st event
        eventSender.sendEvent(event1);
        // Send the 2nd event
        eventSender.sendEvent(event2);

        // Checks that the received message count is equal to the number of messages sent
        // Also, checks the received message body is equal to the sent message
        assertIsSatisfied();
    }

    @Test
    public void testSendAndReceiveMockActivitiRawEvent() throws Exception
    {
        // Generate random events
        String event1 = EventMaker.getRandomRawActivitiEvent();
        String event2 = EventMaker.getRandomRawActivitiEvent();

        // Set the expected messages
        setExpectedBodiesReceived(event1, event2);
        // Set the expected number of messages
        setExpectedMessageCount(2);

        // Send the 1st event
        eventSender.sendEvent(event1);
        // Send the 2nd event
        eventSender.sendEvent(event2);

        // Checks that the received message count is equal to the number of messages sent
        // Also, checks the received message body is equal to the sent message
        assertIsSatisfied();
    }

    @Test
    public void testSendAndReceiveMockActivitiPublicEvent() throws Exception
    {
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
        setExpectedBodiesReceived(expectedBodies.toArray());
        // Set the expected number of messages
        setExpectedMessageCount(12);

        // Send the 1st event. This should generate 11 events.
        eventSender.sendEvent(events1);
        // Send the 2nd event. This should generate 1 event
        eventSender.sendEvent(events2);

        // Checks that the received message count is equal to the number of messages sent
        // Also, checks the received message body is equal to the sent message
        assertIsSatisfied();
    }

    @Test
    public void testSendAndReceiveMockConnectorEvent() throws Exception
    {
        // Generate random events
        CloudConnectorIntegrationRequest event1 = EventMaker.getRandomCloudConnectorEvent();
        CloudConnectorIntegrationRequest event2 = EventMaker.getRandomCloudConnectorEvent();

        // Set the expected messages
        setExpectedBodiesReceived(PUBLIC_OBJECT_MAPPER.writeValueAsString(event1), PUBLIC_OBJECT_MAPPER.writeValueAsString(event2));
        // Set the expected number of messages
        setExpectedMessageCount(2);

        // Send the 1st event
        eventSender.sendEvent(event1);
        // Send the 2nd event
        eventSender.sendEvent(event2);

        // Checks that the received message count is equal to the number of messages sent
        // Also, checks the received message body is equal to the sent message
        assertIsSatisfied();
    }

    @Test
    public void testMockEventsViaRestApi()
    {
        final int numOfEvents = 2;
        EventRequestPayload payload = new EventRequestPayload();
        payload.setNumOfEvents(numOfEvents);
        payload.setPauseTimeInMillis(-1L);

        // Set the expected number of messages
        setExpectedMessageCount(numOfEvents);
        // Send event via Rest API
        restTemplate.postForLocation(baseUrl + "events", payload);

        // Checks that the received message count is equal to the number of messages sent
        assertIsSatisfied();
    }

    @Test
    public void testCustomConnectorEventViaRestApi_inBoundVars() throws Exception
    {
        Map<String, Object> inBoundVariables = new HashMap<>();

        CloudConnectorPayload payload = new CloudConnectorPayload();
        inBoundVariables.put("properties", Collections.singletonMap("cm:title", "Test Title"));
        inBoundVariables.put("nodeId", UUID.randomUUID().toString());
        payload.setInBoundVariables(inBoundVariables);

        // Set the expected number of messages
        setExpectedMessageCount(1);
        // Send event via Rest API
        restTemplate.postForLocation(baseUrl + "connector-event", payload);

        checkMessageContains(PUBLIC_OBJECT_MAPPER.writeValueAsString(inBoundVariables));

        // Checks that the received message count is equal to the number of messages sent
        assertIsSatisfied();
    }

    @Test
    public void testCustomConnectorEventViaRestApi_inBoundVarsWithQueueName() throws Exception
    {
        final MockEndpoint mockEndpoint = getMockEndpoint("mock:testDynamicRoute");
        Map<String, Object> inBoundVariables = new HashMap<>();

        CloudConnectorPayload payload = new CloudConnectorPayload();
        inBoundVariables.put("properties", Collections.singletonMap("cm:title", "Test Title"));
        inBoundVariables.put("nodeId", UUID.randomUUID().toString());
        payload.setInBoundVariables(inBoundVariables);

        // Set the expected number of messages for this dynamic route
        mockEndpoint.setExpectedMessageCount(1);
        // As we are overriding the configured endpoints,
        // we shouldn't receive any messages from them.
        setExpectedMessageCount(0);
        // Send event via Rest API and provide the destination name
        restTemplate.postForLocation(baseUrl + "connector-event?destinationName=testDynamicRoute", payload);

        String receivedEvent = getBody(mockEndpoint, 0);
        assertNotNull(receivedEvent);
        assertTrue(receivedEvent.contains(PUBLIC_OBJECT_MAPPER.writeValueAsString(inBoundVariables)));

        // Checks that the received message count is equal to the number of messages sent
        mockEndpoint.assertIsSatisfied();
        // The configured endpoints should not receive any messages
        assertIsSatisfied();
    }

    @Test
    public void testCustomConnectorEventViaRestApi_inBoundAndOutBoundVars() throws Exception
    {
        String nodeId = UUID.randomUUID().toString();
        Map<String, Object> inBoundVariables = new HashMap<>();
        Map<String, Object> outBoundVariables = new HashMap<>();

        CloudConnectorPayload payload = new CloudConnectorPayload();
        // Set inBoundVariables
        inBoundVariables.put("properties", Collections.singletonMap("cm:description", "Test Description."));
        inBoundVariables.put("nodeId", nodeId);
        payload.setInBoundVariables(inBoundVariables);
        // Set outBoundVariables
        outBoundVariables.put("nodeId", nodeId);
        payload.setOutBoundVariables(outBoundVariables);

        // Set the expected number of messages
        setExpectedMessageCount(1);
        // Send event via Rest API
        restTemplate.postForLocation(baseUrl + "connector-event", payload);

        checkMessageContains(PUBLIC_OBJECT_MAPPER.writeValueAsString(inBoundVariables),
                    PUBLIC_OBJECT_MAPPER.writeValueAsString(outBoundVariables));

        // Checks that the received message count is equal to the number of messages sent
        assertIsSatisfied();
    }

    protected void setExpectedBodiesReceived(Object... expectedBodies)
    {
        mockEndpoints.forEach(m -> m.expectedBodiesReceived(expectedBodies));
    }

    protected void setExpectedMessageCount(int expectedCount)
    {
        mockEndpoints.forEach(m -> m.expectedMessageCount(expectedCount));
    }

    protected void assertIsSatisfied()
    {
        mockEndpoints.forEach(m -> {
            try
            {
                m.assertIsSatisfied();
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        });
    }

    protected void checkMessageContains(String... variables)
    {
        mockEndpoints.forEach(mock -> {
            String receivedEvent = getBody(mock, 0);
            assertNotNull(receivedEvent);
            for (String var : variables)
            {
                assertTrue(receivedEvent.contains(var));
            }
        });
    }

    protected String getBody(MockEndpoint mockEndpoint, int index)
    {
        List<Exchange> list = mockEndpoint.getExchanges();
        if (list.size() <= index)
        {
            return null;
        }
        return list.get(index).getIn().getBody().toString();
    }

    protected MockEndpoint getMockEndpoint(String route)
    {
        assertNotNull("The route uri cannot be null.", route);
        return camelContext.getEndpoint(route, MockEndpoint.class);
    }

    protected abstract List<CamelRouteProperties> getRoutes();
}
