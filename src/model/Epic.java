package model;

import manager.TaskType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Epic extends Task {
    protected List<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = getSubtasks();
        this.startTime = getStartTime();
        this.duration = getDuration();
        this.endTime = getEndTime();
    }

    @Override
    public Instant getStartTime() {
        return startTime;
    }

    @Override
    public Long getDuration() {
        return duration;
    }

    @Override
    public Instant getEndTime() {
        return endTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public void countEpicTime(){
        List<Subtask> subtasksOfEpic = getSubtasks();
        Long durationAllSubtasks = 0L;
        if(subtasksOfEpic == null) {
            setStartTime(Instant.MIN);
            setDuration(0L);
            setEndTime(getStartTime().plus(10, ChronoUnit.MINUTES));
            return;
        }
        if(subtasksOfEpic.size() == 1) {
            setStartTime(subtasksOfEpic.get(0).getStartTime());
            setDuration(subtasksOfEpic.get(0).getDuration());
            setEndTime(subtasksOfEpic.get(0).getEndTime());
        }
        if(subtasksOfEpic != null){
            for (int i = 0; i < subtasksOfEpic.size()-1; i++) {
                Subtask subtask1 = subtasksOfEpic.get(i);
                Subtask subtask2 = subtasksOfEpic.get(i+1);
                Instant timeOfSubtask1 = subtask1.getStartTime();
                Instant timeOfSubtask2 = subtask2.getStartTime();
                if(timeOfSubtask1.isBefore(timeOfSubtask2)){
                    setStartTime(timeOfSubtask1);
                }
                if (timeOfSubtask2.isAfter(timeOfSubtask1)) {
                    setEndTime(timeOfSubtask2);
                }
            }
            for (Subtask subtask : subtasksOfEpic) {
                durationAllSubtasks += subtask.getDuration();
                setDuration(durationAllSubtasks);
            }
        }
    }

    public TaskType getType(){
        return TaskType.EPIC;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + endTime +
                ", subtasks=" + subtasks +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }
}
