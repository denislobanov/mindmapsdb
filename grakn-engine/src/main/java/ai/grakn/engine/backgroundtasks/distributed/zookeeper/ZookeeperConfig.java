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

package ai.grakn.engine.backgroundtasks.distributed.zookeeper;

public class ZookeeperConfig {

    public static final String ZOOKEEPER_URL = "127.0.0.1";
    public static final String SCHEDULER_PATH = "/scheduler";

    public static final String TASK_PATH = "/task/%s";
    public static final String STATE_PATH = "/task/%s/state";
    public static final String EXECUTOR_PATH = "/task/%s/executor";

}