/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.fakeeventgenerator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.alfresco.fakeeventgenerator.config.EventConfig.EventTypeCategory;
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
    private final AtomicInteger totalMessageCounter;

    @Autowired
    public EventSender(CamelMessageProducer camelMessageProducer, EventTypeCategory eventTypeCategory)
    {
        this.camelMessageProducer = camelMessageProducer;
        this.eventTypeCategory = eventTypeCategory;
        this.executorService = Executors.newScheduledThreadPool(Runtime.getRuntime()
                    .availableProcessors() + 1);
        this.totalMessageCounter = new AtomicInteger(0);
    }

    public void sendRandomEvent(int numOfEvents)
    {
        sendRandomEvent(numOfEvents, 1000L);
    }

    public void sendRandomEvent(int numOfEvents, long pauseTimeMillis)
    {
        for (int i = 0; i < numOfEvents; i++)
        {
            try
            {
                sendEvent(eventTypeCategory.getRandomEvent());
                Thread.sleep(pauseTimeMillis);
            }
            catch (InterruptedException e)
            {
                LOGGER.info(e.getMessage());
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
            sendEvent(eventTypeCategory.getRandomEvent());
            counter++;
        }
    }

    private void stop(AtomicBoolean cancelled, ScheduledFuture<?> senderHandler)
    {
        cancelled.set(true);
        senderHandler.cancel(true);
    }

    public void sendEvent(Object event)
    {
        try
        {
            camelMessageProducer.send(event);
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
        totalMessageCounter.incrementAndGet();
    }

    public void shutdown()
    {
        camelMessageProducer.shutdown();
        executorService.shutdown();
    }

    public int getTotalMessagesSent()
    {
        return totalMessageCounter.get();
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
