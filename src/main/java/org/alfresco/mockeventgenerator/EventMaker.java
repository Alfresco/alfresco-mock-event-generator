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

import static java.lang.System.currentTimeMillis;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.alfresco.event.model.EventV1;
import org.alfresco.event.model.HierarchyEntry;
import org.alfresco.event.model.ResourceV1;
import org.alfresco.event.model.acs.AuthorityResourceV1;
import org.alfresco.event.model.acs.NodeResourceV1;
import org.alfresco.event.model.acs.PermissionResourceV1;
import org.alfresco.event.model.activiti.ActivitiCloudRuntimeResourceV1;
import org.alfresco.event.model.activiti.ActivityResourceV1;
import org.alfresco.event.model.activiti.ProcessResourceV1;
import org.alfresco.event.model.activiti.SequenceFlowResourceV1;
import org.alfresco.event.model.activiti.TaskCandidateResourceV1;
import org.alfresco.event.model.activiti.TaskResourceV1;
import org.alfresco.event.model.activiti.VariableResourceV1;
import org.alfresco.mockeventgenerator.model.CloudConnectorIntegrationRequest;
import org.alfresco.mockeventgenerator.model.CloudConnectorIntegrationRequest.IntegrationContext;
import org.alfresco.mockeventgenerator.util.ResourceUtil;
import org.alfresco.mockeventgenerator.util.UserInfo;
import org.alfresco.sync.events.types.NodeAddedEvent;
import org.alfresco.sync.events.types.RepositoryEvent;
import org.alfresco.sync.events.types.TransactionCommittedEvent;
import org.alfresco.sync.events.types.authority.AuthorityAddedToGroupEvent;
import org.alfresco.sync.events.types.permission.LocalPermissionGrantedEvent;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Jamal Kaabi-Mofrad
 */
public class EventMaker
{
    private static final Random RANDOM = new Random();
    private static final List<UserInfo> USER_LIST = new ArrayList<>();
    private static final List<String> GROUP_LIST = new ArrayList<>();
    private static final List<String> PERMISSIONS = new ArrayList<>();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    static
    {
        USER_LIST.add(new UserInfo("John", "Doe", 25, "johndoe"));
        USER_LIST.add(new UserInfo("Sam", "Blogg", 31, "sblogg"));
        USER_LIST.add(new UserInfo("Glen", "Raymond", 45, "graymond"));
        USER_LIST.add(new UserInfo("Joe", "Sixpack", 41, "jsixpack"));
        USER_LIST.add(new UserInfo("Jane", "Meatball", 28, "jmeatball"));

        GROUP_LIST.add("GROUP_A");
        GROUP_LIST.add("GROUP_B");
        GROUP_LIST.add("GROUP_C");
        GROUP_LIST.add("GROUP_D");
        GROUP_LIST.add("GROUP_E");

        PERMISSIONS.add("SiteManager");
        PERMISSIONS.add("SiteCollaborator");
        PERMISSIONS.add("SiteContributor");
        PERMISSIONS.add("SiteConsumer");
    }

