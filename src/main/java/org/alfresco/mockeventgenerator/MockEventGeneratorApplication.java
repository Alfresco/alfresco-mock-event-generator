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
public class MockEventGeneratorApplication implements ApplicationRunner
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MockEventGeneratorApplication.class);

    @Value("${generator.scheduled.enabled:false}")
    private boolean scheduledEnabled;

    @Value("${generator.scheduled.periodInSeconds:1}")
    private int periodInSeconds;

    @Value("${generator.scheduled.numOfEventsPerSecond:1000}")
    private int numOfEventsPerSecond;

    @Value("${generator.scheduled.runForInSeconds:10}")
    private int runForInSeconds;

    @Value("${generator.fixed.numOfEvents:10}")
    private int numOfEvents;

    @Value("${generator.fixed.pauseTimeInMillis:1000}")
    private long pauseTimeInMillis;

    @Value("${generator.startSendAtStartup:true}")
    private boolean startSendAtStartup;

    @Value("${generator.shutdownAfterSend:true}")
    private boolean shutdownAfterSend;

    @Value("${generator.waitBeforeShutdownInSeconds:2}")
    private int waitBeforeShutdownInSeconds;

    private final EventSender messageSender;

    @Autowired
    public MockEventGeneratorApplication(EventSender messageSender)
    {
        this.messageSender = messageSender;
    }

    public static void main(String[] args)
    {
        SpringApplication.run(MockEventGeneratorApplication.class, args);
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
        sb.append("\n\tEvents were aggregated: ").append(messageSender.isAggregatedEvents());
        sb.append("\n****************************************");
        return sb.toString();
    }
}
