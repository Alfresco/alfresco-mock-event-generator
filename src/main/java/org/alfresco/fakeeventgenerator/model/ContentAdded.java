/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.fakeeventgenerator.model;

/**
 * @author Jamal Kaabi-Mofrad
 */
//TODO remove this class when alfresco-event-model jar becomes available
public class ContentAdded extends Event
{
    private static final String TYPE = "CONTENT_CREATED";

    public ContentAdded()
    {
        setType(TYPE);
    }

    @Override
    public String toString()
    {
        return super.toString();
    }
}
