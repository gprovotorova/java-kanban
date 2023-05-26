package server;

import com.google.gson.Gson;
import constans.Constans;
import manager.*;

import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskServer {
    private HttpServer taskServer;
    private Gson gson;
    private TaskManager taskManager;

    public HttpTaskServer() {
        this.taskManager = Managers.getDefaultTaskManager();
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        taskServer = HttpServer.create(new InetSocketAddress("localhost", Constans.PORT_8075), 0);
        taskServer.createContext("/tasks/", new TaskHandler(taskManager));
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + Constans.PORT_8075);
        System.out.println("Открой в браузере http://localhost:" + Constans.PORT_8075 + "/tasks/");
        taskServer.start();
    }

    public void stop(){
        taskServer.stop(0);
        System.out.println("Остановили сервер на порту " + Constans.PORT_8075);
    }

    public static void main(String[] args) throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
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

        HttpTaskManager httpTaskManager2 = new HttpTaskManager("http://localhost:8078");
        httpTaskManager2 = httpTaskManager2.load();
        System.out.println("1 - " + httpTaskManager.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList()));
        httpTaskManager2.getTask(1);
        System.out.println("2 - " + httpTaskManager2.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList()));

        kvServer.stop();
    }
}

