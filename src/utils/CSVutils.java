package utils;

import exceptions.ManagerSaveException;
import manager.*;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.*;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSVutils {

    private static final String COMMA = ", ";
    private static final String CONST = "\n";
    private static final String HEADLINE = "id,type,name,status,description,epic,startTime,duration,endTime\n";

    public static void save(FileBackedTasksManager fileManager) {
        try (Writer fileWriter = new FileWriter(fileManager.getFileName())){
            fileWriter.write(HEADLINE);
            Map<Integer, Task> savedTasks = fileManager.mergeAllTasks();
            for (Integer key : savedTasks.keySet()) {
                Task task = savedTasks.get(key);
                fileWriter.write(CSVutils.toString(task, fileManager) + CONST);
            }
            fileWriter.write(CONST);
            fileWriter.write(CSVutils.historyToString(fileManager.memoryHistoryManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла.");
        }
    }

    public static String toString(Task task, FileBackedTasksManager fileManager){
        String line = "";
        if(task.getType().equals(TaskType.SUBTASK)){
            int id = task.getId();
            Subtask subtask = fileManager.subtasks.get(id);
            line = String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s",
                    task.getId(), //0
                    task.getType(), //1
                    task.getName(), //2
                    task.getStatus(), //3
                    task.getDescription(), //4
                    subtask.getEpicId(), //5
                    task.getStartTime().toString(), //6
                    task.getDuration(), //7
                    task.getEndTime().toString()); //8
        } else {
            line = String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s",
                    task.getId(), //0
                    task.getType(), //1
                    task.getName(), //2
                    task.getStatus(), //3
                    task.getDescription(), //4
                    0,
                    task.getStartTime().toString(), //5
                    task.getDuration(), //6
                    task.getEndTime().toString()); //7
        }
        return line;
    }

    public static Task fromString(String line, FileBackedTasksManager manager){
        String [] array = line.split(COMMA);
        String taskType = array[1];
        switch(taskType){
            case "TASK":
                int taskId = manager.addNewTask(new Task(array[2], array[4], Status.valueOf(array[3]), Long.parseLong(String.valueOf(Instant.parse(array[6]).toEpochMilli()/1000)), Long.parseLong(array[7])));
                int newId = Integer.parseInt(array[0]);
                Task task = manager.getTask(taskId);
                if(taskId != newId){
                    task.setId(newId);
                    manager.changeId(taskId, newId, task);
                }
                return task;

            case "SUBTASK":
                int subtaskId = manager.addNewSubtask(new Subtask(array[2], array[4], Integer.parseInt(array[5]), Status.valueOf(array[3]), Long.parseLong(String.valueOf(Instant.parse(array[6]).toEpochMilli()/1000)), Long.parseLong(array[7])));
                int epicId = Integer.parseInt(array[5]);
                ArrayList<Subtask> listOfSubtasks = manager.getSubtasks(epicId);
                manager.getEpic(epicId).setSubtasks(listOfSubtasks);
                newId = Integer.parseInt(array[0]);
                Subtask subtask = manager.getSubtask(subtaskId);
                if(subtaskId != newId){
                    subtask.setId(newId);
                    manager.changeId(subtaskId, newId, subtask);
                }
                manager.getEpic(epicId).countEpicTime();
                return subtask;
            case "EPIC":
                epicId = manager.addNewEpic(new Epic(array[2], array[4]));
                newId = Integer.parseInt(array[0]);
                Epic epic = manager.getEpic(epicId);
                if(epicId != newId){
                    epic.setId(newId);
                    manager.changeId(epicId, newId, epic);
                }
                epic.countEpicTime();
                return epic;
            default:
                throw new ManagerSaveException("Произошла ошибка: не найден тип задачи.");
        }
    }

    public static String historyToString(HistoryManager historyManager) {
        List<Task> addedTasks = historyManager.getAll();
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

    public static void reloadHistory(List<Integer> savedId, FileBackedTasksManager manager){
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
}
