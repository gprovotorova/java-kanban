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

    private final HistoryManager memoryHistoryManager = Managers.getDefaultHistory();

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
        int savedEpicId = subtask.getEpicId();
        Epic epic = epics.get(savedEpicId);
        if(epic == null){
            return 0;
        }
        int epicId = epic.getId();
        if(epicId == savedEpicId){
            subtask.setId(generateId());
            subtasks.put(subtask.getId(), subtask);
            setEpicStatus(savedEpicId);
        }
        return subtask.getId();
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
        epic.setSubtasks(savedEpic.getSubtasks());
        setEpicStatus(savedEpic.getId());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask savedSubtask = subtasks.get(subtask.getId());
        Epic savedEpic = epics.get(savedSubtask.getEpicId());
        savedSubtask.setName(subtask.getName());
        savedSubtask.setDescription(subtask.getDescription());
        subtask.setStatus(savedSubtask.getStatus());
        setEpicStatus(savedEpic.getId());
    }

    //Удаление по идентификатору
    public void deleteByIdTasks(int id) {
        tasks.remove(id);
        memoryHistoryManager.removeTask(id);
    }

    public void deleteByIdEpic(int id) {
        ArrayList<Subtask> subtasksInEpic = getAllSubtasksOfEpic(epics.get(id));
        for (Subtask subtask : subtasksInEpic) {
            memoryHistoryManager.removeTask(subtask.getId());
        }
        subtasksInEpic.clear();
        epics.remove(id);
        memoryHistoryManager.removeTask(id);

    }

    public void deleteByIdSubtask(int id) {
        subtasks.remove(id);
        memoryHistoryManager.removeTask(id);
    }

    //Статус эпика
    private void setEpicStatus(int id){
        Epic savedEpic = epics.get(id);
        if(savedEpic != null) {
            ArrayList<Subtask> subtaskArrayList = getAllSubtasksOfEpic(savedEpic);
            if(subtaskArrayList.isEmpty()){
                savedEpic.setStatus(Status.NEW);
            } else {
                int countDone = 0;
                int countNew = 0;
                for (Subtask savedSubtask : subtaskArrayList) {
                    if (savedSubtask.getStatus().equals(Status.DONE)) {
                        ++countDone;
                    }
                    if (savedSubtask.getStatus().equals(Status.NEW)) {
                        ++countNew;
                    }
                }
                if (countDone == subtaskArrayList.size()) {
                    savedEpic.setStatus(Status.DONE);
                } else if (countNew == subtaskArrayList.size()) {
                    savedEpic.setStatus(Status.NEW);
                } else {
                    savedEpic.setStatus(Status.IN_PROGRESS);
                }
            }
        }
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
        for (int savedEpicId : epics.keySet()) {
            Epic savedEpic = epics.get(savedEpicId);
            ArrayList<Subtask> savedSubtask = savedEpic.getSubtasks();
            if(!savedSubtask.isEmpty()){
                savedSubtask.clear();
            }
            setEpicStatus(savedEpicId);
        }
        subtasks.clear();
    }

    //Получение списка всех задач
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics(){
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Task> getHistory() {
        return memoryHistoryManager.getAll();
    }

    public ArrayList<Subtask> getAllSubtasksOfEpic(Epic epic) {
        return new ArrayList<>(epic.getSubtasks());
    }
}
