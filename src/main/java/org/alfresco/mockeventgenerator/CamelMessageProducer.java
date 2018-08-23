/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.mockeventgenerator;

import java.util.Collection;
import java.util.Collections;
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
    private final String endpoint;
    private final ExecutorService executor;
    private final AtomicInteger totalMessageCounter;
    private final AtomicBoolean aggregated;
    private ObjectMapper objectMapper;

    @Autowired
    public CamelMessageProducer(CamelContext camelContext, CamelRouteProperties routeProperties, ObjectMapper objectMapper)
    {
        this.producer = camelContext.createProducerTemplate();
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        this.producer.setExecutorService(executor);
        this.endpoint = routeProperties.getToRoute();
        this.totalMessageCounter = new AtomicInteger(0);
        this.aggregated = new AtomicBoolean(false);
        this.objectMapper = objectMapper;
    }

    public void send(Object message) throws Exception
    {
        send(message, Collections.emptyMap());
    }

    public void send(Object message, Map<String, Object> headers) throws Exception
    {
        if (message instanceof Collection)
        {
            aggregated.set(true);
            Collection<?> msgs = (Collection<?>) message;
            for (Object obj : msgs)
            {
                sendImpl(obj, headers);
            }
        }
        else
        {
            if (message instanceof String && ((String) message).startsWith("["))
            {
                aggregated.set(true);
            }
            sendImpl(message, headers);
        }
    }

    private void sendImpl(Object message, Map<String, Object> headers) throws Exception
    {
        if (!(message instanceof String))
        {
            message = objectMapper.writeValueAsString(message);
        }
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Sending message:" + message.toString() + " \nTo endpoint:" + endpoint);
        }
        producer.sendBodyAndHeaders(endpoint, message, headers);
        totalMessageCounter.incrementAndGet();
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