    public enum RawAcsEventInstance
    {
        NODE_ADDED_EVENT()
        {
            @Override
            public NodeAddedEvent getEvent()
            {
                final String siteId = "testsite" + getMilliSecondPart();
                final String docName = "testDoc" + getMilliSecondPart() + ".txt";
                return new NodeAddedEvent(0,
                            docName,
                            getUUID(),
                            currentTimeMillis(),
                            "",
                            siteId,
                            getUUID(),
                            "cm:content",
                            Stream.of("/Company Home/Sites/" + siteId + "/documentLibrary/Docs/" + docName).collect(Collectors.toList()),
                            Stream.of(getUUID(6)).collect(Collectors.toList()),
                            getUserName(),
                            currentTimeMillis(),
                            null,
                            Stream.of("sys:localized", "sys:referenceable", "cm:auditable").collect(Collectors.toSet()),
                            new HashMap<>());
            }
        },
        AUTHORITY_ADDED_TO_GROUP_EVENT()
        {
            @Override
            public AuthorityAddedToGroupEvent getEvent()
            {
                return AuthorityAddedToGroupEvent.builder()
                            .parentGroup(getGroup())
                            .authorityName(getUserName())
                            .seqNumber(1)
                            .txnId(getUUID())
                            .networkId("")
                            .timestamp(currentTimeMillis())
                            .username(getUserName())
                            .build();
            }
        },
        LOCAL_PERMISSION_GRANTED_EVENT()
        {
            @Override
            public LocalPermissionGrantedEvent getEvent()
            {
                final String siteId = "testsite" + getMilliSecondPart();
                return LocalPermissionGrantedEvent.builder()
                            .authority(getGroup())
                            .permission(getPermission())
                            .seqNumber(2)
                            .name("Docs")
                            .txnId(getUUID())
                            .timestamp(currentTimeMillis())
                            .networkId("")
                            .siteId(siteId)
                            .nodeId(getUUID())
                            .nodeType("cm:folder")
                            .paths(Stream.of("/Company Home/Sites/" + siteId + "/documentLibrary/Docs").collect(Collectors.toList()))
                            .parentNodeIds(Stream.of(getUUID(5)).collect(Collectors.toList()))
                            .username(getUserName())
                            .nodeModificationTime(currentTimeMillis())
                            .aspects(Stream.of("sys:localized", "cm:titled", "sys:referenceable", "cm:auditable").collect(Collectors.toSet()))
                            .nodeProperties(new HashMap<>())
                            .build();
            }
        },
        TRANSACTION_COMMITTED_EVENT()
        {
            @Override
            public TransactionCommittedEvent getEvent()
            {
                return new TransactionCommittedEvent(RANDOM.nextInt(3),
                            getUUID(),
                            "",
                            currentTimeMillis(),
                            getUserName(),
                            null);
            }
        };

        public abstract RepositoryEvent getEvent();
    }

    public enum PublicAcsEventInstance
    {
        NODE_ADDED_EVENT()
        {
            @Override
            public EventV1<NodeResourceV1> getEvent()
            {
                List<HierarchyEntry> hierarchyEntries = getNodeHierarchyEntries();
                NodeResourceV1 contentResource = new NodeResourceV1(getUUID(), hierarchyEntries, "cm:content");

                return new EventV1<>("NODEADDED", getUserName(), contentResource);
            }
        },
        AUTHORITY_ADDED_TO_GROUP_EVENT()
        {
            @Override
            public EventV1<AuthorityResourceV1> getEvent()
            {
                AuthorityResourceV1 resource = new AuthorityResourceV1(getUserName(), new ArrayList<>());
                resource.setParentGroup(getGroup());

                return new EventV1<>("AUTHADDEDTOGROUP", "admin", resource);
            }
        },
        LOCAL_PERMISSION_GRANTED_EVENT()
        {
            @Override
            public EventV1<PermissionResourceV1> getEvent()
            {
                List<HierarchyEntry> hierarchyEntries = getNodeHierarchyEntries();

                PermissionResourceV1 resource = new PermissionResourceV1(getUUID(), hierarchyEntries, "cm:folder");
                resource.setAuthority(getGroup());
                resource.setPermission(getPermission());

                return new EventV1<>("LOCALPERMISSIONGRANTED", getUserName(), resource);
            }
        },
        TRANSACTION_COMMITTED_EVENT()
        {
            @Override
            public EventV1<ResourceV1> getEvent()
            {
                ResourceV1 resource = new ResourceV1(getUUID(), new ArrayList<>());

                return new EventV1<>("TRANSACTION_COMMITTED", getUserName(), resource);
            }
        };

        public abstract EventV1<? extends ResourceV1> getEvent();
    }

