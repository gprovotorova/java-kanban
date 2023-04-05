package manager;

import model.Task;

public class Node {
    Task task;
    public Node next;
    public Node prev;

    public Node(Node prev, Task task, Node next) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }

    public Task getTask() {
        return task;
    }
}
