package manager;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager{

    private static File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    private static InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    public static void main(String[] args) {


        FileBackedTasksManager manager = new FileBackedTasksManager(new File("history.csv"));

        int taskId = manager.addNewTask(new Task("TASK 1", "...", Status.NEW));
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
        

        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(new File("history.csv"));

        manager2.getEpic(3);
        manager2.getTask(1);
        manager2.getSubtask(6);
        manager2.getSubtask(4);

        manager2.save();


    }



    private static FileBackedTasksManager loadFromFile(File file){
        List <String> info = new ArrayList<>();
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))){
            while(fileReader.ready()){
                String line = fileReader.readLine();
                if(!line.isEmpty()){
                    info.add(line);
                }
            }
            fileReader.close();
            info.remove(0);
            int index = info.size()-1;
            for (int i = 0; i <= index-1; i++) {
                manager.fromString(info.get(i));
            }
            List <Integer> loadHistory = manager.historyFromString(info.get(index));
            manager.reloadHistory(loadHistory);
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла.");
        }
        return manager;
    }


    public void save() {
        try {
            Writer fileWriter = new FileWriter(file);
            fileWriter.write("id,type,name,status,description,epic");
            fileWriter.write("\n");
            HashMap <Integer, Task> savedTasks = mergeAllTasks();
            for (Integer key : savedTasks.keySet()) {
                Task task = savedTasks.get(key);
                fileWriter.write(toString(task) + "\n");
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString(historyManager));
            fileWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException();
        }

    }

    public String toString(Task task){
        String line = "";
        if(task.getType().equals(TaskType.SUBTASK)){
            int id = task.getId();
            line = String.format("%s, %s, %s, %s, %s, %s", task.getId(), task.getType(), task.getName(), task.getStatus(),
                    task.getDescription(), super.getSubtask(id).getEpicId());
        } else {
            line = String.format("%s, %s, %s, %s, %s", task.getId(), task.getType(), task.getName(), task.getStatus(),
                    task.getDescription());
        }
        return line;
    }

    public Task fromString(String line){
        String [] array = line.split(", ");
        if(array[1].equals("TASK")){
            int taskId = super.addNewTask(new Task(array[2], array[4], Status.valueOf(array[3])));
            int newId = Integer.parseInt(array[0]);
            Task task = super.getTask(taskId);
            if(taskId != newId){
                task.setId(newId);
                setIdInHashMap(taskId, newId, task);
            }
            return task;
        }
        if(array[1].equals("SUBTASK")){
            int subtaskId = super.addNewSubtask(new Subtask(array[2], array[4], Integer.parseInt(array[5]), Status.valueOf(array[3])));
            int epicId = Integer.parseInt(array[5]);
            ArrayList<Subtask> listOfSubtasks = getListOfSubtasks(epicId);
            getEpic(epicId).setSubtasks(listOfSubtasks);
            int newId = Integer.parseInt(array[0]);
            Subtask subtask = super.getSubtask(subtaskId);
            if(subtaskId != newId){
                subtask.setId(newId);
                setIdInHashMap(subtaskId, newId, subtask);
            }
            return subtask;
        }
        if(array[1].equals("EPIC")){
            int epicId = super.addNewEpic(new Epic(array[2], array[4]));
            int newId = Integer.parseInt(array[0]);
            Epic epic = super.getEpic(epicId);
            if(epicId != newId){
                epic.setId(newId);
                setIdInHashMap(epicId, newId, epic);
            }
            return epic;
        }
        return null;
    }

    private ArrayList<Subtask> getListOfSubtasks(int epicId){
        ArrayList<Subtask> listOfSubtasks = super.getAllSubtasks();
        ArrayList <Subtask> epicSubtasks = new ArrayList<>();
        for (Subtask subtask : listOfSubtasks) {
            if(subtask.getEpicId() == epicId){
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
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

    public void reloadHistory(List <Integer> savedId){
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

    @Override
    public int addNewTask(Task task){
        super.addNewTask(task);
        historyManager.addTask(task);
        save();
        return task.getId();
    }

    @Override
    public int addNewEpic(Epic epic){
        super.addNewEpic(epic);
        historyManager.addTask(epic);
        save();
        return epic.getId();
    }

    @Override
    public int addNewSubtask(Subtask subtask){
        super.addNewSubtask(subtask);
        historyManager.addTask(subtask);
        save();
        return subtask.getId();
    }

    @Override
    public Task getTask(int id){
        Task task = super.getTask(id);
        historyManager.addTask(task);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id){
        Epic epic = super.getEpic(id);
        historyManager.addTask(epic);
        save();
        return epic;
    }
    @Override
    public Subtask getSubtask(int id){
        Subtask subtask = super.getSubtask(id);
        historyManager.addTask(subtask);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task){
        super.updateTask(task);
        historyManager.addTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic){
        super.updateEpic(epic);
        historyManager.addTask(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask){
        super.updateSubtask(subtask);
        historyManager.addTask(subtask);
        save();
    }

    @Override
    public void deleteByIdTasks(int id){
        super.deleteByIdTasks(id);
        historyManager.removeTask(id);
        save();
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
        save();
    }
    @Override
    public void deleteByIdSubtask(int id){
        super.deleteByIdSubtask(id);
        historyManager.removeTask(id);
        save();
    }

    @Override
    public void deleteAllTasks(){
        super.deleteAllTasks();
        save();
    }
    @Override
    public void deleteAllEpics(){
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks(){
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public ArrayList<Task> getAllTasks(){
        ArrayList tasks = super.getAllTasks();
        save();
        return tasks;
    }
    @Override
    public ArrayList<Epic> getAllEpics(){
        ArrayList epics = super.getAllEpics();
        save();
        return epics;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks(){
        ArrayList subtasks = super.getAllSubtasks();
        save();
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
