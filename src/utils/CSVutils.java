package utils;

import exceptions.ManagerSaveException;
import manager.*;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSVutils {

    private static String CONST = "";
    private static String HEADLINE = "id,type,name,status,description,epic\n";
    private static final String COMMA = ", ";
    private static FileBackedTasksManager fileManager;
    private static InMemoryHistoryManager historyManager;
    private File file;

    public CSVutils(File file, FileBackedTasksManager fileManager, InMemoryHistoryManager historyManager) {
        this.file = file;
        this.fileManager = fileManager;
        this.historyManager = historyManager;
    }

    public void save() {
        try (Writer fileWriter = new FileWriter("history.csv");){
            fileWriter.write(HEADLINE);
            Map<Integer, Task> savedTasks = fileManager.mergeAllTasks();
            for (Integer key : savedTasks.keySet()) {
                Task task = savedTasks.get(key);
                fileWriter.write(toString(task) + "\n");
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла.");
        }
    }

    private String toString(Task task){
        if(task.getType().equals(TaskType.SUBTASK)){
            int id = task.getId();
            Subtask subtask = fileManager.subtasks.get(id);
            CONST = String.format("%s, %s, %s, %s, %s, %s",
                    task.getId(),
                    task.getType(),
                    task.getName(),
                    task.getStatus(),
                    task.getDescription(),
                    subtask.getEpicId());
        } else {
            CONST = String.format("%s, %s, %s, %s, %s",
                    task.getId(),
                    task.getType(),
                    task.getName(),
                    task.getStatus(),
                    task.getDescription());
        }
        return CONST;
    }

    public Task fromString(String line){
        String [] array = line.split(COMMA);
        String taskType = array[1];
        switch(taskType){
            case "TASK":
                int taskId = fileManager.addNewTask(new Task(array[2], array[4], Status.valueOf(array[3])));
                int newId = Integer.parseInt(array[0]);
                Task task = fileManager.getTask(taskId);
                if(taskId != newId){
                    task.setId(newId);
                    fileManager.changeId(taskId, newId, task);
                }
                return task;

            case "SUBTASK":
                int subtaskId = fileManager.addNewSubtask(new Subtask(array[2], array[4], Integer.parseInt(array[5]), Status.valueOf(array[3])));
                int epicId = Integer.parseInt(array[5]);
                ArrayList<Subtask> listOfSubtasks = fileManager.getSubtasks(epicId);
                fileManager.getEpic(epicId).setSubtasks(listOfSubtasks);
                newId = Integer.parseInt(array[0]);
                Subtask subtask = fileManager.getSubtask(subtaskId);
                if(subtaskId != newId){
                    subtask.setId(newId);
                    fileManager.changeId(subtaskId, newId, subtask);
                }
                return subtask;
            case "EPIC":
                epicId = fileManager.addNewEpic(new Epic(array[2], array[4]));
                newId = Integer.parseInt(array[0]);
                Epic epic = fileManager.getEpic(epicId);
                if(epicId != newId){
                    epic.setId(newId);
                    fileManager.changeId(epicId, newId, epic);
                }
                return epic;
            default:
                throw new ManagerSaveException("Произошла ошибка: не найден тип задачи.");
        }
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
        String [] array = line.split(COMMA);
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
            Task task = fileManager.getObject(id);
            savedTasks.add(task);
        }
        historyManager.cleanViewHistory();
        for (Task savedTask : savedTasks) {
            historyManager.addTask(savedTask);
        }
    }
}
