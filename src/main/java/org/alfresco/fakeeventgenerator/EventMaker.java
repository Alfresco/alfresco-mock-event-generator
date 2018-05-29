/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.fakeeventgenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.alfresco.event.model.BaseEvent;
import org.alfresco.event.model.ContentResourceImpl;
import org.alfresco.event.model.ProcessResourceImpl;
import org.alfresco.event.model.Resource;
import org.alfresco.event.model.ResourceImpl;
import org.alfresco.event.model.internal.BaseInternalEvent;
import org.alfresco.event.model.internal.BaseInternalEventImpl;
import org.alfresco.event.model.internal.ContentInternalEvent;
import org.alfresco.event.model.internal.ContentInternalEventImpl;
import org.alfresco.event.model.internal.ProcessInternalEvent;
import org.alfresco.event.model.internal.ProcessInternalEventImpl;

/**
 * @author Jamal Kaabi-Mofrad
 */
public class EventMaker
{
    private static final Random RANDOM = new Random();
    private static final List<String> USER_LIST = new ArrayList<>();
    private static final List<String> READER_AUTHORITIES_LIST = new ArrayList<>();
    private static final Map<String, Long> PRODUCER_INDICES = new HashMap<String, Long>();

    private static final String PRODUCER_BASE = "BaseProducer";
    private static final String PRODUCER_ACS = "ACS";
    private static final String PRODUCER_APS = "APS";

    static
    {
        USER_LIST.add("johndoe");
        USER_LIST.add("sblogg");
        USER_LIST.add("graymond");
        USER_LIST.add("jsixpack");
        USER_LIST.add("jmeatball");
        READER_AUTHORITIES_LIST.add("GROUP_A");
        READER_AUTHORITIES_LIST.add("GROUP_B");
        READER_AUTHORITIES_LIST.add("GROUP_C");
        READER_AUTHORITIES_LIST.add("GROUP_D");
        READER_AUTHORITIES_LIST.add("GROUP_E");
        PRODUCER_INDICES.put(PRODUCER_BASE, 0L);
        PRODUCER_INDICES.put(PRODUCER_ACS, 0L);
        PRODUCER_INDICES.put(PRODUCER_APS, 0L);
    }

    public enum EventInstance
    {
        BASE_EVENT()
        {
            @Override
            public BaseInternalEvent getEvent()
            {
                return new BaseInternalEventImpl("BASE_EVENT",
                            BaseEvent.class.getCanonicalName(),
                            getUsername(), 
                            new ResourceImpl(UUID.randomUUID().toString(), "BaseType"),
                            getReaderAuthorities(),
                            null,
                            null,
                            PRODUCER_BASE,
                            getProducerIndex(PRODUCER_BASE),
                            System.currentTimeMillis());
            }
        },
        CONTENT_CREATED()
        {
            @Override
            public ContentInternalEvent getEvent()
            {
                return new ContentInternalEventImpl("CONTENT_CREATED",
                            getUsername(),
                            new ContentResourceImpl(UUID.randomUUID().toString(), "Content", "cm:content"),
                            getReaderAuthorities(),
                            null,
                            null,
                            PRODUCER_ACS,
                            getProducerIndex(PRODUCER_ACS),
                            System.currentTimeMillis());
            }
        },
        PROCESS_STARTED()
        {
            @Override
            public ProcessInternalEvent getEvent()
            {
                return new ProcessInternalEventImpl("PROCESS_STARTED",
                            getUsername(),
                            new ProcessResourceImpl(UUID.randomUUID().toString(), "Process"),
                            getReaderAuthorities(),
                            null,
                            null,
                            PRODUCER_APS,
                            getProducerIndex(PRODUCER_APS),
                            System.currentTimeMillis());
            }
        };

        public abstract <T extends BaseInternalEvent<R>, R extends Resource> T getEvent();
    }

    private static String getUsername()
    {
        int index = RANDOM.nextInt(USER_LIST.size());
        return USER_LIST.get(index);
    }

    private static Set<String> getReaderAuthorities()
    {
        HashSet<String> readerAuthorities = new HashSet<String>(READER_AUTHORITIES_LIST.size());
        int numAuthorities = RANDOM.nextInt(READER_AUTHORITIES_LIST.size());
        if (numAuthorities == 0)
        {
            numAuthorities = 1;
        }
        for (int i = 0; i < numAuthorities; i++)
        {
            int index = RANDOM.nextInt(READER_AUTHORITIES_LIST.size());
            readerAuthorities.add(READER_AUTHORITIES_LIST.get(index));
        }
        
        return readerAuthorities;
    }

    public static BaseInternalEvent getRandomEvent()
    {
        int i = RANDOM.nextInt(EventInstance.values().length);
        return EventInstance.values()[i].getEvent();
    }
    
    public static Long getProducerIndex(String producer)
    {
        Long index = PRODUCER_INDICES.get(producer);
        PRODUCER_INDICES.put(producer, index+1);
        return index;
    }
}
