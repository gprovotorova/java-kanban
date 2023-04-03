import manager.HistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

public class Main { 
    public static void main(String[] args) {

        HistoryManager memoryHistoryManager = Managers.getDefaultHistory();
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        //Создание задачи 1
        int taskId = taskManager.addNewTask(new Task("Покупка подарка", "Купить и упаковать подарок подруге", Status.NEW));
        System.out.println("Create new task: " + taskId);
        System.out.println("Information about task: " + taskManager.getTask(taskId).toString());

        //Получение по идентификатору
        Task taskFromManager = taskManager.getTask(taskId);
        System.out.println("Get task: " + taskFromManager);

        //Обновление
        taskFromManager.setName("Подарок подруге");
        taskManager.updateTask(taskFromManager);
        System.out.println("Update task: " + taskFromManager);

        //Создание задачи 2
        taskId = taskManager.addNewTask(new Task("Купить платье", "Купить черное длинное платье", Status.IN_PROGRESS));
        System.out.println("Create new task: " + taskId);
        System.out.println("Information about task: " + taskManager.getTask(taskId).toString());

        //Просмотр истории
        System.out.println("Your viewing history:" + taskManager.getHistory().toString());

        //Обновление
        taskFromManager.setDescription("Купить короткое черное платье");
        taskManager.updateTask(taskFromManager);
        System.out.println("Update task: " + taskFromManager);
        
        //Создание эпика 1
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        int epicId = taskManager.addNewEpic(new Epic("Подготовка вечеринки", "Вечеринка, чтобы отпраздновать " +
                "день рождения"));
        taskManager.getEpic(epicId).setSubtasks(listOfSubtasks);
        int subtaskId = taskManager.addNewSubtask(new Subtask("Выбрать тему", "...", epicId, Status.IN_PROGRESS));
        listOfSubtasks.add(taskManager.getSubtask(subtaskId));
        subtaskId = taskManager.addNewSubtask(new Subtask("Выбрать место", "...", epicId, Status.NEW));
        listOfSubtasks.add(taskManager.getSubtask(subtaskId));
        subtaskId = taskManager.addNewSubtask(new Subtask("Купить торт", "...", epicId, Status.DONE));
        listOfSubtasks.add(taskManager.getSubtask(subtaskId));
        taskManager.getEpic(epicId).setSubtasks(listOfSubtasks);
        System.out.println("Create new epic: " + epicId);
        System.out.println("Information about epic: " + taskManager.getEpic(epicId).toString());

        //Обновление подзадачи
        Epic epicFromManager = taskManager.getEpic(3);
        epicFromManager.setDescription("Выбрать тему и цвета вечеринки");
        taskManager.updateEpic(taskManager.getEpic(epicFromManager.getId()));
        System.out.println("Update epic: " + epicFromManager);

        //Создание эпика 2
        listOfSubtasks = new ArrayList<>();
        epicId = taskManager.addNewEpic(new Epic("Торт", "Торт для дня рождения"));
        taskManager.getEpic(epicId).setSubtasks(listOfSubtasks);
        subtaskId = taskManager.addNewSubtask(new Subtask("Выбрать кондитерскую", "...", epicId, Status.IN_PROGRESS));
        listOfSubtasks.add(taskManager.getSubtask(subtaskId));
        taskManager.getEpic(epicId).setSubtasks(listOfSubtasks);
        taskManager.updateEpic(taskManager.getEpic(epicId));
        System.out.println("Create new epic: " + epicId);
        System.out.println("Information about epic: " + taskManager.getEpic(epicId).toString());

        //Получение эпика по идентификатору
        epicFromManager = taskManager.getEpic(epicId);
        System.out.println("Get epic: " + epicFromManager);

        //Получение подзадачи по идентификатору
        Subtask subtaskFromManager = taskManager.getSubtask(6);
        System.out.println("Get subtask: " + subtaskFromManager);

        //Обновление
        epicFromManager.setDescription("Медовик");
        taskManager.updateEpic(epicFromManager);
        System.out.println("Update epic: " + epicFromManager);

        //Просмотр истории
        System.out.println("Your viewing history:" + taskManager.getHistory().toString());

        //Получение списка всех задач
        System.out.println("All tasks: " + taskManager.getAllTasks());

        //Получение списка всех эпиков
        System.out.println("All epics: " + taskManager.getAllEpics());

        //Получение списка всех подзадач
        System.out.println("All subtasks: " + taskManager.getAllSubtasks());

        //Получение всех задач одного эпика
        epicFromManager = taskManager.getEpic(epicId);
        System.out.println("All subtasks of epic: " + taskManager.getAllSubtasksOfEpic(epicFromManager));

        //Удаление по идентификатору задачи
        taskManager.deleteByIdTasks(1);
        
        //Удаление по идентификатору подзадачи
        taskManager.deleteByIdSubtask(6);

        //Удаление по идентификатору эпика
        taskManager.deleteByIdEpic(3);

        //Удаление всех задач
        taskManager.deleteAllTasks();

        //Удаление всех подзадач
        taskManager.deleteAllSubtasks();

        //Удаление всех эпиков
        taskManager.deleteAllEpics();

        //Получение списка всех задач
        System.out.println("All tasks: " + taskManager.getAllTasks());

        //Получение списка всех эпиков
        System.out.println("All epics: " + taskManager.getAllEpics());

        //Получение списка всех подзадач
        System.out.println("All subtasks: " + taskManager.getAllSubtasks());


    }
}