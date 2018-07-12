/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.fakeeventgenerator.config;

import org.alfresco.event.databind.EventObjectMapperFactory;
import org.alfresco.event.model.internal.InternalEvent;
import org.alfresco.events.types.RepositoryEvent;
import org.alfresco.fakeeventgenerator.EventMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author Jamal Kaabi-Mofrad
 */
@Configuration
public class EventConfig
{
    private String eventTypeStr;

    @Autowired
    public EventConfig(@Value("${faker.eventType}") String eventTypeStr)
    {
        this.eventTypeStr = eventTypeStr;
    }

    @Bean
    public EventTypeCategory eventTypeCategory()
    {
        return EventTypeCategory.valueOf(eventTypeStr);
    }

    @Bean
    public ObjectMapper objectMapper()
    {
        EventTypeCategory eventTypeCategory = eventTypeCategory();
        switch (eventTypeCategory)
        {
            case INTERNAL_EVENT:
            {
                return EventObjectMapperFactory.createInstance();
            }
            case ACS_RAW_EVENT:
            {
                return createAcsRawEventObjectMapper();
            }
            default:
            {
                throw new RuntimeException("No mapper is defined for the: " + eventTypeCategory);
            }
        }
    }

    public static ObjectMapper createAcsRawEventObjectMapper()
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return mapper;
    }

    /**
     * @author Jamal Kaabi-Mofrad
     */
    public enum EventTypeCategory
    {
        INTERNAL_EVENT()
        {
            @Override
            public InternalEvent getRandomEvent()
            {
                return EventMaker.getRandomInternalEvent();
            }
        },
        ACS_RAW_EVENT()
        {
            @Override
            public RepositoryEvent getRandomEvent()
            {
                return EventMaker.getRandomRawAcsEvent();
            }
        };

        public abstract Object getRandomEvent();
    }
}
