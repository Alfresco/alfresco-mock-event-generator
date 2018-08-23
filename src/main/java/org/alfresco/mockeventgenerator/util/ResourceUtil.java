/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.mockeventgenerator.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.springframework.util.FileCopyUtils;

/**
 * @author Jamal Kaabi-Mofrad
 */
public class ResourceUtil
{
    public static String getResourceFileAsString(String fileName)
    {
        InputStream inputStream = ResourceUtil.class.getClassLoader()
                    .getResourceAsStream(fileName);
        StringWriter writer = new StringWriter();
        try
        {
            FileCopyUtils.copy(new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8.name())), writer);
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }

        return writer.toString();
    }
}
