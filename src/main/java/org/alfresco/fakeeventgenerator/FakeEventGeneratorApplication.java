/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.fakeeventgenerator;

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
    @Value("${faker.numOfEvents:10}")
    private int numOfEvents;

    @Value("${faker.pauseTimeInMillis:1000}")
    private long pauseTimeInMillis;

    @Value("${faker.startSendAtStartup:true}")
    private boolean startSendAtStartup;

    @Value("${faker.shutdownAfterSend:true}")
    private boolean shutdownAfterSend;

    @Value("${faker.waitBeforeShutdownInMillis:2000}")
    private long waitBeforeShutdownInMillis;

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
            messageSender.sendRandomEvent(numOfEvents, pauseTimeInMillis);

            if (shutdownAfterSend)
            {
                // Wait before shutting down
                Thread.sleep(waitBeforeShutdownInMillis);
                System.exit(0);
            }
        }
    }
}
