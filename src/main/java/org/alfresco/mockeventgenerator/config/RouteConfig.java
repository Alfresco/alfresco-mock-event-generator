/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
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
