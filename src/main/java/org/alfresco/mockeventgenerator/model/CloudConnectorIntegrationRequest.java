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
package org.alfresco.mockeventgenerator.model;

import java.util.Map;

/**
 * @author Jamal Kaabi-Mofrad
 */
public class CloudConnectorIntegrationRequest
{
    private String appName;
    private String appVersion;
    private String serviceName;
    private String serviceFullName;
    private String serviceType;
    private String serviceVersion;
    private IntegrationContext integrationContext;

    public String getAppName()
    {
        return appName;
    }

    public void setAppName(String appName)
    {
        this.appName = appName;
    }

    public String getAppVersion()
    {
        return appVersion;
    }

    public void setAppVersion(String appVersion)
    {
        this.appVersion = appVersion;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }

    public String getServiceFullName()
    {
        return serviceFullName;
    }

    public void setServiceFullName(String serviceFullName)
    {
        this.serviceFullName = serviceFullName;
    }

    public String getServiceType()
    {
        return serviceType;
    }

    public void setServiceType(String serviceType)
    {
        this.serviceType = serviceType;
    }

    public String getServiceVersion()
    {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion)
    {
        this.serviceVersion = serviceVersion;
    }

    public IntegrationContext getIntegrationContext()
    {
        return integrationContext;
    }

    public void setIntegrationContext(IntegrationContext integrationContext)
    {
        this.integrationContext = integrationContext;
    }

    public static class IntegrationContext
    {
        private String id;
        private String processInstanceId;
        private String processDefinitionId;
        private String connectorType;
        private String activityElementId;
        private Map<String, Object> inBoundVariables;
        private Map<String, Object> outBoundVariables;

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public String getProcessInstanceId()
        {
            return processInstanceId;
        }

        public void setProcessInstanceId(String processInstanceId)
        {
            this.processInstanceId = processInstanceId;
        }

        public String getProcessDefinitionId()
        {
            return processDefinitionId;
        }

        public void setProcessDefinitionId(String processDefinitionId)
        {
            this.processDefinitionId = processDefinitionId;
        }

        public String getConnectorType()
        {
            return connectorType;
        }

        public void setConnectorType(String connectorType)
        {
            this.connectorType = connectorType;
        }

        public String getActivityElementId()
        {
            return activityElementId;
        }

        public void setActivityElementId(String activityElementId)
        {
            this.activityElementId = activityElementId;
        }

        public Map<String, Object> getInBoundVariables()
        {
            return inBoundVariables;
        }

        public void setInBoundVariables(Map<String, Object> inBoundVariables)
        {
            this.inBoundVariables = inBoundVariables;
        }

        public Map<String, Object> getOutBoundVariables()
        {
            return outBoundVariables;
        }

        public void setOutBoundVariables(Map<String, Object> outBoundVariables)
        {
            this.outBoundVariables = outBoundVariables;
        }
    }
}
