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
package org.alfresco.mockeventgenerator.config;

import java.util.List;

import org.alfresco.event.databind.EventObjectMapperFactory;
import org.alfresco.event.model.EventV1;
import org.alfresco.event.model.ResourceV1;
import org.alfresco.mockeventgenerator.EventMaker;
import org.alfresco.mockeventgenerator.model.CloudConnectorIntegrationRequest;
import org.alfresco.sync.events.types.RepositoryEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

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
    private String eventCategoryStr;

    @Autowired
    public EventConfig(@Value("${generator.eventCategory}") String eventCategoryStr)
    {
        this.eventCategoryStr = eventCategoryStr;
    }

    @Bean
    public EventTypeCategory eventTypeCategory()
    {
        return EventTypeCategory.valueOf(eventCategoryStr);
    }

    @Bean
    public ObjectMapper objectMapper()
    {
        EventTypeCategory eventTypeCategory = eventTypeCategory();
        switch (eventTypeCategory)
        {
            case ACS_RAW_EVENT:
            {
                return createAcsRawEventObjectMapper();
            }
            case ACS_PUBLIC_EVENT:
            {
                return EventObjectMapperFactory.createInstance();
            }
            case ACTIVITI_RAW_EVENT:
            {
                // This won't be used, as the content is String. We are returning a default
                // ObjectMapper to avoid chalking for null mapper. See CamelMessageProducer#send() method.
                return new ObjectMapper();
            }
            case ACTIVITI_PUBLIC_EVENT:
            {
                return EventObjectMapperFactory.createInstance();
            }
            case CLOUD_CONNECTOR_EVENT:
            {
                return EventObjectMapperFactory.createInstance();
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
        ACS_RAW_EVENT()
        {
            @Override
            public RepositoryEvent getRandomEvent()
            {
                return EventMaker.getRandomRawAcsEvent();
            }
        },
        ACS_PUBLIC_EVENT()
        {
            @Override
            public EventV1<? extends ResourceV1> getRandomEvent()
            {
                return EventMaker.getRandomPublicAcsEvent();
            }
        },
        ACTIVITI_RAW_EVENT()
        {
            @Override
            public String getRandomEvent()
            {
                return EventMaker.getRandomRawActivitiEvent();
            }
        },
        ACTIVITI_PUBLIC_EVENT()
        {
            @Override
            public List<EventV1<? extends ResourceV1>> getRandomEvent()
            {
                return EventMaker.getRandomPublicActivitiEvent();
            }
        },
        CLOUD_CONNECTOR_EVENT()
        {
            @Override
            public CloudConnectorIntegrationRequest getRandomEvent()
            {
                return EventMaker.getRandomCloudConnectorEvent();
            }
        };

        public abstract Object getRandomEvent();
    }

    /*
    * Override the default converter, so we can use a different ObjectMapper
    * rather than the one intended for events.
    */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter()
    {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        jsonConverter.setObjectMapper(objectMapper);
        return jsonConverter;
    }
}
