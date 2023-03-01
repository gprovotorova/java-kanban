package homeworkProject;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.Manager;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        Manager manager = new Manager();

        //Создание задачи 1
        int taskID = manager.addNewTask(new Task("Покупка подарка", "Купить и упаковать подарок подруге", Status.NEW));
        System.out.println("Create new task: " + taskID);
        System.out.println("Information about task: " + manager.getTask(taskID).toString());


        //Получение по идентификатору
        Task taskFromManager = manager.getTask(taskID);
        System.out.println("Get task: " + taskFromManager);

        //Обновление
        taskFromManager.setName("Подарок подруге");
        manager.updateTask(taskFromManager);
        System.out.println("Update task: " + taskFromManager);


        //Создание задачи 2
        taskID = manager.addNewTask(new Task("Купить платье", "Купить черное длинное платье", Status.InProgress));
        System.out.println("Create new task: " + taskID);
        System.out.println("Information about task: " + manager.getTask(taskID).toString());

        //Получение по идентификатору
        taskFromManager = manager.getTask(taskID);
        System.out.println("Get task: " + taskFromManager);

        //Обновление
        taskFromManager.setDescription("Купить короткое черное платье");
        manager.updateTask(taskFromManager);
        System.out.println("Update task: " + taskFromManager);

        //Создание эпика 1
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();

        int epicID = manager.addNewEpic(new Epic("Подготовка вечеринки", "Вечеринка, чтобы отпраздновать " +
                "день рождения", listOfSubtasks));
        int subtaskID = manager.addNewSubtask(new Subtask("Выбрать тему", "...", manager.getEpic(epicID), Status.DONE));
        listOfSubtasks.add(manager.getSubtask(subtaskID));
        subtaskID = manager.addNewSubtask(new Subtask("Выбрать место", "...", manager.getEpic(epicID), Status.DONE));
        listOfSubtasks.add(manager.getSubtask(subtaskID));
        subtaskID = manager.addNewSubtask(new Subtask("Купить торт", "...", manager.getEpic(epicID), Status.DONE));
        listOfSubtasks.add(manager.getSubtask(subtaskID));
        manager.getEpic(epicID).setSubtasks(listOfSubtasks);
        manager.setEpicStatus(epicID);
        System.out.println("Create new epic: " + epicID);
        System.out.println("Information about epic: " + manager.getEpic(epicID).toString());

        //Обновление подзадачи
        Epic epicFromManager = manager.getEpic(3);
        epicFromManager.setDescription("Выбрать тему и цвета вечеринки");
        manager.updateEpic(manager.getEpic(epicFromManager.getID()));
        System.out.println("Update epic: " + epicFromManager);

        //Создание эпика 2
        ArrayList<Subtask> listOfNewSubtasks = new ArrayList<>();
        epicID = manager.addNewEpic(new Epic("Торт", "Торт для дня рождения", listOfNewSubtasks));
        subtaskID = manager.addNewSubtask(new Subtask("Выбрать кондитерскую", "...", manager.getEpic(epicID), Status.InProgress));
        listOfNewSubtasks.add(manager.getSubtask(subtaskID));
        manager.getEpic(epicID).setSubtasks(listOfNewSubtasks);
        manager.setEpicStatus(epicID);
        System.out.println("Create new epic: " + epicID);
        System.out.println("Information about epic: " + manager.getEpic(epicID).toString());

        //Получение эпика по идентификатору
        epicFromManager = manager.getEpic(epicID);
        System.out.println("Get epic: " + epicFromManager);

        //Получение подзадачи по идентификатору
        Subtask subtaskFromManager = manager.getSubtask(6);
        System.out.println("Get subtask: " + subtaskFromManager);

        //Обновление
        epicFromManager.setDescription("Медовик");
        manager.updateEpic(epicFromManager);
        System.out.println("Update epic: " + epicFromManager);


        //Получение списка всех задач
        System.out.println("All tasks: " + manager.getAllTasks());

        //Получение списка всех эпиков
        System.out.println("All epics: " + manager.getAllEpics());

        //Получение списка всех подзадач
        System.out.println("All subtasks: " + manager.getAllSubtasks());

        //Получение всех задач одного эпика
        epicFromManager = manager.getEpic(epicID);
        System.out.println("All subtasks of epic: " + manager.getAllSubtasksOfEpic(epicFromManager));

        //Удаление по идентификатору задачи
        manager.deleteByIDTasks(1);

        //Удаление по идентификатору подзадачи
        manager.deleteByIDSubtask(6);

        //Удаление по идентификатору эпика
        manager.deleteByIDEpic(3);

        //Удаление всех задач
        manager.deleteAllTasks();

        //Удаление всех подзадач
        manager.deleteAllSubtasks();

        //Удаление всех эпиков
        manager.deleteAllEpics();

    }
}
