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
package org.alfresco.mockeventgenerator.config.kafka;

import org.alfresco.mockeventgenerator.config.CamelRouteProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Jamal Kaabi-Mofrad
 */
@ConfigurationProperties(prefix = "messaging.to.kafka")
public class KafkaProperties
{
    private final CamelRouteProperties camelRoute = new CamelRouteProperties();
    private String host;
    private int port;

    public CamelRouteProperties getCamelRoute()
    {
        return camelRoute;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }
}