    public enum RawActivitiEventInstance
    {
        PROCESS_CREATED()
        {
            @Override
            public String getEvent()
            {
                return getDecoratedEvent("processStarted.json");
            }
        },
        TASK_ASSIGNED()
        {
            @Override
            public String getEvent()
            {
                return getDecoratedEvent("taskAssigned.json");
            }
        },
        TASK_COMPLETED()
        {
            @Override
            public String getEvent()
            {
                return getDecoratedEvent("taskCompleted.json");
            }
        };

        private static final ConcurrentMap<String, JSONArray> CACHE = new ConcurrentHashMap<>();

        private static JSONArray getEventResource(String eventFileName)
        {
            JSONArray eventArray = CACHE.get(eventFileName);
            if (eventArray == null)
            {
                String eventStr = ResourceUtil.getResourceFileAsString("events/activiti/" + eventFileName);
                eventArray = new JSONArray(eventStr);
                CACHE.putIfAbsent(eventFileName, eventArray);
            }
            return eventArray;
        }

        private static String getDecoratedEvent(String eventFileName)
        {
            JSONArray jsonArray = getEventResource(eventFileName);
            for (Object obj : jsonArray)
            {
                JSONObject eventJson = (JSONObject) obj;
                eventJson.put("id", getUUID());
                eventJson.put("timestamp", currentTimeMillis());

                JSONObject entity = eventJson.getJSONObject("entity");
                if(entity.has("startDate"))
                {
                    entity.put("startDate",  getJacksonFormatDateTime());
                }
                if(entity.has("claimedDate"))
                {
                    entity.put("claimedDate",  getJacksonFormatDateTime());
                }
                if(entity.has("createdDate"))
                {
                    entity.put("createdDate",  getJacksonFormatDateTime());
                }
            }
            return jsonArray.toString();
        }

        public abstract String getEvent();
    }

