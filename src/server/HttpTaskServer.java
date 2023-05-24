package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.*;

import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private HttpServer taskServer;
    private Gson gson;
    private TaskManager taskManager;

    public HttpTaskServer() {
        this.taskManager = Managers.getDefaultTaskManager();
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        taskServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        taskServer.createContext("/tasks/", new TaskHandler(taskManager));
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/tasks/");
        taskServer.start();
    }

    public void stop(){
        taskServer.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    public static void main(String[] args) throws IOException {
        /*InMemoryTaskManager manager = new InMemoryTaskManager();
        HttpTaskServer server = new HttpTaskServer(manager);

        manager.addNewTask(new Task("Task_1", "...", Status.NEW, 1682812800L, 180000L));
        manager.addNewTask(new Task("Task_2", "...", Status.NEW, 1683425253L, 129600L));

        List<Subtask> subtasks = new ArrayList<>();
        int epicId = manager.addNewEpic(new Epic("Epic_3", "..."));
        manager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = manager.addNewSubtask(new Subtask("Subtask_3.1", "...", epicId, Status.IN_PROGRESS, 1683166053L, 172800L));
        subtasks.add(manager.getSubtask(subtaskId));
        subtaskId = manager.addNewSubtask(new Subtask("Subtask_3.2", "...", epicId, Status.NEW, 1682906853L, 43200L));
        subtasks.add(manager.getSubtask(subtaskId));
        subtaskId = manager.addNewSubtask(new Subtask("Subtask_3.3", "...", epicId, Status.DONE, 1683029253L, 86400L));
        subtasks.add(manager.getSubtask(subtaskId));
        manager.getEpic(epicId).setSubtasks(subtasks);
        manager.getEpic(epicId).countEpicTime();

        subtasks = new ArrayList<>();
        epicId = manager.addNewEpic(new Epic("EPIC_4", "..."));
        manager.getEpic(epicId).setSubtasks(subtasks);
        subtaskId = manager.addNewSubtask(new Subtask("SUBTASK_4.1", "...", epicId, Status.DONE, 1684029700L, 67000L));
        subtasks.add(manager.getSubtask(subtaskId));
        manager.getEpic(epicId).setSubtasks(subtasks);
        manager.getEpic(epicId).countEpicTime();

        server.start();
        server.stop();
        */

        HttpTaskManager httpTaskManager = new HttpTaskManager("http://localhost:8078");
        httpTaskManager.addNewTask(new Task("Task_1", "...", Status.NEW, 1682812800L, 180000L));

        List<Subtask> subtasks = new ArrayList<>();
        int epicId = httpTaskManager.addNewEpic(new Epic("Epic_2", "..."));
        httpTaskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = httpTaskManager.addNewSubtask(new Subtask("Subtask_2.1", "...", epicId, Status.IN_PROGRESS, 1683166053L, 172800L));
        subtasks.add(httpTaskManager.getSubtask(subtaskId));
        subtaskId = httpTaskManager.addNewSubtask(new Subtask("Subtask_3.2", "...", epicId, Status.NEW, 1682906853L, 43200L));
        subtasks.add(httpTaskManager.getSubtask(subtaskId));
        httpTaskManager.getEpic(epicId).setSubtasks(subtasks);
        httpTaskManager.getEpic(epicId).countEpicTime();

        HttpTaskManager httpTaskManager2 = new HttpTaskManager();
        httpTaskManager2 = httpTaskManager2.load();
        System.out.println("1 - " + httpTaskManager.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList()));
        httpTaskManager2.getTask(1);
        System.out.println("2 - " + httpTaskManager2.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList()));
    }
}

