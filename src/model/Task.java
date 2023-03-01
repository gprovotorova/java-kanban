package model;

public class Task {
    protected int ID;
    protected String name;
    protected Status status;
    protected String description;

    public Task(String name, String description, Status taskStatus) {
        status = taskStatus;
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    @Override
    public String toString() {
        return "Task{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
