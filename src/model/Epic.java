package model;

import java.util.ArrayList;


public class Epic extends Task {
    protected ArrayList<Subtask> subtasks;


    public Epic(String name, String description, ArrayList<Subtask> subtasks) {
        super(name, description);
        this.subtasks = subtasks;
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "ID=" + getId() +
                ", name='" + getName() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", description='" + getDescription() + '\'' +
                "subtasks=" + subtasks +
                '}';
    }
}
