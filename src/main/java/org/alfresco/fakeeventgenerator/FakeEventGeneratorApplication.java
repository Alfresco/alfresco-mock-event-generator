/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.fakeeventgenerator;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Jamal Kaabi-Mofrad
 */
@SpringBootApplication
public class FakeEventGeneratorApplication implements ApplicationRunner
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FakeEventGeneratorApplication.class);

    @Value("${faker.scheduled.enabled:false}")
    private boolean scheduledEnabled;

    @Value("${faker.scheduled.periodInSeconds:1}")
    private int periodInSeconds;

    @Value("${faker.scheduled.numOfEventsPerSecond:1000}")
    private int numOfEventsPerSecond;

    @Value("${faker.scheduled.runForInSeconds:10}")
    private int runForInSeconds;

    @Value("${faker.fixed.numOfEvents:10}")
    private int numOfEvents;

    @Value("${faker.fixed.pauseTimeInMillis:1000}")
    private long pauseTimeInMillis;

    @Value("${faker.startSendAtStartup:true}")
    private boolean startSendAtStartup;

    @Value("${faker.shutdownAfterSend:true}")
    private boolean shutdownAfterSend;

    @Value("${faker.waitBeforeShutdownInSeconds:2}")
    private int waitBeforeShutdownInSeconds;

    private final EventSender messageSender;

    @Autowired
    public FakeEventGeneratorApplication(EventSender messageSender)
    {
        this.messageSender = messageSender;
    }

    public static void main(String[] args)
    {
        SpringApplication.run(FakeEventGeneratorApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        if (startSendAtStartup)
        {
            if (scheduledEnabled)
            {
                messageSender.sendRandomEventAtFixedRate(periodInSeconds, numOfEventsPerSecond, runForInSeconds);
                // Adding 2 seconds to wait for the last run to finish
                TimeUnit.SECONDS.sleep(runForInSeconds + 2);
                LOGGER.info(buildReport(true));
            }
            else
            {
                messageSender.sendRandomEvent(numOfEvents, pauseTimeInMillis);
                LOGGER.info(buildReport(false));
            }

            if (shutdownAfterSend)
            {
                shutDown(waitBeforeShutdownInSeconds);
            }
        }
    }

    private void shutDown(int waitInSeconds) throws InterruptedException
    {
        TimeUnit.SECONDS.sleep(waitInSeconds);
        messageSender.shutdown();
        System.exit(0);
    }

    private String buildReport(boolean withAveragePerSecond)
    {
        StringBuilder sb = new StringBuilder(180);
        sb.append("****************************************")
                    .append("\n\tTotal events sent: ").append(messageSender.getTotalMessagesSent());
        if (withAveragePerSecond)
        {
            sb.append("\n\tAverage per second: ").append(messageSender.getTotalMessagesSent() / runForInSeconds);
        }
        sb.append("\n ****************************************");
        return sb.toString();
    }
}
