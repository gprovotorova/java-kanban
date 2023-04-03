package manager;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private CustomLinkedList<Task> viewHistory = new CustomLinkedList<>();

    public InMemoryHistoryManager() {
        this.viewHistory = viewHistory;
    }

    @Override
    public void addTask(Task task) {
        viewHistory.linkLast(task);
    }

    @Override
    public void removeTask(int id){
        viewHistory.remove(id);
    }

    @Override
    public List<Task> getAll() {
        return viewHistory.getTasks();
    }

    public class CustomLinkedList<T> {
        public Node head = null;
        public Node tail = null;
        protected int id = 0;

        public int setId(int id) {
            return ++id;
        }

        private LinkedList<Task> viewHistoryForWork = new LinkedList<>();
        HashMap<Integer, Node> reviewedTask = new HashMap<>();

        public void linkLast(Task task){
            if (viewHistoryForWork.contains(task)){
                int index = viewHistoryForWork.indexOf(task);
                Task taskForWork = viewHistoryForWork.get(index);
                var node = new Node(taskForWork, tail, head);
                reviewedTask.put(setId(id), node);
                removeNode(node);
                remove(index);
                viewHistoryForWork.addLast(task);
            } else {
                viewHistoryForWork.addLast(task);
            }
        }


        public ArrayList<Task> getTasks() {
            ArrayList<Task> addedTasks = new ArrayList<>();
            for (int i = 0; i < viewHistoryForWork.size(); i++) {
                addedTasks.add(i, viewHistoryForWork.get(i));
            }
            return addedTasks;
        }

        public void removeNode(Node node) {
            reviewedTask.remove(node);
            Node temp = head, prev = null;
            Task taskForSearch = node.getTask();
            if (temp != null && temp.task == taskForSearch) {
                head = temp.next;
                return;
            }
            while (temp != null && temp.task != taskForSearch) {
                prev = temp;
                temp = temp.next;
            }
            if (temp == null){
                return;
            }
            prev.next = temp.next;
        }

        public void remove(int id) {
            viewHistoryForWork.remove(id);
        }
    }
}
