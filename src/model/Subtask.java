package model;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String name, String description, Epic epic, Status subtaskStatus) {
        super(name, description, subtaskStatus);
        this.epic = epic;
    }

    public Epic getEpic(){
        return epic;
    }

}
