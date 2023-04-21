package model;

import manager.TaskType;

import java.util.Objects;

public class Subtask extends Task {
    protected int epicId;

    public Subtask(String name, String description, int epicId, Status subtaskStatus) {
        super(name, description, subtaskStatus);
        this.epicId = epicId;
    }

    public TaskType getType(){
        return TaskType.SUBTASK;
    }

    public int getEpicId(){
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
