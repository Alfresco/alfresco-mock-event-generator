/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.fakeeventgenerator;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

/**
 * @author Jamal Kaabi-Mofrad
 */
@SpringBootApplication
public class FakeEventGeneratorApplication implements ApplicationRunner
{

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
        Map<String, String> map = args.getNonOptionArgs().stream().map(str -> str.split("="))
                    .collect(Collectors.toMap(a -> a[0], a -> a.length > 1 ? a[1] : ""));

        int numOfEvents = StringUtils.isEmpty(map.get("event")) ? 10 : Integer.parseInt(map.get("event"));
        long pauseTime = StringUtils.isEmpty(map.get("pause")) ? 1000L : Long.parseLong(map.get("pause"));

        messageSender.sendRandomEvent(numOfEvents, pauseTime);
        TimeUnit.SECONDS.sleep(5);
        System.exit(-1);
    }
}
