/*
 * Grakn - A Distributed Semantic Database
 * Copyright (C) 2016  Grakn Labs Limited
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

package ai.grakn.engine.backgroundtasks;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

import static ai.grakn.engine.util.SystemOntologyElements.SCHEDULED_TASK;
import static ai.grakn.engine.util.SystemOntologyElements.STATUS;
import static ai.grakn.engine.util.SystemOntologyElements.STATUS_CHANGE_TIME;
import static ai.grakn.engine.util.SystemOntologyElements.STATUS_CHANGE_BY;
import static ai.grakn.engine.util.SystemOntologyElements.TASK_CLASS_NAME;
import static ai.grakn.engine.util.SystemOntologyElements.CREATED_BY;
import static ai.grakn.engine.util.SystemOntologyElements.EXECUTING_HOSTNAME;
import static ai.grakn.engine.util.SystemOntologyElements.RUN_AT;
import static ai.grakn.engine.util.SystemOntologyElements.RECURRING;
import static ai.grakn.engine.util.SystemOntologyElements.RECUR_INTERVAL;
import static ai.grakn.engine.util.SystemOntologyElements.STACK_TRACE;
import static ai.grakn.engine.util.SystemOntologyElements.TASK_EXCEPTION;
import static ai.grakn.engine.util.SystemOntologyElements.TASK_CHECKPOINT;
import static ai.grakn.engine.util.SystemOntologyElements.TASK_CONFIGURATION;

/**
 * Internal task state model used to keep track of scheduled tasks.
 */
public class TaskState implements Cloneable, Serializable {
    /**
     * Task status, @see TaskStatus.
     */
    private TaskStatus status;
    /**
     * Time when task status was last updated.
     */
    private Date statusChangeTime;
    /**
     * String identifying who last updated task status.
     */
    private String statusChangedBy;
    /**
     * Name of Class implementing the BackgroundTask interface that should be executed when task is run.
     */
    private String taskClassName;
    /**
     * String identifying who created this task.
     */
    private String creator;
    /**
     * String identifying which engine instance is executing this task, set when task is scheduled.
     */
    private String executingHostname;
    /**
     * When this task should be executed.
     */
    private Date runAt;
    /**
     * Should this task be run again after it has finished executing successfully.
     */
    private Boolean recurring;
    /**
     * If a task is marked as recurring, this represents the time delay between the next executing of this task.
     */
    private long interval;
    /**
     * Used to store any executing failures for the given task.
     */
    private String stackTrace;
    private String exception;
    /**
     * Used to store a task checkpoint allowing it to resume from the same point of execution as at the time of the checkpoint.
     */
    private String taskCheckpoint;
    /**
     * Configuration passed to the task on startup, can contain data/location of data for task to process, etc.
     */
    private JSONObject configuration;
    
    public TaskState(String taskClassName) {
        status = TaskStatus.CREATED;
        this.taskClassName = taskClassName;
    }

    public TaskState status(TaskStatus status) {
        this.status = status;
        return this;
    }

    public TaskStatus status() {
        return status;
    }

    public TaskState statusChangeTime(Date statusChangeTime) {
        this.statusChangeTime = statusChangeTime;
        return this;
    }

    public Date statusChangeTime() {
        return statusChangeTime;
    }

    public TaskState statusChangedBy(String statusChangedBy) {
        this.statusChangedBy = statusChangedBy;
        return this;
    }

    public String statusChangedBy() {
        return statusChangedBy;
    }

    public String taskClassName() {
        return this.taskClassName;
    }

    public TaskState creator(String creator) {
        this.creator = creator;
        return this;
    }

    public String creator() {
        return creator;
    }

    public TaskState executingHostname(String hostname) {
        executingHostname = hostname;
        return this;
    }

    public String executingHostname() {
        return executingHostname;
    }

    public TaskState runAt(Date runAt) {
        this.runAt = runAt;
        return this;
    }

    public Date runAt() {
        return runAt;
    }

    public TaskState isRecurring(Boolean recurring) {
        this.recurring = recurring;
        return this;
    }

    public Boolean isRecurring() {
        return recurring != null ? recurring : false;
    }

