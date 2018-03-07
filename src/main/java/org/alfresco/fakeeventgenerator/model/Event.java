/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.fakeeventgenerator.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Jamal Kaabi-Mofrad
 */
//TODO remove this class when alfresco-event-model jar becomes available
public class Event implements Serializable
{
    private static final long serialVersionUID = -332558645532053092L;

    private static final String TYPE = "BASE_EVENT";

    private String id;
    private String type;
    private long timestamp;
    private String principal;
    private String producer;
    private List<String> resources;

    public Event()
    {
        type = TYPE;
    }

    public String getId()
    {
        return id;
    }

    public Event setId(String id)
    {
        this.id = id;
        return this;
    }

    public String getType()
    {
        return type;
    }

    public Event setType(String type)
    {
        this.type = type;
        return this;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public Event setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
        return this;
    }

    public String getPrincipal()
    {
        return principal;
    }

    public Event setPrincipal(String principal)
    {
        this.principal = principal;
        return this;
    }

    public String getProducer()
    {
        return producer;
    }

    public Event setProducer(String producer)
    {
        this.producer = producer;
        return this;
    }

    public List<String> getResources()
    {
        return resources;
    }

    public Event setResources(List<String> resources)
    {
        this.resources = resources;
        return this;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder(250);
        sb.append("Event [id=").append(id).append(", type=").append(type).append(", timestamp=").append(timestamp).append(", principal=")
                    .append(principal).append(", producer=").append(producer).append(", resources=").append(resources).append(']');
        return sb.toString();
    }
}
