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
package org.alfresco.mockeventgenerator.config;

import org.springframework.context.annotation.Bean;

/**
 * @author Jamal Kaabi-Mofrad
 */
public class RouteConfig
{
    private final CamelRouteProperties routeProperties;

    public RouteConfig(CamelRouteProperties routeProperties)
    {
        this.routeProperties = routeProperties;
    }

    @Bean
    public CamelRouteProperties routeProperties()
    {
        return routeProperties;
    }
}