    public TaskState interval(long interval) {
        this.interval = interval;
        return this;
    }

    public long interval() {
        return interval;
    }

    public TaskState stackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
        return this;
    }

    public String stackTrace() {
        return stackTrace;
    }

    public TaskState exception(String exceptionMessage) {
        this.exception = exceptionMessage;
        return this;
    }

    public String exception() {
        return exception;
    }

    public TaskState checkpoint(String taskCheckpoint) {
        this.taskCheckpoint = taskCheckpoint;
        return this;
    }

    public String checkpoint() {
        return taskCheckpoint;
    }

    public TaskState configuration(JSONObject configuration) {
        this.configuration = configuration;
        return this;
    }

    public JSONObject configuration() {
        return configuration;
    }

    public TaskState clone() throws CloneNotSupportedException {
        TaskState state = (TaskState)super.clone();

        state.status(status)
             .statusChangeTime(statusChangeTime)
             .statusChangedBy(statusChangedBy)
             .creator(creator)
             .executingHostname(executingHostname)
             .runAt(runAt)
             .isRecurring(recurring)
             .interval(interval)
             .stackTrace(stackTrace)
             .exception(exception)
             .checkpoint(taskCheckpoint)
             .configuration(new JSONObject(configuration.toString()));

        return state;
    }

    /**
     * Serialize all the of the state into a json object
     * @return the serialized json object
     */
    public String serialize(){
        JSONObject json = new JSONObject();
        json.put(STATUS, status);
        json.put(STATUS_CHANGE_TIME, statusChangeTime);
        json.put(CREATED_BY, creator);
        json.put(TASK_CLASS_NAME, taskClassName);
        json.put(EXECUTING_HOSTNAME, executingHostname);
        json.put(RUN_AT, runAt != null ? runAt.getTime() : null);
        json.put(RECURRING, recurring);
        json.put(RECUR_INTERVAL, interval);
        json.put(STACK_TRACE, stackTrace);
        json.put(TASK_EXCEPTION, exception);
        json.put(TASK_CHECKPOINT, taskCheckpoint);
        json.put(TASK_CONFIGURATION, configuration);
        return json.toString();
    }

    public static TaskState deserialize(String serialized){
        JSONObject json = new JSONObject(serialized);
        TaskState state = new TaskState(json.getString(TASK_CLASS_NAME));

        state.status(TaskStatus.valueOf(json.getString(STATUS)));
        if(json.has(CREATED_BY)) state.creator(json.getString(CREATED_BY));
        if(json.has(EXECUTING_HOSTNAME)) state.executingHostname(json.getString(EXECUTING_HOSTNAME));
        if(json.has(RUN_AT)) state.runAt(new Date(json.getLong(RUN_AT)));
        if(json.has(RECURRING)) state.isRecurring(json.getBoolean(RECURRING));
        if(json.has(RECUR_INTERVAL)) state.interval(json.getLong(RECUR_INTERVAL));
        if(json.has(STACK_TRACE)) state.stackTrace(json.getString(STACK_TRACE));
        if(json.has(TASK_EXCEPTION)) state.exception(json.getString(TASK_EXCEPTION));
        if(json.has(TASK_CHECKPOINT)) state.checkpoint(json.getString(TASK_CHECKPOINT));
        if(json.has(TASK_CONFIGURATION)) state.configuration(json.getJSONObject(TASK_CONFIGURATION));

        return state;
    }

    @Override
    public String toString() {
        return "TaskState{" +
                "status=" + status +
                ", statusChangeTime=" + statusChangeTime +
                ", statusChangedBy='" + statusChangedBy + '\'' +
                ", taskClassName='" + taskClassName + '\'' +
                ", creator='" + creator + '\'' +
                ", executingHostname='" + executingHostname + '\'' +
                ", runAt=" + runAt +
                ", recurring=" + recurring +
                ", interval=" + interval +
                ", stackTrace='" + stackTrace + '\'' +
                ", exception='" + exception + '\'' +
                ", taskCheckpoint='" + taskCheckpoint + '\'' +
                ", configuration=" + configuration +
                '}';
    }
}
