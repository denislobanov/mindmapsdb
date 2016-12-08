/*
 * Grakn - A Distributed Semantic Database
 * Copyright (C) 2016  Grakn Labs Ltd
 *
 * Grakn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Grakn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Grakn. If not, see <http://www.gnu.org/licenses/gpl.txt>.
 */

package ai.grakn.test.engine.backgroundtasks;

import ai.grakn.engine.backgroundtasks.TaskStatus;
import ai.grakn.engine.backgroundtasks.distributed.ClusterManager;
import ai.grakn.engine.backgroundtasks.distributed.DistributedTaskManager;
import ai.grakn.engine.backgroundtasks.distributed.Scheduler;
import ai.grakn.engine.backgroundtasks.distributed.TaskRunner;
import ai.grakn.engine.backgroundtasks.taskstorage.GraknStateStorage;
import ai.grakn.test.AbstractEngineTest;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import static ai.grakn.engine.backgroundtasks.TaskStatus.COMPLETED;
import static ai.grakn.engine.backgroundtasks.TaskStatus.FAILED;
import static java.util.Collections.singletonMap;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assume.assumeFalse;

public class TaskManagerTest extends AbstractEngineTest {
    private DistributedTaskManager manager;

    @BeforeClass
    public static void resetLogs() {
        ((Logger) org.slf4j.LoggerFactory.getLogger(GraknStateStorage.class)).setLevel(Level.DEBUG);
        ((Logger) org.slf4j.LoggerFactory.getLogger(Scheduler.class)).setLevel(Level.DEBUG);
        ((Logger) org.slf4j.LoggerFactory.getLogger(TaskRunner.class)).setLevel(Level.DEBUG);
        ((Logger) org.slf4j.LoggerFactory.getLogger(ClusterManager.class)).setLevel(Level.DEBUG);
    }

    @Before
    public void setup() throws Exception {
        assumeFalse(usingTinker());
        manager = new DistributedTaskManager();
        Thread.sleep(5000);
    }

    private void waitToFinish(String id) {
        while (true) {
            TaskStatus status = manager.getState(id);
            if (status == COMPLETED || status == FAILED) {
                System.out.println(id + " ------> " + status);
                break;
            }

            System.out.println("Checking " + id);

            try {
                Thread.sleep(5000);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    /**
     * Run end to end test and assert that the state
     * is correct in zookeeper.
     */
    public void endToEndTest(){
        Collection<String> ids = new HashSet<>();

        for(int i = 0; i < 20; i++) {
            String taskId = manager.scheduleTask(new TestTask(), TaskManagerTest.class.getName(),
                    new Date(), 0, new JSONObject(singletonMap("name", "task"+i)));

            ids.add(taskId);
        }

        ids.forEach(this::waitToFinish);
        ids.stream().map(manager::getState).allMatch(s -> s == COMPLETED);
        assertEquals(20, TestTask.startedCounter.get());
    }
}