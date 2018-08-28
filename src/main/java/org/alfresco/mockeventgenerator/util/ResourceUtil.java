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
