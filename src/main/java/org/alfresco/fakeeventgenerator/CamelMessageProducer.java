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

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author Jamal Kaabi-Mofrad
 */
public class CamelMessageProducer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CamelMessageProducer.class);

    private ProducerTemplate producer;
    private String endpoint;
    private static ObjectMapper objectMapper;

    static
    {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public CamelMessageProducer(ProducerTemplate producer, String endpoint)
    {
        this.producer = producer;
        this.endpoint = endpoint;
    }

    public void send(Object message) throws Exception
    {
        send(message, Collections.emptyMap());
    }

    public void send(Object message, Map<String, Object> headers) throws Exception
    {
        if (objectMapper != null && !(message instanceof String))
        {
            message = objectMapper.writeValueAsString(message);
        }
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Sending message:" + message.toString()+" \nTo endpoint:"+endpoint);
        }
        producer.sendBodyAndHeaders(endpoint, message, headers);
    }
}
