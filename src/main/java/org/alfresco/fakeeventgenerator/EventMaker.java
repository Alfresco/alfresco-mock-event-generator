/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.fakeeventgenerator;

import static java.lang.System.currentTimeMillis;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.alfresco.event.TestUtil;
import org.alfresco.event.model.NodeResourceV1;
import org.alfresco.event.model.ProcessResourceV1;
import org.alfresco.event.model.ResourceV1;
import org.alfresco.event.model.internal.InternalEvent;
import org.alfresco.events.types.NodeAddedEvent;
import org.alfresco.events.types.RepositoryEvent;
import org.alfresco.events.types.TransactionCommittedEvent;
import org.alfresco.events.types.authority.AuthorityAddedToGroupEvent;
import org.alfresco.events.types.permission.LocalPermissionGrantedEvent;

/**
 * @author Jamal Kaabi-Mofrad
 */
public class EventMaker
{
    private static final Random RANDOM = new Random();
    private static final List<String> USER_LIST = new ArrayList<>();
    private static final List<String> GROUP_LIST = new ArrayList<>();
    private static final List<String> READER_AUTHORITIES_LIST = new ArrayList<>();
    private static final Map<String, Long> PRODUCER_INDICES = new HashMap<>();
    private static final List<String> PERMISSIONS = new ArrayList<>();

    private static final String PRODUCER_BASE = "BaseProducer";
    private static final String PRODUCER_ACS = "ACS";
    private static final String PRODUCER_APS = "APS";

    static
    {
        USER_LIST.add("johndoe");
        USER_LIST.add("sblogg");
        USER_LIST.add("graymond");
        USER_LIST.add("jsixpack");
        USER_LIST.add("jmeatball");

        GROUP_LIST.add("GROUP_A");
        GROUP_LIST.add("GROUP_B");
        GROUP_LIST.add("GROUP_C");
        GROUP_LIST.add("GROUP_D");
        GROUP_LIST.add("GROUP_E");

        READER_AUTHORITIES_LIST.addAll(GROUP_LIST);

        PRODUCER_INDICES.put(PRODUCER_BASE, 0L);
        PRODUCER_INDICES.put(PRODUCER_ACS, 0L);
        PRODUCER_INDICES.put(PRODUCER_APS, 0L);

        PERMISSIONS.add("SiteManager");
        PERMISSIONS.add("SiteCollaborator");
        PERMISSIONS.add("SiteContributor");
        PERMISSIONS.add("SiteConsumer");
    }

    @SuppressWarnings("unchecked")
    public enum InternalEventInstance
    {
        BASE_EVENT()
        {
            @Override
            public InternalEvent<ResourceV1> getEvent()
            {
                return new InternalEvent<>("BASE_EVENT",
                            getUsername(), 
                            new ResourceV1(getUUID(), null),
                            getReaderAuthorities(),
                            null,
                            null,
                            PRODUCER_BASE,
                            getProducerIndex(PRODUCER_BASE),
                            currentTimeMillis());
            }
        },
        CONTENT_CREATED()
        {
            @Override
            public InternalEvent<NodeResourceV1> getEvent()
            {
                return new InternalEvent<>("CONTENT_CREATED",
                            getUsername(),
                            new NodeResourceV1(getUUID(), TestUtil.getTestNodeHierarchy(), "cm:content"),
                            getReaderAuthorities(),
                            null,
                            null,
                            PRODUCER_ACS,
                            getProducerIndex(PRODUCER_ACS),
                            currentTimeMillis());
            }
        },
        PROCESS_STARTED()
        {
            @Override
            public InternalEvent<ProcessResourceV1> getEvent()
            {
                return new InternalEvent<>("PROCESS_STARTED",
                            getUsername(),
                            new ProcessResourceV1(getUUID(), TestUtil.getTestProcessHierarchy()),
                            getReaderAuthorities(),
                            null,
                            null,
                            PRODUCER_APS,
                            getProducerIndex(PRODUCER_APS),
                            currentTimeMillis());
            }
        };

        public abstract <R extends ResourceV1> InternalEvent<R> getEvent();
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
                            getUsername(),
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
                            .authorityName(getUsername())
                            .seqNumber(1)
                            .txnId(getUUID())
                            .networkId("")
                            .timestamp(currentTimeMillis())
                            .username(getUsername())
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
                            .username(getUsername())
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
                            getUsername(),
                            null);
            }
        };

        public abstract RepositoryEvent getEvent();
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

    private static String getUsername()
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

    private static Set<String> getReaderAuthorities()
    {
        HashSet<String> readerAuthorities = new HashSet<>(READER_AUTHORITIES_LIST.size());
        int numAuthorities = RANDOM.nextInt(READER_AUTHORITIES_LIST.size());
        if (numAuthorities == 0)
        {
            numAuthorities = 1;
        }
        for (int i = 0; i < numAuthorities; i++)
        {
            int index = RANDOM.nextInt(READER_AUTHORITIES_LIST.size());
            readerAuthorities.add(READER_AUTHORITIES_LIST.get(index));
        }

        return readerAuthorities;
    }

    public static InternalEvent getRandomInternalEvent()
    {
        int i = RANDOM.nextInt(InternalEventInstance.values().length);
        return InternalEventInstance.values()[i].getEvent();
    }

    public static RepositoryEvent getRandomRawAcsEvent()
    {
        int i = RANDOM.nextInt(RawAcsEventInstance.values().length);
        return RawAcsEventInstance.values()[i].getEvent();
    }

    public static Long getProducerIndex(String producer)
    {
        Long index = PRODUCER_INDICES.get(producer);
        PRODUCER_INDICES.put(producer, index + 1);
        return index;
    }
}
