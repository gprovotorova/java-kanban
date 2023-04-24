package manager;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import utils.CSVutils;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class FileBackedTasksManager extends InMemoryTaskManager{

    private static File file;
    public String LINE = "";
    private static final String COMMA = ", ";

    public FileBackedTasksManager(File file) {
        this.file = file;
    }
    
    private static InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    
    private static CSVutils CSVutil = new CSVutils(file, historyManager);

    public static void main(String[] args) {


        FileBackedTasksManager manager = new FileBackedTasksManager(new File("history.csv"));
        CSVutils CSVutil = new CSVutils(file, manager, historyManager);

        manager.addNewTask(new Task("TASK 1", "...", Status.NEW));
        manager.addNewTask(new Task("TASK 2", "...", Status.NEW));

        //Создание эпика 1
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        int epicId = manager.addNewEpic(new Epic("EPIC 1", "..."));
        manager.getEpic(epicId).setSubtasks(listOfSubtasks);
        int subtaskId = manager.addNewSubtask(new Subtask("SUBTASK 1", "...", epicId, Status.IN_PROGRESS));
        listOfSubtasks.add(manager.getSubtask(subtaskId));
        subtaskId = manager.addNewSubtask(new Subtask("SUBTASK 2", "...", epicId, Status.NEW));
        listOfSubtasks.add(manager.getSubtask(subtaskId));
        subtaskId = manager.addNewSubtask(new Subtask("SUBTASK 3", "...", epicId, Status.DONE));
        listOfSubtasks.add(manager.getSubtask(subtaskId));
        manager.getEpic(epicId).setSubtasks(listOfSubtasks);

        //Создание эпика 2
        listOfSubtasks = new ArrayList<>();
        epicId = manager.addNewEpic(new Epic("EPIC 2", "..."));
        manager.getEpic(epicId).setSubtasks(listOfSubtasks);

        //Вызов задач, подзадач и эпика
        manager.getTask(1);

        manager.getEpic(3);

        manager.getTask(2);

        manager.getSubtask(4);

        manager.getTask(1);

        manager.getEpic(3);

        manager.getSubtask(6);


        FileBackedTasksManager manager2 = CSVutil.loadFromFile(new File("history.csv"));


        manager2.getEpic(3);
        manager2.getTask(1);
        manager2.getSubtask(6);
        manager2.getSubtask(4);

        manager2.CSVutil.save();

         




    }

    public String toString(Task task){
        if(task.getType().equals(TaskType.SUBTASK)){
            int id = task.getId();
            LINE = String.format("%s, %s, %s, %s, %s, %s", task.getId(), task.getType(), task.getName(), task.getStatus(),
                    task.getDescription(), super.getSubtask(id).getEpicId());
        } else {
            LINE = String.format("%s, %s, %s, %s, %s", task.getId(), task.getType(), task.getName(), task.getStatus(),
                    task.getDescription());
        }
        return LINE;
    }

    public Object fromString(String line){
        String [] array = line.split(COMMA);
        String taskType = array[1];
        switch(taskType){
            case "TASK":
                int taskId = super.addNewTask(new Task(array[2], array[4], Status.valueOf(array[3])));
                int newId = Integer.parseInt(array[0]);
                Task task = super.getTask(taskId);
                if(taskId != newId){
                    task.setId(newId);
                    changeId(taskId, newId, task);
                }
                return task;

            case "SUBTASK":
                int subtaskId = super.addNewSubtask(new Subtask(array[2], array[4], Integer.parseInt(array[5]), Status.valueOf(array[3])));
                int epicId = Integer.parseInt(array[5]);
                ArrayList<Subtask> listOfSubtasks = getSubtasks(epicId);
                getEpic(epicId).setSubtasks(listOfSubtasks);
                newId = Integer.parseInt(array[0]);
                Subtask subtask = super.getSubtask(subtaskId);
                if(subtaskId != newId){
                    subtask.setId(newId);
                    changeId(subtaskId, newId, subtask);
                }
                return subtask;
            case "EPIC":
                epicId = super.addNewEpic(new Epic(array[2], array[4]));
                newId = Integer.parseInt(array[0]);
                Epic epic = super.getEpic(epicId);
                if(epicId != newId){
                    epic.setId(newId);
                    changeId(epicId, newId, epic);
                }
                return epic;
        }
        Optional<Task> empty = Optional.empty();
        return empty;
    }

    public static String historyToString(HistoryManager manager) {
        List<Task> addedTasks = manager.getAll();
        String history = String.valueOf(addedTasks.get(0).getId());
        for (int i = 1; i < addedTasks.size(); i++) {
            history = history + ", " + addedTasks.get(i).getId();
        }
        return history;
    }

    public static List<Integer> historyFromString(String line){
        String [] array = line.split(", ");
        List<Integer> history = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            history.add(Integer.parseInt(array[i]));
        }

        return history;
    }

    public void reloadHistory(List<Integer> savedId){
        List <Task> savedTasks = new ArrayList<>();
        for (int i = 0; i < savedId.size(); i++){
            int id = savedId.get(i);
            Task task = getObject(id);
            savedTasks.add(task);
        }
        historyManager.cleanViewHistory();
        for (Task savedTask : savedTasks) {
            historyManager.addTask(savedTask);
        }
    }




    private ArrayList<Subtask> getSubtasks(int epicId){
        ArrayList<Subtask> subtasks = super.getAllSubtasks();
        ArrayList <Subtask> epicSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks) {
            if(subtask.getEpicId() == epicId){
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }



    @Override
    public int addNewTask(Task task){
        super.addNewTask(task);
        historyManager.addTask(task);
        CSVutil.save();
        return task.getId();
    }

    @Override
    public int addNewEpic(Epic epic){
        super.addNewEpic(epic);
        historyManager.addTask(epic);
        CSVutil.save();
        return epic.getId();
    }

    @Override
    public int addNewSubtask(Subtask subtask){
        super.addNewSubtask(subtask);
        historyManager.addTask(subtask);
        CSVutil.save();
        return subtask.getId();
    }

    @Override
    public Task getTask(int id){
        Task task = super.getTask(id);
        historyManager.addTask(task);
        CSVutil.save();
        return task;
    }

    @Override
    public Epic getEpic(int id){
        Epic epic = super.getEpic(id);
        historyManager.addTask(epic);
        CSVutil.save();
        return epic;
    }
    @Override
    public Subtask getSubtask(int id){
        Subtask subtask = super.getSubtask(id);
        historyManager.addTask(subtask);
        CSVutil.save();
        return subtask;
    }

    @Override
    public void updateTask(Task task){
        super.updateTask(task);
        historyManager.addTask(task);
        CSVutil.save();
    }

    @Override
    public void updateEpic(Epic epic){
        super.updateEpic(epic);
        historyManager.addTask(epic);
        CSVutil.save();
    }

    @Override
    public void updateSubtask(Subtask subtask){
        super.updateSubtask(subtask);
        historyManager.addTask(subtask);
        CSVutil.save();
    }

    @Override
    public void deleteByIdTasks(int id){
        super.deleteByIdTasks(id);
        historyManager.removeTask(id);
        CSVutil.save();
    }
    @Override
    public void deleteByIdEpic(int id){
        ArrayList<Subtask> subtasksInEpic = super.getAllSubtasksOfEpic(super.getEpic(id));
        for (Subtask subtask : subtasksInEpic) {
            historyManager.removeTask(subtask.getId());
            super.deleteByIdSubtask(subtask.getId());
        }
        super.deleteByIdEpic(id);
        historyManager.removeTask(id);
        CSVutil.save();
    }
    @Override
    public void deleteByIdSubtask(int id){
        super.deleteByIdSubtask(id);
        historyManager.removeTask(id);
        CSVutil.save();
    }

    @Override
    public void deleteAllTasks(){
        super.deleteAllTasks();
        CSVutil.save();
    }
    @Override
    public void deleteAllEpics(){
        super.deleteAllEpics();
        CSVutil.save();
    }

    @Override
    public void deleteAllSubtasks(){
        super.deleteAllSubtasks();
        CSVutil.save();
    }

    @Override
    public ArrayList<Task> getAllTasks(){
        ArrayList tasks = super.getAllTasks();
        CSVutil.save();
        return tasks;
    }
    @Override
    public ArrayList<Epic> getAllEpics(){
        ArrayList epics = super.getAllEpics();
        CSVutil.save();
        return epics;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks(){
        ArrayList subtasks = super.getAllSubtasks();
        CSVutil.save();
        return subtasks;
    }

    @Override
    public List<Task> getHistory() {
        List history = super.getHistory();
        historyToString(historyManager);
        return history;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasksOfEpic(Epic epic) {
        ArrayList<Subtask> subtasksOfEpic;
        if (epic.getSubtasks() != null) {
            subtasksOfEpic = new ArrayList<>(epic.getSubtasks());
        } else {
            subtasksOfEpic = null;
        }
        return subtasksOfEpic;
    }

    public Task getObject(int id){
        HashMap <Integer, Task> savedTasks = mergeAllTasks();
        Task task = savedTasks.get(id);
        return task;
    }
}