    public enum PublicActivitiEventInstance
    {
        PROCESS_CREATED()
        {
            @Override
            public List<EventV1<? extends ResourceV1>> getEvents()
            {
                List<EventV1<? extends ResourceV1>> events = new ArrayList<>();

                // Event 1
                ProcessResourceV1 processCreatedResource = new ProcessResourceV1(getUUID(), null);
                setCommonValues(processCreatedResource, getUUID());
                processCreatedResource.setStatus("RUNNING");
                processCreatedResource.setProcessDefinitionId("SimpleProcess:1:" + getUUID());
                processCreatedResource.setProcessDefinitionKey("SimpleProcess");
                EventV1<ProcessResourceV1> processCreatedEvent = new EventV1<>("PROCESS_CREATED", getUserName(), processCreatedResource);
                events.add(processCreatedEvent);

                // Event 2
                VariableResourceV1<String> variableCreatedResource01 = new VariableResourceV1<>(getUUID(), null);
                setCommonValues(variableCreatedResource01, "firstName");
                variableCreatedResource01.setName("firstName");
                variableCreatedResource01.setType("string");
                variableCreatedResource01.setProcessInstanceId(processCreatedResource.getEntityId());
                variableCreatedResource01.setTaskVariable(false);
                variableCreatedResource01.setValue("Paulo");
                EventV1<VariableResourceV1<String>> variableCreatedEvent01 = new EventV1<>("VARIABLE_CREATED", null, variableCreatedResource01);
                events.add(variableCreatedEvent01);

                // Event 3
                VariableResourceV1<String> variableCreatedResource02 = new VariableResourceV1<>(getUUID(), null);
                setCommonValues(variableCreatedResource02, "lastName");
                variableCreatedResource02.setName("lastName");
                variableCreatedResource02.setType("string");
                variableCreatedResource02.setProcessInstanceId(processCreatedResource.getEntityId());
                variableCreatedResource02.setTaskVariable(false);
                variableCreatedResource02.setValue("Silva");
                EventV1<VariableResourceV1<String>> variableCreatedEvent02 = new EventV1<>("VARIABLE_CREATED", null, variableCreatedResource02);
                events.add(variableCreatedEvent02);

                // Event 4
                VariableResourceV1<Integer> variableCreatedResource03 = new VariableResourceV1<>(getUUID(), null);
                setCommonValues(variableCreatedResource03, "age");
                variableCreatedResource03.setName("age");
                variableCreatedResource03.setType("integer");
                variableCreatedResource03.setProcessInstanceId(processCreatedResource.getEntityId());
                variableCreatedResource03.setTaskVariable(false);
                variableCreatedResource03.setValue(25);
                EventV1<VariableResourceV1<Integer>> variableCreatedEvent03 = new EventV1<>("VARIABLE_CREATED", null, variableCreatedResource03);
                events.add(variableCreatedEvent03);

                // Event 5
                ProcessResourceV1 processStartedResource = new ProcessResourceV1(getUUID(), null);
                setCommonValues(processCreatedResource, processCreatedResource.getEntityId());
                processStartedResource.setStatus("RUNNING");
                processStartedResource.setProcessDefinitionId(processCreatedResource.getProcessDefinitionId());
                processStartedResource.setProcessDefinitionKey(processCreatedResource.getProcessDefinitionKey());
                EventV1<ProcessResourceV1> processStartedEvent = new EventV1<>("PROCESS_STARTED", processCreatedEvent.getPrincipal(),
                            processStartedResource);
                events.add(processStartedEvent);

                // Event 6
                ActivityResourceV1 activityStartedResource01 = new ActivityResourceV1(getUUID(), null);
                setCommonValues(activityStartedResource01, processCreatedResource.getEntityId());
                activityStartedResource01.setActivityType("startEvent");
                activityStartedResource01.setElementId("startEvent1");
                activityStartedResource01.setProcessDefinitionId(processCreatedResource.getProcessDefinitionId());
                activityStartedResource01.setProcessInstanceId(processCreatedResource.getEntityId());
                EventV1<ActivityResourceV1> activityStartedEvent01 = new EventV1<>("ACTIVITY_STARTED", null, activityStartedResource01);
                events.add(activityStartedEvent01);

                // Event 7
                ActivityResourceV1 activityCompletedResource = new ActivityResourceV1(getUUID(), null);
                setCommonValues(activityCompletedResource, processCreatedResource.getEntityId());
                activityCompletedResource.setActivityType("startEvent");
                activityCompletedResource.setElementId("startEvent1");
                activityCompletedResource.setProcessDefinitionId(processCreatedResource.getProcessDefinitionId());
                activityCompletedResource.setProcessInstanceId(processCreatedResource.getEntityId());
                EventV1<ActivityResourceV1> activityCompletedEvent = new EventV1<>("ACTIVITY_COMPLETED", null, activityCompletedResource);
                events.add(activityCompletedEvent);

                // Event 8
                SequenceFlowResourceV1 sequenceFlowTakenResource = new SequenceFlowResourceV1(getUUID(), null);
                setCommonValues(sequenceFlowTakenResource, processCreatedResource.getEntityId());
                sequenceFlowTakenResource.setProcessInstanceId(processCreatedResource.getEntityId());
                sequenceFlowTakenResource.setProcessDefinitionId(processCreatedResource.getProcessDefinitionId());
                sequenceFlowTakenResource.setSourceActivityElementId("startEvent1");
                sequenceFlowTakenResource.setSourceActivityType("org.activiti.bpmn.model.StartEvent");
                sequenceFlowTakenResource.setTargetActivityElementId("sid-" + getUUID());
                sequenceFlowTakenResource.setTargetActivityName("Perform action");
                sequenceFlowTakenResource.setTargetActivityType("org.activiti.bpmn.model.UserTask");
                EventV1<SequenceFlowResourceV1> sequenceFlowTakenEvent = new EventV1<>("SEQUENCE_FLOW_TAKEN", null, sequenceFlowTakenResource);
                events.add(sequenceFlowTakenEvent);

                // Event 9
                ActivityResourceV1 activityStartedResource02 = new ActivityResourceV1(getUUID(), null);
                setCommonValues(activityStartedResource02, processCreatedResource.getEntityId());
                activityStartedResource02.setActivityName("Perform action");
                activityStartedResource02.setActivityType("userTask");
                activityStartedResource02.setElementId(sequenceFlowTakenResource.getTargetActivityElementId());
                activityStartedResource02.setProcessDefinitionId(processCreatedResource.getProcessDefinitionId());
                activityStartedResource02.setProcessInstanceId(processCreatedResource.getEntityId());
                EventV1<ActivityResourceV1> activityStartedEvent02 = new EventV1<>("ACTIVITY_STARTED", null, activityStartedResource02);
                events.add(activityStartedEvent02);

                // Event 10
                TaskCandidateResourceV1 taskCandidateGroupAddedResource = new TaskCandidateResourceV1(getUUID(), null);
                setCommonValues(taskCandidateGroupAddedResource, "hr");
                taskCandidateGroupAddedResource.setTaskId(getUUID());
                taskCandidateGroupAddedResource.setGroupId("hr");
                EventV1<TaskCandidateResourceV1> taskCandidateGroupAddedEvent = new EventV1<>("TASK_CANDIDATE_GROUP_ADDED", null,
                            taskCandidateGroupAddedResource);
                events.add(taskCandidateGroupAddedEvent);

                // Event 11
                TaskResourceV1 taskCreatedResource = new TaskResourceV1(getUUID(), null);
                setCommonValues(taskCreatedResource, taskCandidateGroupAddedResource.getTaskId());

                taskCreatedResource.setName("Perform action");
                taskCreatedResource.setProcessDefinitionId(processCreatedResource.getProcessDefinitionId());
                taskCreatedResource.setProcessInstanceId(processCreatedResource.getEntityId());
                taskCreatedResource.setPriority(50);
                taskCreatedResource.setStatus("CREATED");
                taskCreatedResource.setCreatedDate(new Date());
                taskCreatedResource.setClaimedDate(new Date());
                EventV1<TaskResourceV1> taskCreatedEvent = new EventV1<>("TASK_CREATED", null, taskCreatedResource);
                events.add(taskCreatedEvent);

                return events;
            }
        },
        TASK_ASSIGNED()
        {
            @Override
            public List<EventV1<? extends ResourceV1>> getEvents()
            {
                TaskResourceV1 taskAssignedResource = new TaskResourceV1(getUUID(), null);
                setCommonValues(taskAssignedResource, getUUID());
                taskAssignedResource.setName("Perform action");
                taskAssignedResource.setProcessDefinitionId("SimpleProcess:1:" + getUUID());
                taskAssignedResource.setProcessInstanceId(getUUID());
                taskAssignedResource.setPriority(50);
                taskAssignedResource.setStatus("ASSIGNED");
                taskAssignedResource.setAssignee(getUserName());
                taskAssignedResource.setCreatedDate(new Date());
                taskAssignedResource.setClaimedDate(new Date());
                EventV1<TaskResourceV1> taskAssignedEvent = new EventV1<>("TASK_ASSIGNED", null, taskAssignedResource);

                return Collections.singletonList(taskAssignedEvent);
            }
        },
        TASK_COMPLETED()
        {
            @Override
            public List<EventV1<? extends ResourceV1>> getEvents()
            {
                List<EventV1<? extends ResourceV1>> events = new ArrayList<>();

                // Event 1
                TaskResourceV1 taskCompletedResource = new TaskResourceV1(getUUID(), null);
                setCommonValues(taskCompletedResource, getUUID());
                taskCompletedResource.setName("Perform action");
                taskCompletedResource.setProcessDefinitionId("SimpleProcess:1:" + getUUID());
                taskCompletedResource.setProcessInstanceId(getUUID());
                taskCompletedResource.setPriority(50);
                taskCompletedResource.setStatus("ASSIGNED");
                taskCompletedResource.setAssignee(getUserName());
                taskCompletedResource.setCreatedDate(new Date());
                taskCompletedResource.setClaimedDate(new Date());
                EventV1<TaskResourceV1> taskCompletedEvent = new EventV1<>("TASK_COMPLETED", null, taskCompletedResource);
                events.add(taskCompletedEvent);

                // Event 2
                TaskCandidateResourceV1 taskCandidateGroupRemovedResource = new TaskCandidateResourceV1(getUUID(), null);
                setCommonValues(taskCandidateGroupRemovedResource, "hr");
                taskCandidateGroupRemovedResource.setTaskId(getUUID());
                taskCandidateGroupRemovedResource.setGroupId("hr");
                EventV1<TaskCandidateResourceV1> taskCandidateGroupRemovedEvent = new EventV1<>("TASK_CANDIDATE_GROUP_REMOVED", null,
                            taskCandidateGroupRemovedResource);
                events.add(taskCandidateGroupRemovedEvent);

                // Event 3
                ActivityResourceV1 activityCompletedResource01 = new ActivityResourceV1(getUUID(), null);
                setCommonValues(activityCompletedResource01, taskCompletedResource.getProcessInstanceId());
                activityCompletedResource01.setActivityName("Perform action");
                activityCompletedResource01.setActivityType("userTask");
                activityCompletedResource01.setElementId("sid-" + getUUID());
                activityCompletedResource01.setProcessDefinitionId(taskCompletedResource.getProcessDefinitionId());
                activityCompletedResource01.setProcessInstanceId(taskCompletedResource.getProcessInstanceId());
                EventV1<ActivityResourceV1> activityCompletedEvent01 = new EventV1<>("ACTIVITY_COMPLETED", null, activityCompletedResource01);
                events.add(activityCompletedEvent01);

                // Event 4
                SequenceFlowResourceV1 sequenceFlowTakenResource = new SequenceFlowResourceV1(getUUID(), null);
                setCommonValues(sequenceFlowTakenResource, taskCompletedResource.getProcessInstanceId());
                sequenceFlowTakenResource.setProcessInstanceId(taskCompletedResource.getProcessInstanceId());
                sequenceFlowTakenResource.setProcessDefinitionId(taskCompletedResource.getProcessDefinitionId());
                sequenceFlowTakenResource.setSourceActivityElementId(activityCompletedResource01.getElementId());
                sequenceFlowTakenResource.setSourceActivityName("Perform action");
                sequenceFlowTakenResource.setSourceActivityType("org.activiti.bpmn.model.UserTask");
                sequenceFlowTakenResource.setTargetActivityElementId("sid-" + getUUID());
                sequenceFlowTakenResource.setTargetActivityType("org.activiti.bpmn.model.EndEvent");
                EventV1<SequenceFlowResourceV1> sequenceFlowTakenEvent = new EventV1<>("SEQUENCE_FLOW_TAKEN", null, sequenceFlowTakenResource);
                events.add(sequenceFlowTakenEvent);

                // Event 5
                ActivityResourceV1 activityStartedResource = new ActivityResourceV1(getUUID(), null);
                setCommonValues(activityStartedResource, taskCompletedResource.getProcessInstanceId());
                activityStartedResource.setActivityType("endEvent");
                activityStartedResource.setElementId(sequenceFlowTakenResource.getTargetActivityElementId());
                activityStartedResource.setProcessDefinitionId(taskCompletedResource.getProcessDefinitionId());
                activityStartedResource.setProcessInstanceId(taskCompletedResource.getProcessInstanceId());
                EventV1<ActivityResourceV1> activityStartedEvent = new EventV1<>("ACTIVITY_STARTED", null, activityStartedResource);
                events.add(activityStartedEvent);

                // Event 6
                ActivityResourceV1 activityCompletedResource02 = new ActivityResourceV1(getUUID(), null);
                setCommonValues(activityCompletedResource02, taskCompletedResource.getProcessInstanceId());
                activityCompletedResource02.setActivityType("endEvent");
                activityCompletedResource02.setElementId(sequenceFlowTakenResource.getTargetActivityElementId());
                activityCompletedResource02.setProcessDefinitionId(taskCompletedResource.getProcessDefinitionId());
                activityCompletedResource02.setProcessInstanceId(taskCompletedResource.getProcessInstanceId());
                EventV1<ActivityResourceV1> activityCompletedEvent02 = new EventV1<>("ACTIVITY_COMPLETED", null, activityCompletedResource02);
                events.add(activityCompletedEvent02);

                // Event 7
                VariableResourceV1<String> variableDeletedResource01 = new VariableResourceV1<>(getUUID(), null);
                setCommonValues(variableDeletedResource01, "firstName");
                variableDeletedResource01.setName("firstName");
                variableDeletedResource01.setType("string");
                variableDeletedResource01.setProcessInstanceId(taskCompletedResource.getProcessInstanceId());
                variableDeletedResource01.setTaskVariable(false);
                EventV1<VariableResourceV1<String>> variableDeletedEvent01 = new EventV1<>("VARIABLE_DELETED", null, variableDeletedResource01);
                events.add(variableDeletedEvent01);

                // Event 8
                VariableResourceV1<String> variableDeletedResource02 = new VariableResourceV1<>(getUUID(), null);
                setCommonValues(variableDeletedResource02, "lastName");
                variableDeletedResource02.setName("lastName");
                variableDeletedResource02.setType("string");
                variableDeletedResource02.setProcessInstanceId(taskCompletedResource.getProcessInstanceId());
                variableDeletedResource02.setTaskVariable(false);
                EventV1<VariableResourceV1<String>> variableDeletedEvent02 = new EventV1<>("VARIABLE_DELETED", null, variableDeletedResource02);
                events.add(variableDeletedEvent02);

                // Event 9
                VariableResourceV1<Integer> variableDeletedResource03 = new VariableResourceV1<>(getUUID(), null);
                setCommonValues(variableDeletedResource03, "age");
                variableDeletedResource03.setName("age");
                variableDeletedResource03.setType("integer");
                variableDeletedResource03.setProcessInstanceId(taskCompletedResource.getProcessInstanceId());
                variableDeletedResource03.setTaskVariable(false);
                EventV1<VariableResourceV1<Integer>> variableDeletedEvent03 = new EventV1<>("VARIABLE_DELETED", null, variableDeletedResource03);
                events.add(variableDeletedEvent03);

                // Event 10
                ProcessResourceV1 processCompletedResource = new ProcessResourceV1(getUUID(), null);
                setCommonValues(processCompletedResource, taskCompletedResource.getProcessInstanceId());
                processCompletedResource.setStatus("COMPLETED");
                processCompletedResource.setProcessDefinitionId(taskCompletedResource.getProcessDefinitionId());
                EventV1<ProcessResourceV1> processCompletedEvent = new EventV1<>("PROCESS_COMPLETED", getUserName(), processCompletedResource);
                events.add(processCompletedEvent);

                return events;
            }
        };

