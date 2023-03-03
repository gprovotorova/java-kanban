package model;

public class Subtask extends Task {
    protected int epicId;

    public Subtask(String name, String description, int epicId, Status subtaskStatus) {
        super(name, description, subtaskStatus);
        this.epicId = epicId;
    }

    public int getEpicId(){
        return epicId;
    }

}
