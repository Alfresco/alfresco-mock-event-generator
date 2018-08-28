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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jamal Kaabi-Mofrad
 */
@RestController
@RequestMapping("/alfresco/mock/events")
public class EventController
{
    @Value("${generator.fixed.pauseTimeInMillis:1000}")
    private long pauseTimeInMillis;

    private final EventSender messageSender;

    @Autowired
    public EventController(EventSender messageSender)
    {
        this.messageSender = messageSender;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity sendEvents(@RequestBody EventRequestPayload payload)
    {
        if (payload.getNumOfEvents() == null || payload.getNumOfEvents() <= 0)
        {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        long pauseTime = pauseTimeInMillis;
        if (payload.getPauseTimeInMillis() != null && payload.getPauseTimeInMillis() >= 0L)
        {
            pauseTime = payload.getPauseTimeInMillis();
        }
        messageSender.sendRandomEvent(payload.getNumOfEvents(), pauseTime);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    public static class EventRequestPayload
    {
        private Integer numOfEvents;
        private Long pauseTimeInMillis;

        public Integer getNumOfEvents()
        {
            return numOfEvents;
        }

        public void setNumOfEvents(Integer numOfEvents)
        {
            this.numOfEvents = numOfEvents;
        }

        public Long getPauseTimeInMillis()
        {
            return pauseTimeInMillis;
        }

        public void setPauseTimeInMillis(Long pauseTimeInMillis)
        {
            this.pauseTimeInMillis = pauseTimeInMillis;
        }
    }
}
