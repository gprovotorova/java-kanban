import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import server.KVServer;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {

        new KVServer().start();
        TaskManager taskManager = Managers.getDefaultTaskManager();

        //Создание задачи 1
        int taskId = taskManager.addNewTask(new Task("...", "...", Status.NEW, 1693575487L, 43200L));
        System.out.println("Create new task: " + taskId);
        System.out.println("Information about task: " + taskManager.getTask(taskId).toString());

        //Создание задачи 2
        taskId = taskManager.addNewTask(new Task("...", "...", Status.NEW, 1693661887L, 43200L));
        System.out.println("Create new task: " + taskId);
        System.out.println("Information about task: " + taskManager.getTask(taskId).toString());

        //Создание эпика 1
        ArrayList<Subtask> subtasks = new ArrayList<>();
        int epicId = taskManager.addNewEpic(new Epic("...", "..."));
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = taskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.IN_PROGRESS, 1693748287L, 43200L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        subtaskId = taskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.NEW, 1693834687L, 43200L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        subtaskId = taskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.DONE, 1693921087L, 43200L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        taskManager.getEpic(epicId).countEpicTime();
        System.out.println("Create new epic: " + epicId);
        System.out.println("Information about epic: " + taskManager.getEpic(epicId).toString());

        //Создание эпика 2
        subtasks = new ArrayList<>();
        epicId = taskManager.addNewEpic(new Epic("...", "..."));
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        subtaskId = taskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.DONE, 1694007487L, 43200L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        taskManager.getEpic(epicId).countEpicTime();
        System.out.println("Create new epic: " + epicId);
        System.out.println("Information about epic: " + taskManager.getEpic(epicId).toString());

        // Просмотр истории
        System.out.println("Your viewing history:" + taskManager.getHistory().toString());

        //Вывод задач в порядке приоритета
        System.out.println("Priority history: " + taskManager.getPrioritizedTasks());

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
        taskManager.deleteByIdTask(1);

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

        //Вывод задач в порядке приоритета
        System.out.println("Priority history: " + taskManager.getPrioritizedTasks());

    }
}