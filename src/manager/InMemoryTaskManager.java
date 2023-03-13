package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    private final HashMap <Integer, Task> tasks = new HashMap<>();
    private final HashMap <Integer, Epic> epics = new HashMap<>();
    private final HashMap <Integer, Subtask> subtasks = new HashMap<>();

    private static HistoryManager memoryHistoryManager;
    //= Managers.getDefaultHistory();

    public InMemoryTaskManager (HistoryManager memoryHistoryManager) {
        this.memoryHistoryManager = memoryHistoryManager;
    }

    //Генератор ID
    private int generateId(){
        return ++id;
    }

    //Создание задачи
    @Override
    public int addNewTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    //Создание эпика
    @Override
    public int addNewEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    //Создание подзадачи
    @Override
    public int addNewSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        for (int id : epics.keySet()) {
            if(id == epicId){
                subtask.setId(generateId());
                subtasks.put(subtask.getId(), subtask);
                return subtask.getId();
            }
        }
        return 0;
    }

    //Получение по идентификатору
    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if(task == null) {
            return null;
        }
        memoryHistoryManager.addTask(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if(epic == null) {
            return null;
        }
        memoryHistoryManager.addTask(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id){
        Subtask subtask = subtasks.get(id);
        if(subtask == null) {
            return null;
        }
        memoryHistoryManager.addTask(subtask);
        return subtask;
    }

    //Обновление
    @Override
    public void updateTask(Task task) {
        Task savedTask = tasks.get(task.getId());
        savedTask.setName(task.getName());
        savedTask.setStatus(task.getStatus());
        savedTask.setDescription(task.getDescription());
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
        epic.setStatus(savedEpic.getStatus());
        epic.setSubtasks(savedEpic.getSubtasks());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask savedSubtask = subtasks.get(subtask.getId());
        Epic savedEpic = epics.get(savedSubtask.getEpicId());
        setEpicStatus(savedEpic.getId());
        savedSubtask.setName(subtask.getName());
        savedSubtask.setDescription(subtask.getDescription());
        subtask.setStatus(savedSubtask.getStatus());
    }

    //Удаление по идентификатору
    @Override
    public void deleteByIDTasks(int id) {
        tasks.remove(id);
        System.out.println("Delete id task " + id );
    }

    @Override
    public void deleteByIDEpic(int id) {
        epics.remove(id);
        System.out.println("Delete id epic " + id );
    }

    @Override
    public void deleteByIDSubtask(int id) {
        subtasks.remove(id);
        System.out.println("Delete id subtask " + id );
    }

    //Статус эпика
    public Status setEpicStatus(int id){
        Epic savedEpic = epics.getOrDefault(id, null);
        if(savedEpic != null) {
            ArrayList<Subtask> subtaskArrayList = savedEpic.getSubtasks();
            if(subtaskArrayList.isEmpty()){
                savedEpic.setStatus(Status.NEW);
            }
            for (int i = 0; i < subtaskArrayList.size(); i++) {
                Subtask savedSubtask = subtaskArrayList.get(i);
                if(savedEpic.getStatus() == null){
                    if (savedSubtask.getStatus().equals(Status.NEW)){
                        savedEpic.setStatus(Status.NEW);
                    } else if(savedSubtask.getStatus().equals(Status.DONE)) {
                        savedEpic.setStatus(Status.DONE);
                    } else {
                        savedEpic.setStatus(Status.InProgress);
                    }
                } else if (savedSubtask.getStatus().equals(Status.NEW) && savedEpic.getStatus().equals(Status.NEW)){
                    savedEpic.setStatus(Status.NEW);
                } else if(savedSubtask.getStatus().equals(Status.DONE) && savedEpic.getStatus().equals(Status.DONE)) {
                    savedEpic.setStatus(Status.DONE);
                } else {
                    savedEpic.setStatus(Status.InProgress);
                }

            }
        }
        return savedEpic.getStatus();
    }

    //Удаление всех задач
    @Override
    public void deleteAllTasks(){
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtasks();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    //Получение списка всех задач
    @Override
    public ArrayList<String> getAllTasks() {
        ArrayList<String> nameOfTasks = new ArrayList<>();
        for (int id : tasks.keySet()) {
            Task savedTask = tasks.get(id);
            nameOfTasks.add(savedTask.getName());
        }
        return nameOfTasks;
    }

    @Override
    public ArrayList<String> getAllEpics(){
        ArrayList<String> nameOfEpics = new ArrayList<>();
        for (int id : epics.keySet()) {
            Epic savedEpic = epics.get(id);
            nameOfEpics.add(savedEpic.getName());
        }
        return nameOfEpics;
    }

    @Override
    public ArrayList<String> getAllSubtasks() {
        ArrayList<String> savedListOfSubtasks = new ArrayList<>();
        for (int id : subtasks.keySet()) {
            String savedSubtask = subtasks.get(id).getName();
            savedListOfSubtasks.add(savedSubtask);
        }
        return savedListOfSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> taskViewHistory = memoryHistoryManager.getAll();
        return taskViewHistory;
    }

    public ArrayList<Subtask> getAllSubtasksOfEpic(Epic epic) {
        int epicID = epic.getId();
        ArrayList<Subtask> nameOfSubtasks = new ArrayList<>();
        for (int id : epics.keySet()) {
            if (id == epicID){
                Epic savedEpic = epics.get(id);
                nameOfSubtasks = savedEpic.getSubtasks();
            }
        }
        return nameOfSubtasks;
    }
}
