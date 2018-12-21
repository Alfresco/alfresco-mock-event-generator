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
package org.alfresco.mockeventgenerator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.alfresco.mockeventgenerator.config.EventConfig.EventTypeCategory;
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
    private final EventTypeCategory eventTypeCategory;
    private final ScheduledExecutorService executorService;

    @Autowired
    public EventSender(CamelMessageProducer camelMessageProducer, EventTypeCategory eventTypeCategory)
    {
        this.camelMessageProducer = camelMessageProducer;
        this.eventTypeCategory = eventTypeCategory;
        this.executorService = Executors.newScheduledThreadPool(Runtime.getRuntime()
                    .availableProcessors() + 1);
    }

    public void sendRandomEvent(int numOfEvents)
    {
        sendRandomEvent(numOfEvents, 1000L);
    }

    public void sendRandomEvent(int numOfEvents, long pauseTimeMillis)
    {
        for (int i = 0; i < numOfEvents; i++)
        {
            sendEvent(eventTypeCategory.getRandomEvent(), "");
            if (pauseTimeMillis > 0)
            {
                try
                {
                    Thread.sleep(pauseTimeMillis);
                }
                catch (InterruptedException e)
                {
                    LOGGER.info(e.getMessage());
                }
            }
        }
    }

    public void sendRandomEventAtFixedRate(int periodInSeconds, int numOfEventsPerSecond, int runForInSeconds)
    {
        final AtomicBoolean cancelled = new AtomicBoolean(false);
        final ScheduledFuture<?> senderHandler = executorService
                    .scheduleAtFixedRate(() -> start(cancelled, numOfEventsPerSecond), 0, periodInSeconds, TimeUnit.SECONDS);

        try
        {
            executorService.schedule(() -> stop(cancelled, senderHandler), runForInSeconds, TimeUnit.SECONDS);
        }
        catch (Exception ex)
        {
            Thread.currentThread().interrupt();
        }
    }

    private void start(AtomicBoolean cancelled, int numOfEventsPerSecond)
    {
        int counter = 0;
        while (!cancelled.get() && counter < numOfEventsPerSecond)
        {
            sendEvent(eventTypeCategory.getRandomEvent(), "");
            counter++;
        }
    }

    private void stop(AtomicBoolean cancelled, ScheduledFuture<?> senderHandler)
    {
        cancelled.set(true);
        senderHandler.cancel(true);
    }

    public void sendEvent(Object event, String endpoint)
    {
        try
        {
            camelMessageProducer.send(event, endpoint);
        }
        catch (Exception ex)
        {
            if (getCause(ex, InterruptedException.class) != null)
            {
                Thread.currentThread().interrupt();
            }
            else
            {
                LOGGER.error("Error occurred while sending the message.");
                throw new RuntimeException(ex);
            }
        }
    }

    public void shutdown()
    {
        camelMessageProducer.shutdown();
        executorService.shutdown();
    }

    public int getTotalMessagesSent()
    {
        return camelMessageProducer.getTotalMessagesSent();
    }

    public boolean isAggregatedEvents()
    {
        return camelMessageProducer.isAggregated();
    }

    /**
     * Searches through the exception stack of the given throwable to find any instance
     * of the possible cause.  The top-level throwable will also be tested.
     *
     * @param throwable      the exception condition to search
     * @param possibleCauses the types of the exception conditions of interest
     * @return Returns the first instance that matches one of the given
     * possible types, or null if there is nothing in the stack
     */
    public static Throwable getCause(Throwable throwable, Class<?>... possibleCauses)
    {
        while (throwable != null)
        {
            for (Class<?> possibleCauseClass : possibleCauses)
            {
                Class<?> throwableClass = throwable.getClass();
                if (possibleCauseClass.isAssignableFrom(throwableClass))
                {
                    // We have a match
                    return throwable;
                }
            }
            // There was no match, so dig deeper
            Throwable cause = throwable.getCause();
            throwable = (throwable == cause) ? null : cause;
        }
        // Nothing found
        return null;
    }
}
