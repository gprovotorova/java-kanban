package manager;

import model.Status;
import model.Task;
import model.Epic;
import model.Subtask;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    private int id = 0;
    final HashMap <Integer, Task> listOfTasks = new HashMap<>();
    final HashMap <Integer, Epic> listOfEpics = new HashMap<>();
    final HashMap <Integer, Subtask> listOfSubtasks = new HashMap<>();

    //Генератор ID
    private int generateId(){
        return ++id;
    }

    //Создание задачи
    public int addNewTask(Task task) {
        task.setId(generateId());
        listOfTasks.put(task.getId(), task);
        return task.getId();
    }

    //Создание эпика
    public int addNewEpic(Epic epic) {
        epic.setId(generateId());
        listOfEpics.put(epic.getId(), epic);
        return epic.getId();
    }

    //Создание подзадачи
    public int addNewSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        for (int id : listOfEpics.keySet()) {
            if(id == epicId){
                subtask.setId(generateId());
                listOfSubtasks.put(subtask.getId(), subtask);
                return subtask.getId();
            }
        }
        return 0;
    }

    //Получение по идентификатору
    public Task getTask(int id) {
        return listOfTasks.get(id);
    }
    public Epic getEpic(int id) {
        return listOfEpics.get(id);
    }
    public Subtask getSubtask(int id){
        return listOfSubtasks.get(id);
    }

    //Обновление
    public void updateTask(Task task) {
        Task savedTask = listOfTasks.get(task.getId());
        savedTask.setName(task.getName());
        savedTask.setStatus(task.getStatus());
        savedTask.setDescription(task.getDescription());
    }
    public void updateEpic(Epic epic) {
        Epic savedEpic = listOfEpics.get(epic.getId());
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
        epic.setStatus(savedEpic.getStatus());
        epic.setSubtasks(savedEpic.getSubtasks());
    }
    public void updateSubtask(Subtask subtask) {
        Subtask savedSubtask = listOfSubtasks.get(subtask.getId());
        Epic savedEpic = listOfEpics.get(savedSubtask.getEpicId());
        setEpicStatus(savedEpic.getId());
        savedSubtask.setName(subtask.getName());
        savedSubtask.setDescription(subtask.getDescription());
        subtask.setStatus(savedSubtask.getStatus());
    }

    //Удаление по идентификатору
    public void deleteByIDTasks(int id) {
        listOfTasks.remove(id);
        System.out.println("Delete id task " + id );
    }
    public void deleteByIDEpic(int id) {
        listOfEpics.remove(id);
        System.out.println("Delete id epic " + id );
    }

    public void deleteByIDSubtask(int id) {
        listOfSubtasks.remove(id);
        System.out.println("Delete id subtask " + id );
    }

    //Статус эпика
    public Status setEpicStatus(int id){
        Epic savedEpic = listOfEpics.getOrDefault(id, null);
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
    public void deleteAllTasks(){
        listOfTasks.clear();
    }

    public void deleteAllEpics() {
        listOfEpics.clear();
        deleteAllSubtasks();
    }

    public void deleteAllSubtasks() {
        listOfSubtasks.clear();
    }

    //Получение списка всех задач
    public ArrayList<String> getAllTasks() {
        ArrayList<String> nameOfTasks = new ArrayList<>();
        for (int id : listOfTasks.keySet()) {
            Task savedTask = listOfTasks.get(id);
            nameOfTasks.add(savedTask.getName());
        }
        return nameOfTasks;
    }
    public ArrayList<String> getAllEpics(){
        ArrayList<String> nameOfEpics = new ArrayList<>();
        for (int id : listOfEpics.keySet()) {
            Epic savedEpic = listOfEpics.get(id);
            nameOfEpics.add(savedEpic.getName());
        }
        return nameOfEpics;
    }

    public ArrayList<String> getAllSubtasks() {
        ArrayList<String> savedListOfSubtasks = new ArrayList<>();
        for (int id : listOfSubtasks.keySet()) {
            String savedSubtask = listOfSubtasks.get(id).getName();
            savedListOfSubtasks.add(savedSubtask);
        }
        return savedListOfSubtasks;
    }

    public ArrayList<Subtask> getAllSubtasksOfEpic(Epic epic) {
        int epicID = epic.getId();
        ArrayList<Subtask> nameOfSubtasks = new ArrayList<>();
        for (int id : listOfEpics.keySet()) {
            if (id == epicID){
                Epic savedEpic = listOfEpics.get(id);
                nameOfSubtasks = savedEpic.getSubtasks();
            }
        }
        return nameOfSubtasks;
    }

}
