/*
 * MindmapsDB - A Distributed Semantic Database
 * Copyright (C) 2016  Mindmaps Research Ltd
 *
 * MindmapsDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MindmapsDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MindmapsDB. If not, see <http://www.gnu.org/licenses/gpl.txt>.
 */

package io.mindmaps.graph.internal;

import io.mindmaps.concept.EntityType;
import io.mindmaps.concept.Instance;
import io.mindmaps.concept.RelationType;
import io.mindmaps.concept.Resource;
import io.mindmaps.concept.ResourceType;
import io.mindmaps.concept.RoleType;
import io.mindmaps.factory.MindmapsTestGraphFactory;
import io.mindmaps.util.ErrorMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

public class ResourceTest {

    private AbstractMindmapsGraph mindmapsGraph;

    @org.junit.Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Before
    public void buildGraph() {
        mindmapsGraph = (AbstractMindmapsGraph) MindmapsTestGraphFactory.newEmptyGraph();
        mindmapsGraph.initialiseMetaConcepts();
    }

    @Test
    public void testDataType() throws Exception {
        ResourceType resourceType = mindmapsGraph.putResourceType("resourceType", ResourceType.DataType.STRING);
        Resource resource = mindmapsGraph.putResource("resource", resourceType);
        assertEquals(ResourceType.DataType.STRING, resource.dataType());
    }

    @Test
    public void testOwnerInstances() throws Exception {
        EntityType randomThing = mindmapsGraph.putEntityType("A Thing");
        ResourceType resourceType = mindmapsGraph.putResourceType("A Resource Thing", ResourceType.DataType.STRING);
        RelationType hasResource = mindmapsGraph.putRelationType("Has Resource");
        RoleType resourceRole = mindmapsGraph.putRoleType("Resource Role");
        RoleType actorRole = mindmapsGraph.putRoleType("Actor");
        Instance pacino = mindmapsGraph.putEntity("pacino", randomThing);
        Instance jennifer = mindmapsGraph.putEntity("jennifer", randomThing);
        Instance bob = mindmapsGraph.putEntity("bob", randomThing);
        Instance alice = mindmapsGraph.putEntity("alice", randomThing);
        Resource birthDate = mindmapsGraph.putResource("10/10/10", resourceType);
        hasResource.hasRole(resourceRole).hasRole(actorRole);

        assertEquals(0, birthDate.ownerInstances().size());

        mindmapsGraph.putRelation(UUID.randomUUID().toString(), hasResource).
                putRolePlayer(resourceRole, birthDate).putRolePlayer(actorRole, pacino);
        mindmapsGraph.putRelation(UUID.randomUUID().toString(), hasResource).
                putRolePlayer(resourceRole, birthDate).putRolePlayer(actorRole, jennifer);
        mindmapsGraph.putRelation(UUID.randomUUID().toString(), hasResource).
                putRolePlayer(resourceRole, birthDate).putRolePlayer(actorRole, bob);
        mindmapsGraph.putRelation(UUID.randomUUID().toString(), hasResource).
                putRolePlayer(resourceRole, birthDate).putRolePlayer(actorRole, alice);

        assertEquals(4, birthDate.ownerInstances().size());
        assertTrue(birthDate.ownerInstances().contains(pacino));
        assertTrue(birthDate.ownerInstances().contains(jennifer));
        assertTrue(birthDate.ownerInstances().contains(bob));
        assertTrue(birthDate.ownerInstances().contains(alice));
    }

    @Test
    public void checkResourceDataTypes(){
        ResourceType<String> strings = mindmapsGraph.putResourceType("String Type", ResourceType.DataType.STRING);
        ResourceType<Long> longs = mindmapsGraph.putResourceType("Long Type", ResourceType.DataType.LONG);
        ResourceType<Double> doubles = mindmapsGraph.putResourceType("Double Type", ResourceType.DataType.DOUBLE);
        ResourceType<Boolean> booleans = mindmapsGraph.putResourceType("Boolean Type", ResourceType.DataType.BOOLEAN);

        Resource<String> resource1 = mindmapsGraph.putResource("1", strings);
        Resource<Long> resource2 = mindmapsGraph.putResource(1L, longs);
        Resource<Double> resource3 = mindmapsGraph.putResource(1.0, doubles);
        Resource<Boolean> resource4 = mindmapsGraph.putResource(true, booleans);

        Resource<String> resource5 = mindmapsGraph.putResource("5", strings);
        Resource<Long> resource6 = mindmapsGraph.putResource(1L, longs);
        Resource<Double> resource7 = mindmapsGraph.putResource(1.0, doubles);
        Resource<Boolean> resource8 = mindmapsGraph.putResource(true, booleans);

        assertEquals("1", mindmapsGraph.getResource(resource1.getId()).getValue());
        assertEquals(1L, mindmapsGraph.getResource(resource2.getId()).getValue());
        assertEquals(1.0, mindmapsGraph.getResource(resource3.getId()).getValue());
        assertEquals(true, mindmapsGraph.getResource(resource4.getId()).getValue());

        assertThat(mindmapsGraph.getResource(resource1.getId()).getValue(), instanceOf(String.class));
        assertThat(mindmapsGraph.getResource(resource2.getId()).getValue(), instanceOf(Long.class));
        assertThat(mindmapsGraph.getResource(resource3.getId()).getValue(), instanceOf(Double.class));
        assertThat(mindmapsGraph.getResource(resource4.getId()).getValue(), instanceOf(Boolean.class));

        assertEquals(1, mindmapsGraph.getResourcesByValue("1").size());
        assertEquals(1, mindmapsGraph.getResourcesByValue(1L).size());
        assertEquals(1, mindmapsGraph.getResourcesByValue(1.0).size());
        assertEquals(1, mindmapsGraph.getResourcesByValue(true).size());
    }

    @Test
    public void setInvalidResourceTest (){
        ResourceType longResourceType = mindmapsGraph.putResourceType("long", ResourceType.DataType.LONG);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(allOf(
                containsString(ErrorMessage.INVALID_DATATYPE.getMessage("Invalid Thing", Long.class.getName()))
        ));
        mindmapsGraph.putResource("Invalid Thing", longResourceType);
    }

    @Test
    public void datatypeTest2(){
        ResourceType<Double> doubleResourceType = mindmapsGraph.putResourceType("doubleType", ResourceType.DataType.DOUBLE);
        Resource thing = mindmapsGraph.putResource(2.0, doubleResourceType);
        assertEquals(2.0, thing.getValue());
    }

    @Test
    public void testToString() {
        ResourceType<String> concept = mindmapsGraph.putResourceType("a", ResourceType.DataType.STRING);
        Resource<String> concept2 = mindmapsGraph.putResource("concept2", concept);
        assertTrue(concept2.toString().contains("Value"));
    }
}