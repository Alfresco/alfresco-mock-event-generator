/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.fakeeventgenerator;

import org.alfresco.event.model.internal.BaseInternalEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jamal Kaabi-Mofrad
 */
@Service
public class EventSender
{
    private static final Logger LOGGER = LoggerFactory.getLogger(EventSender.class);

    private final CamelMessageProducer camelMessageProducer;

    @Autowired
    public EventSender(CamelMessageProducer camelMessageProducer)
    {
        this.camelMessageProducer = camelMessageProducer;
    }

    public void sendRandomEvent(int numOfEvents)
    {
        sendRandomEvent(numOfEvents, 1000L);
    }

    public void sendRandomEvent(int numOfEvents, long pauseTimeMillis)
    {
        for (int i = 0; i < numOfEvents; i++)
        {
            BaseInternalEvent event = EventMaker.getRandomEvent();
            try
            {
                sendEvent(event);
                Thread.sleep(pauseTimeMillis);
            }
            catch (InterruptedException e)
            {
                LOGGER.info(e.getMessage());
            }
        }
    }

    public void sendEvent(BaseInternalEvent event)
    {
        try
        {
            camelMessageProducer.send(event);
        }
        catch (Exception ex)
        {
            LOGGER.error("Couldn't send the message.", ex);
        }
    }
}
