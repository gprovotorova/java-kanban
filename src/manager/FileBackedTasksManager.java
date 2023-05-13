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

    public FileBackedTasksManager(File file) {
        this.file = file;
    }
    
    public FileBackedTasksManager getFileManager(){
        return FileBackedTasksManager.this;
    }

    public String getFileName(){
        return file.getName();
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
                CSVutils.fromString(line, manager);
            }
            List <Integer> loadHistory = CSVutils.historyFromString(history);
            CSVutils.reloadHistory(loadHistory, manager);
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
        CSVutils.save(getFileManager());
        return task.getId();
    }

    @Override
    public int addNewEpic(Epic epic){
        super.addNewEpic(epic);
        CSVutils.save(getFileManager());
        return epic.getId();
    }

    @Override
    public int addNewSubtask(Subtask subtask){
        super.addNewSubtask(subtask);
        CSVutils.save(getFileManager());
        return subtask.getId();
    }

    @Override
    public Task getTask(int id){
        Task task = super.getTask(id);
        CSVutils.save(getFileManager());
        return task;
    }

    @Override
    public Epic getEpic(int id){
        Epic epic = super.getEpic(id);
        CSVutils.save(getFileManager());
        return epic;
    }

    @Override
    public Subtask getSubtask(int id){
        Subtask subtask = super.getSubtask(id);
        CSVutils.save(getFileManager());
        return subtask;
    }

    @Override
    public void updateTask(Task task){
        super.updateTask(task);
        CSVutils.save(getFileManager());
    }

    @Override
    public void updateEpic(Epic epic){
        super.updateEpic(epic);
        CSVutils.save(getFileManager());
    }

    @Override
    public void updateSubtask(Subtask subtask){
        super.updateSubtask(subtask);
        CSVutils.save(getFileManager());
    }

    @Override
    public void deleteByIdTask(int id){
        super.deleteByIdTask(id);
        CSVutils.save(getFileManager());
    }

    @Override
    public void deleteByIdEpic(int id){
        ArrayList<Subtask> subtasksInEpic = super.getAllSubtasksOfEpic(super.getEpic(id));
        for (Subtask subtask : subtasksInEpic) {
            super.deleteByIdSubtask(subtask.getId());
        }
        super.deleteByIdEpic(id);
        CSVutils.save(getFileManager());
    }
    @Override
    public void deleteByIdSubtask(int id){
        super.deleteByIdSubtask(id);
        CSVutils.save(getFileManager());
    }

    @Override
    public void deleteAllTasks(){
        super.deleteAllTasks();
        CSVutils.save(getFileManager());
    }
    @Override
    public void deleteAllEpics(){
        super.deleteAllEpics();
        CSVutils.save(getFileManager());
    }

    @Override
    public void deleteAllSubtasks(){
        super.deleteAllSubtasks();
        CSVutils.save(getFileManager());
    }

    @Override
    public ArrayList<Task> getAllTasks(){
        ArrayList tasks = super.getAllTasks();
        CSVutils.save(getFileManager());
        return tasks;
    }
    @Override
    public ArrayList<Epic> getAllEpics(){
        ArrayList epics = super.getAllEpics();
        CSVutils.save(getFileManager());
        return epics;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks(){
        ArrayList subtasks = super.getAllSubtasks();
        CSVutils.save(getFileManager());
        return subtasks;
    }

    @Override
    public List<Task> getHistory() {
        List history = super.getHistory();
        CSVutils.historyToString(this.memoryHistoryManager);
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

        int taskId1 = manager.addNewTask(new Task("...", "...", Status.NEW, 1682812800L, 180000L));
        int taskId2 = manager.addNewTask(new Task("...", "...", Status.NEW, 1683425253L, 129600L));

        ArrayList<Subtask> subtasks = new ArrayList<>();
        int epicId = manager.addNewEpic(new Epic("...", "..."));
        manager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = manager.addNewSubtask(new Subtask("...", "...", epicId, Status.IN_PROGRESS, 1683166053L, 172800L));
        subtasks.add(manager.getSubtask(subtaskId));
        subtaskId = manager.addNewSubtask(new Subtask("...", "...", epicId, Status.NEW, 1682906853L, 43200L));
        subtasks.add(manager.getSubtask(subtaskId));
        subtaskId = manager.addNewSubtask(new Subtask("...", "...", epicId, Status.DONE, 1683029253L, 86400L));
        subtasks.add(manager.getSubtask(subtaskId));
        manager.getEpic(epicId).setSubtasks(subtasks);
        manager.getEpic(epicId).countEpicTime();

        subtasks = new ArrayList<>();
        epicId = manager.addNewEpic(new Epic("EPIC 2", "..."));
        manager.getEpic(epicId).setSubtasks(subtasks);
        subtaskId = manager.addNewSubtask(new Subtask("SUBTASK 4", "...", epicId, Status.DONE, 1684029700L, 67000L));
        subtasks.add(manager.getSubtask(subtaskId));
        manager.getEpic(epicId).setSubtasks(subtasks);
        manager.getEpic(epicId).countEpicTime();

        //Вызов задач, подзадач и эпика
        System.out.println(manager.getTask(1));
        System.out.println(manager.getEpic(3));
        System.out.println(manager.getTask(2));
        System.out.println(manager.getSubtask(4));
        System.out.println(manager.getTask(1));
        System.out.println(manager.getEpic(3));
        System.out.println(manager.getSubtask(6));

        FileBackedTasksManager manager2 = loadFromFile(new File("history.csv"));

        System.out.println(manager2.getEpic(3));
        System.out.println(manager2.getTask(1));
        System.out.println(manager2.getSubtask(6));
        System.out.println(manager2.getSubtask(4));

        CSVutils.save(manager2);
    }
}
