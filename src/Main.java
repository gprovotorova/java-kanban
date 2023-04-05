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
        int taskId = taskManager.addNewTask(new Task("...", "...", Status.NEW));
        System.out.println("Create new task: " + taskId);
        System.out.println("Information about task: " + taskManager.getTask(taskId).toString());

        //Создание задачи 2
        taskId = taskManager.addNewTask(new Task("...", "...", Status.NEW));
        System.out.println("Create new task: " + taskId);
        System.out.println("Information about task: " + taskManager.getTask(taskId).toString());

        //Создание эпика 1
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        int epicId = taskManager.addNewEpic(new Epic("...", "..."));
        taskManager.getEpic(epicId).setSubtasks(listOfSubtasks);
        int subtaskId = taskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.IN_PROGRESS));
        listOfSubtasks.add(taskManager.getSubtask(subtaskId));
        subtaskId = taskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.NEW));
        listOfSubtasks.add(taskManager.getSubtask(subtaskId));
        subtaskId = taskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.DONE));
        listOfSubtasks.add(taskManager.getSubtask(subtaskId));
        taskManager.getEpic(epicId).setSubtasks(listOfSubtasks);
        System.out.println("Create new epic: " + epicId);
        System.out.println("Information about epic: " + taskManager.getEpic(epicId).toString());


        //Создание эпика 2
        listOfSubtasks = new ArrayList<>();
        epicId = taskManager.addNewEpic(new Epic("...", "..."));
        taskManager.getEpic(epicId).setSubtasks(listOfSubtasks);
        System.out.println("Create new epic: " + epicId);
        System.out.println("Information about epic: " + taskManager.getEpic(epicId).toString());

        // Просмотр истории
        System.out.println("Your viewing history:" + taskManager.getHistory().toString());

        //Вызов задач, подзадач и эпика
        taskManager.getTask(1);
        // Просмотр истории
        System.out.println("Your viewing history:" + taskManager.getHistory().toString());

        taskManager.getEpic(3);
        // Просмотр истории
        System.out.println("Your viewing history:" + taskManager.getHistory().toString());

        taskManager.getTask(2);
        // Просмотр истории
        System.out.println("Your viewing history:" + taskManager.getHistory().toString());

        taskManager.getSubtask(4);
        // Просмотр истории
        System.out.println("Your viewing history:" + taskManager.getHistory().toString());

        taskManager.getTask(1);
        // Просмотр истории
        System.out.println("Your viewing history:" + taskManager.getHistory().toString());

        taskManager.getEpic(3);
        // Просмотр истории
        System.out.println("Your viewing history:" + taskManager.getHistory().toString());

        taskManager.getSubtask(6);
        // Просмотр истории
        System.out.println("Your viewing history:" + taskManager.getHistory().toString());

        //Удаление по идентификатору задачи
        taskManager.deleteByIdTasks(1);

        // Просмотр истории
        System.out.println("Your viewing history:" + taskManager.getHistory().toString());

        //Удаление по идентификатору подзадачи
        taskManager.deleteByIdSubtask(6);

        // Просмотр истории
        System.out.println("Your viewing history:" + taskManager.getHistory().toString());

        //Удаление по идентификатору эпика
        taskManager.deleteByIdEpic(3);

        // Просмотр истории
        System.out.println("Your viewing history:" + taskManager.getHistory().toString());

    }
}