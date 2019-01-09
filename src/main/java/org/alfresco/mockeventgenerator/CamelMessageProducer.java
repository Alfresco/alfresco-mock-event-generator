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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.alfresco.mockeventgenerator.config.CamelRouteProperties;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Jamal Kaabi-Mofrad
 */
@Component
public class CamelMessageProducer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CamelMessageProducer.class);

    private final ProducerTemplate producer;
    private final List<CamelRouteProperties> endpoints;
    private final ExecutorService executor;
    private final AtomicInteger totalMessageCounter;
    private final AtomicBoolean aggregated;
    private ObjectMapper objectMapper;

    @Autowired
    public CamelMessageProducer(CamelContext camelContext, List<CamelRouteProperties> routeProperties, ObjectMapper objectMapper)
    {
        this.producer = camelContext.createProducerTemplate();
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        this.producer.setExecutorService(executor);
        this.endpoints = routeProperties;
        this.totalMessageCounter = new AtomicInteger(0);
        this.aggregated = new AtomicBoolean(false);
        this.objectMapper = objectMapper;
    }

    public void send(Object message, String endpoint) throws Exception
    {
        send(message, Collections.emptyMap(), endpoint);
    }

    public void send(Object message, Map<String, Object> headers, String endpoint) throws Exception
    {
        if (message instanceof Collection)
        {
            aggregated.set(true);
            Collection<?> msgs = (Collection<?>) message;
            for (Object obj : msgs)
            {
                sendImpl(obj, headers, endpoint);
            }
        }
        else
        {
            if (message instanceof String && ((String) message).startsWith("["))
            {
                aggregated.set(true);
            }
            sendImpl(message, headers, endpoint);
        }
    }

    private void sendImpl(Object message, Map<String, Object> headers, String endpoint) throws Exception
    {
        if (!(message instanceof String))
        {
            message = objectMapper.writeValueAsString(message);
        }

        if (endpoint != null && !endpoint.isEmpty())
        {
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Sending message:" + message.toString() + " \nTo endpoint:" + endpoint);
            }

            producer.sendBodyAndHeaders(getEndpointByKey(endpoint), message, headers);
        } else {
            for (CamelRouteProperties prop : endpoints) {
                String route = prop.getToRoute();

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Sending message:" + message.toString() + " \nTo endpoint:" + route);
                }

                producer.sendBodyAndHeaders(route, message, headers);
            }
        }

        totalMessageCounter.incrementAndGet();
    }

    private String getEndpointByKey(String key)
    {
        for(CamelRouteProperties prop: this.endpoints)
        {
            if (prop.getDestinationName().equals(key))
                return prop.getToRoute();
        }

        return null;
    }

    public void shutdown()
    {
        executor.shutdown();
    }

    public int getTotalMessagesSent()
    {
        return totalMessageCounter.get();
    }

    public boolean isAggregated()
    {
        return aggregated.get();
    }

    /**
     * For test purposes only
     */
    public void setObjectMapper(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    /**
     * For test purposes only
     */
    public ObjectMapper getObjectMapper()
    {
        return this.objectMapper;
    }
}
