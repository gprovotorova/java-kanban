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
        Task task = manager.createNewTask(new Task("Покупка подарка", "Купить и упаковать подарок подруге", Status.NEW));
        System.out.println("Create task: " + task);

        //Получение по идентификатору
        Task taskFromManager = manager.getTask(task.getID());
        System.out.println("Get task: " + taskFromManager);

        //Обновление
        taskFromManager.setName("Подарок подруге");
        manager.updateTask(taskFromManager);
        System.out.println("Update task: " + taskFromManager);

        //Создание задачи 2
        task = manager.createNewTask(new Task("Купить платье", "Купить черное длинное платье", Status.InProgress));
        System.out.println("Create task: " + task.toString());

        //Получение по идентификатору
        taskFromManager = manager.getTask(task.getID());
        System.out.println("Get task: " + taskFromManager);

        //Обновление
        taskFromManager.setDescription("Купить короткое черное платье");
        manager.updateTask(taskFromManager);
        System.out.println("Update task: " + taskFromManager);

        //Создание эпика 1
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        Epic epic = manager.createNewEpic(new Epic("Подготовка вечеринки", "Вечеринка, чтобы отпраздновать день рождения", listOfSubtasks, manager.calculateEpicStatus(task.getID())));
        Subtask subtask1 = manager.createNewSubtask(new Subtask("Выбрать тему", "...", epic, Status.NEW));
        Subtask subtask2 = manager.createNewSubtask(new Subtask("Выбрать место", "...", epic, Status.DONE));
        Subtask subtask3 = manager.createNewSubtask(new Subtask("Купить торт", "...", epic, Status.InProgress));
        listOfSubtasks.add(subtask1);
        listOfSubtasks.add(subtask2);
        listOfSubtasks.add(subtask3);
        epic.setSubtasks(listOfSubtasks);
        System.out.println("Create epic: " + epic);

        //Обновление подзадачи
        Epic epicFromManager = manager.getEpic(3);
        subtask1.setDescription("Выбрать тему и цвета вечеринки");
        manager.updateSubtask(subtask1);
        System.out.println("Update epic: " + epicFromManager);

        //Создание эпика 2
        ArrayList<Subtask> listOfNewSubtasks = new ArrayList<>();
        epic = manager.createNewEpic(new Epic("Торт", "Торт для дня рождения", listOfNewSubtasks, manager.calculateEpicStatus(task.getID())));
        Subtask newSubtask = manager.createNewSubtask(new Subtask("Выбрать кондитерскую", "...", epic, Status.InProgress));
        listOfNewSubtasks.add(newSubtask);
        epic.setSubtasks(listOfNewSubtasks);
        System.out.println("Create epic: " + epic);

        //Получение эпика по идентификатору
        epicFromManager = manager.getEpic(epic.getID());
        System.out.println("Get epic: " + epicFromManager);

        //Получение подзадачи по идентификатору
        Subtask subtaskFromManager = manager.getSubtask(6);
        System.out.println("Get subtask: " + subtaskFromManager);

        //Обновление
        epic.setDescription("Медовик");
        manager.updateEpic(epicFromManager);
        System.out.println("Update epic: " + epicFromManager);

        //Получение списка всех задач
        System.out.println("All tasks: " + manager.getAllTasks());

        //Получение списка всех эпиков
        System.out.println("All epics: " + manager.getAllEpics());

        //Получение списка всех подзадач
        System.out.println("All subtasks: " + manager.getAllSubtasks());

        //Получение всех задач одного эпика
        epicFromManager = manager.getEpic(epic.getID());
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
