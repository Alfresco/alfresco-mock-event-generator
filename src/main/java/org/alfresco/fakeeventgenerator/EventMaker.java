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

import org.alfresco.fakeeventgenerator.model.ContentAdded;
import org.alfresco.fakeeventgenerator.model.Event;
import org.alfresco.fakeeventgenerator.model.ProcessStarted;

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

    enum EventInstance
    {
        BASE_EVENT()
        {
            @Override
            public Event getEvent()
            {
                return new Event().setId(UUID.randomUUID().toString())
                            .setPrincipal(getUsername())
                            .setTimestamp(System.currentTimeMillis())
                            .setResources(Collections.singletonList("urn:alfresco:base:event:" + UUID.randomUUID().toString()));

            }
        },
        CONTENT_CREATED()
        {
            @Override
            public Event getEvent()
            {
                return new ContentAdded().setId(UUID.randomUUID().toString())
                            .setPrincipal(getUsername())
                            .setTimestamp(System.currentTimeMillis())
                            .setResources(Collections.singletonList("urn:alfresco:content:nodeId:" + UUID.randomUUID().toString()));

            }
        },
        PROCESS_STARTED()
        {
            @Override
            public Event getEvent()
            {
                return new ProcessStarted().setId(UUID.randomUUID().toString())
                            .setPrincipal(getUsername())
                            .setTimestamp(System.currentTimeMillis())
                            .setResources(Collections.singletonList("urn:alfresco:process:started:id:" + UUID.randomUUID().toString()));

            }
        };

        public abstract Event getEvent();
    }

    private static String getUsername()
    {
        int index = RANDOM.nextInt(USER_LIST.size());
        return USER_LIST.get(index);
    }

    public static Event getRandomEvent()
    {
        int i = RANDOM.nextInt(EventInstance.values().length);
        return EventInstance.values()[i].getEvent();
    }
}
