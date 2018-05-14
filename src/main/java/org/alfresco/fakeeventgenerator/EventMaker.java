/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.fakeeventgenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.alfresco.event.model.BaseEvent;
import org.alfresco.event.model.BaseInternalEvent;
import org.alfresco.event.model.BaseInternalEventImpl;
import org.alfresco.event.model.ContentCreatedInternalEvent;
import org.alfresco.event.model.ContentCreatedInternalEventImpl;
import org.alfresco.event.model.ContentResourceImpl;
import org.alfresco.event.model.ProcessResourceImpl;
import org.alfresco.event.model.ProcessStartedInternalEvent;
import org.alfresco.event.model.ProcessStartedInternalEventImpl;
import org.alfresco.event.model.ResourceImpl;

/**
 * @author Jamal Kaabi-Mofrad
 */
public class EventMaker
{
    private static final Random RANDOM = new Random();
    private static final List<String> USER_LIST = new ArrayList<>();
    private static final List<String> READER_AUTHORITIES_LIST = new ArrayList<>();

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
    }

    public enum EventInstance
    {
        BASE_EVENT()
        {
            @Override
            public BaseInternalEvent getEvent()
            {
                return new BaseInternalEventImpl(UUID.randomUUID().toString(), "BASE_EVENT",
                            System.currentTimeMillis(), getUsername(), 
                            new ResourceImpl(UUID.randomUUID().toString(), "BaseType"),
                            getReaderAuthorities(),
                            null,
                            null,
                            "Repo");
            }
        },
        CONTENT_CREATED()
        {
            @Override
            public ContentCreatedInternalEvent getEvent()
            {
                return new ContentCreatedInternalEventImpl(UUID.randomUUID().toString(),
                            System.currentTimeMillis(), getUsername(),
                            new ContentResourceImpl(UUID.randomUUID().toString(), "Content", "cm:content"),
                            getReaderAuthorities(),
                            null,
                            null,
                            "ACS");
            }
        },
        PROCESS_STARTED()
        {
            @Override
            public ProcessStartedInternalEvent getEvent()
            {
                return new ProcessStartedInternalEventImpl(UUID.randomUUID().toString(),
                            System.currentTimeMillis(),getUsername(),
                            new ProcessResourceImpl(UUID.randomUUID().toString(), "Process"),
                            getReaderAuthorities(),
                            null,
                            null,
                            "APS");
            }
        };

        public abstract <T extends BaseEvent> T getEvent();
    }

    private static String getUsername()
    {
        int index = RANDOM.nextInt(USER_LIST.size());
        return USER_LIST.get(index);
    }

    private static List<String> getReaderAuthorities()
    {
        HashSet<String> readerAuthorities = new HashSet<String>(READER_AUTHORITIES_LIST.size());
        int numAuthorities = RANDOM.nextInt(READER_AUTHORITIES_LIST.size());
        for (int i = 0; i < numAuthorities; i++)
        {
            int index = RANDOM.nextInt(READER_AUTHORITIES_LIST.size());
            readerAuthorities.add(READER_AUTHORITIES_LIST.get(index));
        }
        
        return new ArrayList<String>(readerAuthorities);
    }

    public static BaseEvent getRandomEvent()
    {
        int i = RANDOM.nextInt(EventInstance.values().length);
        return EventInstance.values()[i].getEvent();
    }
}
