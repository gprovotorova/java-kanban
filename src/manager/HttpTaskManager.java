package manager;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import constans.Constans;
import model.Epic;
import model.Subtask;
import model.Task;
import server.KVTaskClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpTaskManager extends InMemoryTaskManager{
    public KVTaskClient client;
    private Gson gson = Managers.getGson();
    private String url;
    public HttpTaskManager(String url) {
        this.client = new KVTaskClient(url);
        this.url = url;
    }

    @Override
    public int addNewTask(Task task) {
        int taskId = super.addNewTask(task);
        save();
        return taskId;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int epicId = super.addNewEpic(epic);
        save();
        return epicId;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int subtaskId = super.addNewSubtask(subtask);
        save();
        return subtaskId;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteByIdTask(int id) {
        super.deleteByIdTask(id);
        save();
    }

    @Override
    public void deleteByIdEpic(int id) {
        super.deleteByIdEpic(id);
        save();
    }

    @Override
    public void deleteByIdSubtask(int id) {
        super.deleteByIdSubtask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public List<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public List<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return super.getAllSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = super.getHistory();
        save();
        return history;
    }

    @Override
    public List<Subtask> getAllSubtasksOfEpic(Epic epic) {
        List<Subtask> subtasksOfEpic = super.getAllSubtasksOfEpic(epic);
        save();
        return subtasksOfEpic;
    }

    @Override
    public void setEpicStatus(int epicId) {
        super.setEpicStatus(epicId);
        save();
    }

    public void reloadHistory(List<Integer> savedId, HttpTaskManager manager){
        List <Task> savedTasks = new ArrayList<>();
        for (int i = 0; i < savedId.size(); i++){
            int id = savedId.get(i);
            Task task = manager.getObject(id);
            savedTasks.add(task);
        }
        manager.memoryHistoryManager.cleanViewHistory();
        for (Task savedTask : savedTasks) {
            manager.memoryHistoryManager.addTask(savedTask);
        }
    }
    public List<Integer> historyFromString(String jsonHistory){
        JsonElement jsonElement = JsonParser.parseString(jsonHistory);
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        String line = "";
        for (JsonElement element : jsonArray) {
            line += element.getAsString() + Constans.COMMA;
        }
        String [] array = line.split(Constans.COMMA);
        List<Integer> history = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            history.add(Integer.parseInt(array[i]));
        }
        return history;
    }
    public HttpTaskManager load(){
        HttpTaskManager httpTaskManager = new HttpTaskManager(url);
        String jsonTasks = client.load("task");
        httpTaskManager.tasks = gson.fromJson(jsonTasks,
                new TypeToken<Map<Integer, Task>>(){
                }.getType());
        String jsonEpics = client.load("epic");
        httpTaskManager.epics = gson.fromJson(jsonEpics,
                new TypeToken<Map<Integer, Epic>>(){
                }.getType());
        String jsonSubtasks = client.load("subtask");
        httpTaskManager.subtasks = gson.fromJson(jsonSubtasks,
                new TypeToken<Map<Integer, Subtask>>(){
                }.getType());
        String jsonHistory = client.load("history");
        List <Integer> loadHistory = historyFromString(jsonHistory);
        reloadHistory(loadHistory, httpTaskManager);
        return httpTaskManager;
    }
    
    public void save() {
        client.save("task", gson.toJson(tasks));
        client.save("subtask", gson.toJson(subtasks));
        client.save("epic", gson.toJson(epics));
        client.save("history", gson.toJson(memoryHistoryManager.getAll().stream()
                .map(Task::getId)
                .collect(Collectors.toList())));
    }

}
