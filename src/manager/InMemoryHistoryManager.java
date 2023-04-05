package manager;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public Node head;
    public Node tail;
    private HashMap <Integer, Node> viewHistory = new HashMap<>();
    public InMemoryHistoryManager() {
        this.viewHistory = viewHistory;
    }

    @Override
    public void addTask(Task task) {
        Node node = viewHistory.get(task.getId());
        linkLast(task);
        removeNode(node);
    }

    @Override
    public void removeTask(int id){
        Node node = viewHistory.remove(id);
        removeNode(node);
    }

    @Override
    public List<Task> getAll() {
        List<Task> addedTasks = new ArrayList<>();
        Node node = head;
        while (node != null){
            Task task = node.getTask();
            addedTasks.add(task);
            node = node.next;
        }
        return addedTasks;
    }

    public void linkLast(Task task){
        final Node last = tail;
        final Node newNode = new Node(last, task, null);
        tail = newNode;
        if(last == null){
            head = newNode;
        } else {
            last.next = newNode;
        }
        viewHistory.put(task.getId(), tail);
    }

    public void removeNode(Node node) {
        if(node == null){
            return;
        }
        if(node.prev != null){
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if(node.next != null){
            node.next.prev = node.prev;
        }
        viewHistory.replace(node.getTask().getId(), node, tail);
    }

}
