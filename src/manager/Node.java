package manager;

import model.Task;

public class Node {
    Task task;
    public Node next;
    public Node prev;

    public Node(Task task, Node next, Node prev) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }

    public Node(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
