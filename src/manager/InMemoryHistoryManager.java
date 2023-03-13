package manager;

import model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private LinkedList viewHistory = new LinkedList<>();

    public InMemoryHistoryManager() {
        this.viewHistory = viewHistory;
    }

    @Override
    public void addTask(Task task) {
        if (viewHistory.size() > 9){
            viewHistory.removeFirst();
            viewHistory.add(task);
        } else {
            viewHistory.add(task);
        }
    }

    @Override
    public List<Task> getAll() {
        return viewHistory;
    }
}
