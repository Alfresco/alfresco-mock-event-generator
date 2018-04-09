/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.fakeeventgenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.alfresco.event.model.BaseEvent;
import org.alfresco.event.model.BaseEventImpl;
import org.alfresco.event.model.ContentCreatedEvent;
import org.alfresco.event.model.ContentCreatedEventImpl;
import org.alfresco.event.model.ProcessStartedEvent;
import org.alfresco.event.model.ProcessStartedEventImpl;

/**
 * @author Jamal Kaabi-Mofrad
 */
public class EventMaker
{
    private static final Random RANDOM = new Random();
    private static final List<String> USER_LIST = new ArrayList<>();

    static
    {
        USER_LIST.add("johndoe");
        USER_LIST.add("sblogg");
        USER_LIST.add("graymond");
        USER_LIST.add("jsixpack");
        USER_LIST.add("jmeatball");
    }

    public enum EventInstance
    {
        BASE_EVENT()
        {
            @Override
            public BaseEvent getEvent()
            {
                return new BaseEventImpl(UUID.randomUUID().toString(), "BASE_EVENT",
                            System.currentTimeMillis(), "Repo", getUsername());
            }
        },
        CONTENT_CREATED()
        {
            @Override
            public ContentCreatedEvent getEvent()
            {
                return new ContentCreatedEventImpl(UUID.randomUUID().toString(),
                            System.currentTimeMillis(), "Repo", getUsername(),
                            Collections.singletonList("urn:alfresco:content:nodeId:" + UUID.randomUUID().toString()));
            }
        },
        PROCESS_STARTED()
        {
            @Override
            public ProcessStartedEvent getEvent()
            {
                return new ProcessStartedEventImpl(UUID.randomUUID().toString(),
                            System.currentTimeMillis(), "Aps", getUsername(),
                            Collections.singletonList("urn:alfresco:process:started:id:" + UUID.randomUUID().toString()));
            }
        };

        public abstract <T extends BaseEvent> T getEvent();
    }

    private static String getUsername()
    {
        int index = RANDOM.nextInt(USER_LIST.size());
        return USER_LIST.get(index);
    }

    public static BaseEvent getRandomEvent()
    {
        int i = RANDOM.nextInt(EventInstance.values().length);
        return EventInstance.values()[i].getEvent();
    }
}
