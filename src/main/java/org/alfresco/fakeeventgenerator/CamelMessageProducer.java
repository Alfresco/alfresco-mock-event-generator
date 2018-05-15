/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.fakeeventgenerator;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.alfresco.event.databind.EventObjectMapperFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Jamal Kaabi-Mofrad
 */
public class CamelMessageProducer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CamelMessageProducer.class);

    public static final String HEADER_NAME = "typeClassName";

    private static final ObjectMapper MAPPER = EventObjectMapperFactory.createInstance();

    private final ProducerTemplate producer;
    private final String endpoint;
    private final ExecutorService executor;

    public CamelMessageProducer(CamelContext camelContext, String endpoint)
    {
        this.producer = camelContext.createProducerTemplate();
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        this.producer.setExecutorService(executor);
        this.endpoint = endpoint;
    }

    public void send(Object message) throws Exception
    {
        send(message, Collections.singletonMap(HEADER_NAME, message.getClass().getName()));
    }

    public void send(Object message, Map<String, Object> headers) throws Exception
    {
        if (!(message instanceof String))
        {
            message = MAPPER.writeValueAsString(message);
        }
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Sending message:" + message.toString() + " \nTo endpoint:" + endpoint);
        }
        producer.sendBodyAndHeaders(endpoint, message, headers);
    }

    public void shutdown()
    {
        executor.shutdown();
    }
}
