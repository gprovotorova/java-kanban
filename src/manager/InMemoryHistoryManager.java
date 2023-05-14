package manager;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private HashMap <Integer, Node> viewHistory = new HashMap<>();

    /*public InMemoryHistoryManager() {
        this.viewHistory = viewHistory;
    }
    */

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
            node = node.getNext();
        }
        return addedTasks;
    }

    private void linkLast(Task task){
        final Node last = tail;
        final Node newNode = new Node(last, task, null);
        tail = newNode;
        if(last == null){
            head = newNode;
        } else {
            last.setNext(newNode);
        }
        viewHistory.put(task.getId(), tail);
    }

    private void removeNode(Node node) {
        if(node == null){
            return;
        }
        if(node.getPrev() != null){
            node.getPrev().setNext(node.getNext());
        } else {
            head = node.getNext();
        }
        if(node.getNext() != null){
            node.getNext().setPrev(node.getPrev());
        }
        viewHistory.replace(node.getTask().getId(), node, tail);
    }

    public void cleanViewHistory(){
        for (Integer i : viewHistory.keySet()) {
            Node savedNode = viewHistory.get(i);
            removeNode(savedNode);
        }
        viewHistory.clear();
        head = null;
        tail = null;
    }
}
