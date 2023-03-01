package service;

import model.Status;
import model.Task;
import model.Epic;
import model.Subtask;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    private int ID = 0;
    private HashMap <Integer, Task> listOfTasks = new HashMap<>();
    private HashMap <Integer, Epic> listOfEpics = new HashMap<>();
    private HashMap <Integer, Subtask> listOfSubtasks = new HashMap<>();

    //Генератор ID
    private int generateID(){
        return ++ID;
    }

    public int getID() {
        return ID;
    }

    //Создание задачи
    public int addNewTask(Task task) {
        task.setID(generateID());
        listOfTasks.put(task.getID(), task);
        return task.getID();
    }

    //Создание эпика
    public int addNewEpic(Epic epic) {
        epic.setID(generateID());
        listOfEpics.put(epic.getID(), epic);
        return epic.getID();
    }

    //Создание подзадачи
    public int addNewSubtask(Subtask subtask) {
        Epic epic = subtask.getEpic();
        String newEpic = subtask.getEpic().getName();
        for (int id : listOfEpics.keySet()) {
            Epic savedEpic = listOfEpics.get(id);
            String savedEpicName = savedEpic.getName();
            if(savedEpicName.equals(newEpic)){
                subtask.setID(generateID());
                listOfSubtasks.put(subtask.getID(), subtask);
                return subtask.getID();
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
        Task savedTask = listOfTasks.get(task.getID());
        savedTask.setName(task.getName());
        savedTask.setStatus(task.getStatus());
        savedTask.setDescription(task.getDescription());
    }
    public void updateEpic(Epic epic) {
        Epic savedEpic = listOfEpics.get(epic.getID());
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
        epic.setStatus(savedEpic.getStatus());
        epic.setSubtasks(savedEpic.getSubtasks());
    }
    public void updateSubtask(Subtask subtask) {
        Subtask savedSubtask = listOfSubtasks.get(subtask.getID());
        Epic epic = savedSubtask.getEpic();
        Epic savedEpic = listOfEpics.get(epic.getID());
        setEpicStatus(savedEpic.getID());
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
        int epicID = epic.getID();
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