        private static void setCommonValues(ActivitiCloudRuntimeResourceV1 resource, String entityId)
        {
            resource.setEntityId(entityId);
            resource.setTimestamp(currentTimeMillis());
            resource.setAppName("default-app");
            resource.setAppVersion("");
            resource.setServiceFullName("rb-my-app");
            resource.setServiceName("rb-my-app");
            resource.setServiceVersion("");
            resource.setServiceType("runtime-bundle");
        }

        public abstract List<EventV1<? extends ResourceV1>> getEvents();
    }

    public enum CloudConnectorEventInstance
    {
        PROCESS_STARTED()
        {
            @Override
            public CloudConnectorIntegrationRequest getEvent()
            {
                IntegrationContext context = new IntegrationContext();
                context.setId(getUUID());
                context.setProcessInstanceId(getUUID());
                context.setProcessDefinitionId("ConnectorProcess:1:" + getUUID());
                context.setActivityElementId("sid-" + getUUID().toUpperCase());
                context.setConnectorType("Example Connector");
                context.setOutBoundVariables(new HashMap<>());
                Map<String, Object> inBoundVariables = new HashMap<>();
                UserInfo user = getUser();
                inBoundVariables.put("firstName", user.getFirstName());
                inBoundVariables.put("lastName", user.getLastName());
                inBoundVariables.put("age", user.getAge());
                context.setInBoundVariables(inBoundVariables);

                CloudConnectorIntegrationRequest request = new CloudConnectorIntegrationRequest();
                request.setAppName("default-app");
                request.setAppVersion("");
                request.setServiceName("rb-my-app");
                request.setServiceFullName("rb-my-app");
                request.setServiceType("runtime-bundle");
                request.setServiceVersion("");
                request.setIntegrationContext(context);

                return request;
            }
        };

