import manager.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        //Создание задачи 1
        int taskId = taskManager.addNewTask(new Task("...", "...", Status.NEW, 1682812800L, 180000L));
        System.out.println("Create new task: " + taskId);
        System.out.println("Information about task: " + taskManager.getTask(taskId).toString());

        //Создание задачи 2
        taskId = taskManager.addNewTask(new Task("...", "...", Status.NEW, 1683425253L, 129600L));
        System.out.println("Create new task: " + taskId);
        System.out.println("Information about task: " + taskManager.getTask(taskId).toString());

        //Создание эпика 1
        ArrayList<Subtask> subtasks = new ArrayList<>();
        int epicId = taskManager.addNewEpic(new Epic("...", "..."));
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = taskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.IN_PROGRESS, 1683166053L, 172800L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        subtaskId = taskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.NEW, 1682906853L, 43200L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        subtaskId = taskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.DONE, 1683029253L, 86400L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        taskManager.getEpic(epicId).countEpicTime();
        System.out.println("Create new epic: " + epicId);
        System.out.println("Information about epic: " + taskManager.getEpic(epicId).toString());


        //Создание эпика 2
        subtasks = new ArrayList<>();
        epicId = taskManager.addNewEpic(new Epic("...", "..."));
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        subtaskId = taskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.DONE, 1677931200L, 43200L));
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