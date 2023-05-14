package model;

import manager.TaskType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Task {
    protected int id;
    protected String name;
    protected Status status;
    protected String description;
    protected Instant startTime;
    protected Long duration;
    protected Instant endTime;

    public Task(String name, String description, Status taskStatus, Long startTimeIncome, Long duration) {
        status = taskStatus;
        this.name = name;
        this.description = description;
        this.startTime = Instant.ofEpochSecond(startTimeIncome);
        this.duration = duration;
        this.endTime = getEndTime();
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        status = Status.NEW;
    }

    public TaskType getType(){
        return TaskType.TASK;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status taskStatus) {
        status = taskStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Long getDuration() {
        return duration;
    }

    public Instant getEndTime() {
        return startTime.plus(duration, ChronoUnit.MILLIS);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && status == task.status && Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, description);
    }
}