        public abstract CloudConnectorIntegrationRequest getEvent();
    }

    private static String getUUID()
    {
        return UUID.randomUUID().toString();
    }

    private static List<String> getUUID(int numOfUUIDS)
    {
        return IntStream.range(0, numOfUUIDS)
                    .mapToObj(i -> getUUID())
                    .collect(Collectors.toList());
    }

    private static String getUserName()
    {
        return getUser().getUserName();
    }

    private static UserInfo getUser()
    {
        int index = RANDOM.nextInt(USER_LIST.size());
        return USER_LIST.get(index);
    }

    private static String getGroup()
    {
        int index = RANDOM.nextInt(GROUP_LIST.size());
        return GROUP_LIST.get(index);
    }

    private static String getPermission()
    {
        int index = RANDOM.nextInt(PERMISSIONS.size());
        return PERMISSIONS.get(index);
    }

    private static int getMilliSecondPart()
    {
        return LocalTime.now().getNano() / 1000000;
    }

    private static String getJacksonFormatDateTime()
    {
        return ZonedDateTime.now().format(DATE_TIME_FORMATTER);
    }

    private static List<HierarchyEntry> getNodeHierarchyEntries()
    {
        return getHierarchyEntries(5, "Node");
    }

    private static List<HierarchyEntry> getHierarchyEntries(int depth, String type)
    {
        ArrayList<HierarchyEntry> primaryHierarchy = new ArrayList<>();
        for (int i = 0; i < depth; i++)
        {
            primaryHierarchy.add(new HierarchyEntry(getUUID(), type));
        }
        return primaryHierarchy;
    }

    private static <T> T getRandomEventTypeValue(T[] array)
    {
        int i = RANDOM.nextInt(array.length);
        return array[i];
    }

    public static RepositoryEvent getRandomRawAcsEvent()
    {
        return getRandomEventTypeValue(RawAcsEventInstance.values()).getEvent();
    }

    public static EventV1<? extends ResourceV1> getRandomPublicAcsEvent()
    {
        return getRandomEventTypeValue(PublicAcsEventInstance.values()).getEvent();
    }

    public static String getRandomRawActivitiEvent()
    {
        return getRandomEventTypeValue(RawActivitiEventInstance.values()).getEvent();
    }

    public static List<EventV1<? extends ResourceV1>> getRandomPublicActivitiEvent()
    {
        return getRandomEventTypeValue(PublicActivitiEventInstance.values()).getEvents();
    }

    public static CloudConnectorIntegrationRequest getRandomCloudConnectorEvent()
    {
        return getRandomEventTypeValue(CloudConnectorEventInstance.values()).getEvent();
    }
}
