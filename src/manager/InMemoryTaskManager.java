package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Instant;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private int id = 0;
    public final HashMap <Integer, Task> tasks = new HashMap<>();
    public final HashMap <Integer, Epic> epics = new HashMap<>();
    public final HashMap <Integer, Subtask> subtasks = new HashMap<>();

    public Map<Instant, Task> prioritizedTasks = new TreeMap();

    public InMemoryHistoryManager memoryHistoryManager = Managers.getDefaultHistory();

    //Генератор ID
    private int generateId(){
        HashMap <Integer, Task> savedTasks = mergeAllTasks();
        int maxId = 0;
        if(!savedTasks.isEmpty()){
            for (Integer key : savedTasks.keySet()) {
                if(savedTasks.get(key).getId() > maxId){
                    maxId = savedTasks.get(key).getId();
                }
            }
            id = maxId + 1;
        } else {
            ++id;
        }
        return id;
    }

    public void changeId(int oldId, int newId, Task task){
        TaskType type = task.getType();
        switch(type){
            case TASK:
                tasks.put(newId, task);
                tasks.remove(oldId, task);
                break;
            case SUBTASK:
                subtasks.put(newId, (Subtask) task);
                subtasks.remove(oldId, (Subtask) task);
                break;
            case EPIC:
                epics.put(newId, (Epic)task);
                epics.remove(oldId, (Epic)task);
                break;
        }

    }

    //Создание задачи
    @Override
    public int addNewTask(Task task) {
        task.setId(generateId());
        intersectionCheck(task);
        tasks.put(task.getId(), task);
        memoryHistoryManager.addTask(task);
        return task.getId();
    }

    //Создание эпика
    @Override
    public int addNewEpic(Epic epic) {
        epic.setId(generateId());
        epic.countEpicTime();
        epics.put(epic.getId(), epic);
        memoryHistoryManager.addTask(epic);
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
            intersectionCheck(subtask);
            subtasks.put(subtask.getId(), subtask);
            memoryHistoryManager.addTask(subtask);
            setEpicStatus(savedEpicId);
            epic.countEpicTime();
        }
        return subtask.getId();
    }

    //Получение по идентификатору
    @Override
    public Task getTask(int id) {
        Task task = null;
        for (Integer savedId : tasks.keySet()) {
            if(tasks.get(savedId).getId() == id){
                task = tasks.get(savedId);
            }
        }
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
        intersectionCheck(task);
        savedTask.setName(task.getName());
        savedTask.setStatus(task.getStatus());
        savedTask.setDescription(task.getDescription());
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        intersectionCheck(epic);
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
        epic.setSubtasks(savedEpic.getSubtasks());
        setEpicStatus(savedEpic.getId());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask savedSubtask = subtasks.get(subtask.getId());
        Epic savedEpic = epics.get(savedSubtask.getEpicId());
        if(!subtask.getStartTime().equals(savedSubtask.getStartTime())){
            intersectionCheck(subtask);
        }
        if(!subtask.getDuration().equals(savedSubtask.getDuration())){
            intersectionCheck(subtask);
        }
        if(!subtask.getEndTime().equals(savedSubtask.getEndTime())){
            intersectionCheck(subtask);
        }
        savedSubtask.setName(subtask.getName());
        savedSubtask.setDescription(subtask.getDescription());
        subtask.setStatus(savedSubtask.getStatus());
        setEpicStatus(savedEpic.getId());
    }

    //Удаление по идентификатору
    @Override
    public void deleteByIdTask(int id) {
        tasks.remove(id);
        memoryHistoryManager.removeTask(id);
    }

    @Override
    public void deleteByIdEpic(int id) {
        ArrayList<Subtask> subtasksInEpic = getAllSubtasksOfEpic(epics.get(id));
        for (Subtask subtask : subtasksInEpic) {
            memoryHistoryManager.removeTask(subtask.getId());
        }
        for (Subtask subtask : subtasksInEpic) {
            subtasks.remove(subtask.getId(), subtask);
        }
        subtasksInEpic.clear();
        epics.remove(id);
        memoryHistoryManager.removeTask(id);
    }

    @Override
    public void deleteByIdSubtask(int id) {
        Epic epic = epics.get(getSubtask(id).getEpicId());
        subtasks.remove(id);
        epic.countEpicTime();
        intersectionCheck(epic);
        memoryHistoryManager.removeTask(id);
    }

    @Override
    //Статус эпика
    public void setEpicStatus(int id){
        Epic savedEpic = epics.get(id);
        if(savedEpic != null) {
            ArrayList<Subtask> subtaskArrayList = getAllSubtasksOfEpic(savedEpic);
            if(subtaskArrayList == null || subtaskArrayList.isEmpty()){
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

    @Override
    public ArrayList<Subtask> getAllSubtasksOfEpic(Epic epic) {
        return new ArrayList<>(epic.getSubtasks());
    }

    public HashMap <Integer, Task> mergeAllTasks() {
        HashMap <Integer, Task> allObjects = new HashMap<>();
        for(int idTask : tasks.keySet()){
            allObjects.put(idTask, tasks.get(idTask));
        }
        for(int idSubtask : subtasks.keySet()){
            allObjects.put(idSubtask, subtasks.get(idSubtask));
        }
        for(int idEpic : epics.keySet()){
            allObjects.put(idEpic, epics.get(idEpic));
        }
        return allObjects;
    }

    public Map<Instant, Task> getPrioritizedTasks(){
        HashMap <Integer, Task> allObjects = mergeAllTasks();
        for (Integer id : allObjects.keySet()) {
            Instant startTime = allObjects.get(id).getStartTime();
            prioritizedTasks.put(startTime, allObjects.get(id));
        }
        return prioritizedTasks;
    }

    public void intersectionCheck(Task task) {
        HashMap<Integer, Task> allObjects = mergeAllTasks();
        Instant taskStartTime = task.getStartTime();
        Instant taskEndTime = task.getEndTime();
        if(allObjects.size() > 0){
            for (Integer id : allObjects.keySet()) {
                Instant start = allObjects.get(id).getStartTime();
                Instant end = allObjects.get(id).getEndTime();
                if (taskStartTime.isAfter(start) && taskEndTime.isBefore(end)) {
                    throw new RuntimeException("Проверьте время выполнения задачи. Оно не может пересекаться с другими задачами.");
                } else if (taskEndTime.isAfter(start) && taskEndTime.isBefore(end)) {
                    throw new RuntimeException("Проверьте время выполнения задачи. Оно не может пересекаться с другими задачами.");
                } else if (taskStartTime.equals(start) && taskEndTime.equals(end)) {
                    if(!task.equals(allObjects.get(id))){
                        throw new RuntimeException("Проверьте время выполнения задачи. Оно не может совпадать с другими задачами.");
                    }
                }
            }
        }
    }
}
