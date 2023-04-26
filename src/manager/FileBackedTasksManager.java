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

public class FileBackedTasksManager extends InMemoryTaskManager{

    private File file;
    private static InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    private static FileBackedTasksManager manager = new FileBackedTasksManager(new File("history.csv"));
    private static final CSVutils CSVutil = new CSVutils(manager, historyManager);

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(File file){
        List<String> info = new ArrayList<>();
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))){
            while(fileReader.ready()){
                String line = fileReader.readLine();
                if(!line.isEmpty()){
                    info.add(line);
                }
            }
            info.remove(0);
            int index = info.size()-1;
            String history = info.get(index);
            info.remove(index);
            for(String line : info){
                CSVutil.fromString(line);
            }
            List <Integer> loadHistory = CSVutil.historyFromString(history);
            CSVutil.reloadHistory(loadHistory);
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время чтения файла.");
        }
        return manager;
    }

    public ArrayList<Subtask> getSubtasks(int epicId){
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
        CSVutil.historyToString(historyManager);
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

    public static void main(String[] args) {

        FileBackedTasksManager manager = new FileBackedTasksManager(new File("history.csv"));
        CSVutils CSVutil = new CSVutils(manager, historyManager);

        manager.addNewTask(new Task("TASK 1", "...", Status.NEW));
        manager.addNewTask(new Task("TASK 2", "...", Status.NEW));

        //Создание эпика 1
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        int epicId = manager.addNewEpic(new Epic("EPIC 1", "..."));
        manager.getEpic(epicId).setSubtasks(subtasksOfEpic);
        int subtaskId = manager.addNewSubtask(new Subtask("SUBTASK 1", "...", epicId, Status.IN_PROGRESS));
        subtasksOfEpic.add(manager.getSubtask(subtaskId));
        subtaskId = manager.addNewSubtask(new Subtask("SUBTASK 2", "...", epicId, Status.NEW));
        subtasksOfEpic.add(manager.getSubtask(subtaskId));
        subtaskId = manager.addNewSubtask(new Subtask("SUBTASK 3", "...", epicId, Status.DONE));
        subtasksOfEpic.add(manager.getSubtask(subtaskId));
        manager.getEpic(epicId).setSubtasks(subtasksOfEpic);

        //Создание эпика 2
        subtasksOfEpic = new ArrayList<>();
        epicId = manager.addNewEpic(new Epic("EPIC 2", "..."));
        manager.getEpic(epicId).setSubtasks(subtasksOfEpic);

        //Вызов задач, подзадач и эпика
        System.out.println(manager.getTask(1));
        System.out.println(manager.getEpic(3));
        System.out.println(manager.getTask(2));
        System.out.println(manager.getSubtask(4));
        System.out.println(manager.getTask(1));
        System.out.println(manager.getEpic(3));
        System.out.println(manager.getSubtask(6));

        FileBackedTasksManager manager2 = loadFromFile(new File("history.csv"));

        System.out.println(manager.getEpic(3));
        System.out.println(manager.getTask(1));
        System.out.println(manager.getSubtask(6));
        System.out.println(manager.getSubtask(4));

        manager2.CSVutil.save();
    }
}
