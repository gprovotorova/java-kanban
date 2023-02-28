package service;

import model.Status;
import model.Task;
import model.Epic;
import model.Subtask;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    public HashMap <Integer, Task> listOfTasks = new HashMap<>();
    public HashMap <Integer, Epic> listOfEpics = new HashMap<>();
    public HashMap <Integer, Subtask> listOfSubtasks = new HashMap<>();

    //Генератор ID
    int ID = 0;
    private int generateID(){
        return ++ID;
    }

    //Создание задачи
    public Task createNewTask(Task task) {
        task.setID(generateID());
        listOfTasks.put(task.getID(), task);
        return task;
    }

    //Создание эпика
    public Epic createNewEpic(Epic epic) {
        epic.setID(generateID());
        listOfEpics.put(epic.getID(), epic);
        return epic;
    }

    //Создание подзадачи
    public Subtask createNewSubtask(Subtask subtask) {
        String newEpic = subtask.getEpic().getName();
        for (int id : listOfEpics.keySet()) {
            Epic savedEpic = listOfEpics.get(id);
            String savedEpicName = savedEpic.getName();
            if(savedEpicName.equals(newEpic)){
                subtask.setID(generateID());
                listOfSubtasks.put(subtask.getID(), subtask);
                return subtask;
            }
        }
        return null;
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
        calculateEpicStatus(savedEpic.getID());
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
    public Status calculateEpicStatus(int id){
        Status status = Status.NEW;
        Epic savedEpic = listOfEpics.getOrDefault(id, null);
        if(savedEpic != null) {
            ArrayList<Subtask> subtaskArrayList = savedEpic.getSubtasks();
            for (int i = 0; i < subtaskArrayList.size(); i++) {
                Subtask savedSubtask = subtaskArrayList.get(i);
                if(savedSubtask.getStatus().equals(Status.DONE)){
                    status = Status.DONE;
                } else {
                    status = Status.InProgress;
                }
            }
        } else {
            status = Status.NEW;
        }
        return status;
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

    public HashMap <String, ArrayList<String>> getAllSubtasks() {
        HashMap <String, ArrayList<String>> epicAndListOfSubtasks = new HashMap<>();
        for (int id : listOfSubtasks.keySet()) {
            Subtask savedSubtask = listOfSubtasks.get(id);
            String savedEpicName = savedSubtask.getEpic().getName();
            if(!epicAndListOfSubtasks.containsKey(savedEpicName)){
                ArrayList<String> nameOfSubtasks = new ArrayList<>();
                nameOfSubtasks.add(savedSubtask.getName());
                epicAndListOfSubtasks.put(savedEpicName, nameOfSubtasks);
            } else {
                ArrayList <String> nameOfSavedSubtasks = epicAndListOfSubtasks.get(savedEpicName);
                nameOfSavedSubtasks.add(savedSubtask.getName());
                epicAndListOfSubtasks.put(savedEpicName, nameOfSavedSubtasks);
            }
        }
        return epicAndListOfSubtasks;
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
