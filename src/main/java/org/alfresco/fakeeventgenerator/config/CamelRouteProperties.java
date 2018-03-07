/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.fakeeventgenerator.config;

/**
 * @author Jamal Kaabi-Mofrad
 */
public class CamelRouteProperties
{
    private String destinationName;
    private String fromRoute;
    private String toRoute;

    public String getDestinationName()
    {
        return destinationName;
    }

    public void setDestinationName(String destinationName)
    {
        this.destinationName = destinationName;
    }

    public String getFromRoute()
    {
        return fromRoute;
    }

    public void setFromRoute(String fromRoute)
    {
        this.fromRoute = fromRoute;
    }

    public String getToRoute()
    {
        return toRoute;
    }

    public void setToRoute(String toRoute)
    {
        this.toRoute = toRoute;
    }
}
