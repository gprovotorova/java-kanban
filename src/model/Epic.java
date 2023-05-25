package model;

import manager.TaskType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Subtask> subtasks;

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
        if(subtasksOfEpic == null || subtasksOfEpic.isEmpty()) {
            setStartTime(Instant.MIN);
            setDuration(0L);
            setEndTime(getStartTime().plus(10, ChronoUnit.MINUTES));
        } else {
            if(subtasksOfEpic.size() == 1) {
                setStartTime(subtasksOfEpic.get(0).getStartTime());
                setDuration(subtasksOfEpic.get(0).getDuration());
                setEndTime(subtasksOfEpic.get(0).getEndTime());
            }
            for (int i = 0; i < subtasksOfEpic.size()-1; i++) {
                Instant firstSubtaskStartTime = subtasksOfEpic.get(i).getStartTime();
                Instant secondSubtaskStartTime = subtasksOfEpic.get(i+1).getStartTime();
                if(firstSubtaskStartTime.isBefore(secondSubtaskStartTime)){
                    setStartTime(firstSubtaskStartTime);
                }
                Instant firstSubtaskEndTime = subtasksOfEpic.get(i).getEndTime();
                Instant secondSubtaskEndTime = subtasksOfEpic.get(i+1).getEndTime();
                if (firstSubtaskEndTime.isAfter(secondSubtaskEndTime)) {
                    setEndTime(firstSubtaskEndTime);
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
