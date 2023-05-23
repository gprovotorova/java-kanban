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
        taskServer.createContext("/tasks/", this::handle);
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

        public void handle(HttpExchange httpExchange) {
            try {
                String path = httpExchange.getRequestURI().getPath();
                String uri = httpExchange.getRequestURI().toString();
                if(uri.contains("id")){
                    String pam = uri.substring(path.length()+1).replaceFirst("id=", "");
                    path = path.substring(1) + "/" + pam;
                } else {
                    path = path.substring(1);
                }
                String[] pathParts = path.split("/");
                String method = httpExchange.getRequestMethod();
                switch(method){
                    case "GET":{
                        if(pathParts.length == 1){
                            Map<Instant, Task> tasks = taskManager.getPrioritizedTasks();
                            String jsonTasks = gson.toJson(tasks);
                            writeResponse(httpExchange, jsonTasks, 200);
                            break;
                        }
                        if(pathParts.length == 4){
                            int id = parsePathId(pathParts[3]);
                            if(id != -1) {
                                List<Subtask> subtasksOfEpic = taskManager.getAllSubtasksOfEpic(taskManager.getEpic(id));
                                String jsonSubtasksOfEpic = gson.toJson(subtasksOfEpic);
                                writeResponse(httpExchange, jsonSubtasksOfEpic, 200);
                                break;
                            } else {
                                writeResponse(httpExchange, "Получен некорректный идентификатор.", 405);
                                break;
                            }
                        }
                        if(pathParts.length == 3){
                            handleGetTaskById(httpExchange, path);
                            break;
                        }
                        if(pathParts.length == 2 && pathParts[1].equals("history")){
                            List<Integer> history = taskManager.getHistory().stream()
                                    .map(Task::getId)
                                    .collect(Collectors.toList());
                            String jsonHistory = gson.toJson(history);
                            writeResponse(httpExchange, jsonHistory, 200);
                            break;
                        } else {
                            handleGetTask(httpExchange, path);
                            break;
                        }
                    }
                    case "POST":{
                        handleAddOrUpdateTask(httpExchange, path);
                        break;
                    }
                    case "DELETE":{
                        if(pathParts.length == 3){
                            handleDeleteTaskById(httpExchange, path);
                            break;
                        }
                        if(pathParts.length == 2){
                            handleDeleteTask(httpExchange, path);
                            break;
                        }
                    }
                    default:
                        writeResponse(httpExchange, "Такого эндпоинта не существует", 404);
                }
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                httpExchange.close();
            }
        }

        private void handleDeleteTaskById(HttpExchange httpExchange, String path) throws IOException{
            String[] pathParts = path.split("/");
            if(pathParts[1].equals("task")) {
                int id = parsePathId(pathParts[2]);
                if (id != -1) {
                    taskManager.deleteByIdTask(id);
                    writeResponse(httpExchange, "Задача с идентификатором " + id + " удалена", 200);
                } else {
                    writeResponse(httpExchange, "Получен некорректный идентификатор.", 405);
                }
            }
            if(pathParts[1].equals("subtask")){
                int id = parsePathId(pathParts[2]);
                if(id != -1){
                    taskManager.deleteByIdSubtask(id);
                    writeResponse(httpExchange, "Подзадача с идентификатором " + id + " удалена", 200);
                } else {
                    writeResponse(httpExchange, "Получен некорректный идентификатор.", 405);
                }
            }
            if(pathParts[1].equals("epic")) {
                int id = parsePathId(pathParts[2]);
                if (id != -1) {
                    taskManager.deleteByIdEpic(id);
                    writeResponse(httpExchange, "Эпик с идентификатором " + id + " удален", 200);
                } else {
                    writeResponse(httpExchange, "Получен некорректный идентификатор.", 405);
                }
            }
        }

        private void handleDeleteTask(HttpExchange httpExchange, String path) throws IOException{
            String[] pathParts = path.split("/");
            if(pathParts[1].equals("task")){
                taskManager.deleteAllTasks();
                writeResponse(httpExchange, "Задачи удалены", 200);
            } else if(pathParts[1].equals("subtask")){
                taskManager.deleteAllSubtasks();
                writeResponse(httpExchange, "Подзадачи удалены", 200);
            } else if(pathParts[1].equals("epic")){
                taskManager.deleteAllEpics();
                writeResponse(httpExchange, "Эпики удалены", 200);
            } else {
                writeResponse(httpExchange, "Такого эндпоинта не существует", 404);
            }
        }

        private void handleAddOrUpdateTask(HttpExchange httpExchange, String path) throws IOException{
            String[] pathParts = path.split("/");
            if(pathParts[1].equals("task")){
                InputStreamReader streamReader = new InputStreamReader(httpExchange.getRequestBody(), DEFAULT_CHARSET);
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                String body = bufferedReader.lines().collect(Collectors.joining ("\n"));
                try {
                    Task task = gson.fromJson(body, Task.class);
                } catch (JsonSyntaxException e){
                    writeResponse(httpExchange, "Получен некорректный JSON", 400);
                }
                Task task = gson.fromJson(body, Task.class);
                List<Task> savedTasks = taskManager.getAllTasks();
                if(savedTasks.contains(task)){
                    taskManager.updateTask(task);
                    writeResponse(httpExchange, "Задача обновлена", 200);
                } else {
                    int taskId = taskManager.addNewTask(task);
                    writeResponse(httpExchange, "Задача добавлена. Еe id  - " + taskId, 200);
                    return;
                }
                writeResponse(httpExchange, "Такого эндпоинта не существует", 404);
            }
            if(pathParts[1].equals("subtask")){
                InputStreamReader streamReader = new InputStreamReader(httpExchange.getRequestBody(), DEFAULT_CHARSET);
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                String body = bufferedReader.lines().collect(Collectors.joining ("\n"));
                try {
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                } catch (JsonSyntaxException e){
                    writeResponse(httpExchange, "Получен некорректный JSON", 400);
                }
                Subtask subtask = gson.fromJson(body, Subtask.class);
                List<Subtask> savedSubtasks = taskManager.getAllSubtasks();
                if(savedSubtasks.contains(subtask)){
                    taskManager.updateSubtask(subtask);
                    writeResponse(httpExchange, "Подзадача обновлена", 200);
                } else {
                    int subtaskId = taskManager.addNewSubtask(subtask);
                    writeResponse(httpExchange, "Подзадача добавлена. Еe id  - " + subtaskId, 200);
                    return;
                }
                writeResponse(httpExchange, "Такого эндпоинта не существует", 404);
            }
            if(pathParts[1].equals("epic")){
                InputStreamReader streamReader = new InputStreamReader(httpExchange.getRequestBody(), DEFAULT_CHARSET);
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                String body = bufferedReader.lines().collect(Collectors.joining ("\n"));
                try {
                    Epic epic = gson.fromJson(body, Epic.class);
                } catch (JsonSyntaxException e){
                    writeResponse(httpExchange, "Получен некорректный JSON", 400);
                }
                Epic epic = gson.fromJson(body, Epic.class);
                List<Epic> savedEpics = taskManager.getAllEpics();
                if(savedEpics.contains(epic)){
                    taskManager.updateEpic(epic);
                    writeResponse(httpExchange, "Эпик обновлен.", 200);
                } else {
                    int epicId = taskManager.addNewEpic(epic);
                    writeResponse(httpExchange, "Эпик добавлен. Его id  - " + epicId, 200);
                    return;
                }
                writeResponse(httpExchange, "Такого эндпоинта не существует", 404);
            }
        }

        private void handleGetTaskById(HttpExchange httpExchange, String path) throws IOException{
            String[] pathParts = path.split("/");
            if(pathParts[1].equals("task")){
                int id = parsePathId(pathParts[2]);
                if(id != -1){
                    Task task = taskManager.getTask(id);
                    String jsonTask = gson.toJson(task);
                    writeResponse(httpExchange, "Задача с идентификатором " + id + " - " + jsonTask, 200);
                } else {
                    writeResponse(httpExchange, "Получен некорректный идентификатор.", 405);
                }
            }
            if(pathParts[1].equals("subtask")) {
                int id = parsePathId(pathParts[2]);
                if (id != -1) {
                    Subtask subtask = taskManager.getSubtask(id);
                    String jsonSubtask = gson.toJson(subtask);
                    writeResponse(httpExchange, "Подзадача с идентификатором " + id + " - " + jsonSubtask, 200);
                } else {
                    writeResponse(httpExchange, "Получен некорректный идентификатор.", 405);
                }
            }
            if(pathParts[1].equals("epic")) {
                int id = parsePathId(pathParts[2]);
                if (id != -1) {
                    Epic epic = taskManager.getEpic(id);
                    String jsonEpic = gson.toJson(epic);
                    writeResponse(httpExchange, "Эпик с идентификатором " + id + " - " + jsonEpic, 200);
                } else {
                    writeResponse(httpExchange, "Получен некорректный идентификатор.", 405);
                }
            }
        }

        private void handleGetTask(HttpExchange httpExchange, String path) throws IOException {
            String[] pathParts = path.split("/");
            if(pathParts[1].equals("task")){
                List<Task> tasks = taskManager.getAllTasks();
                String jsonTasks = gson.toJson(tasks);
                writeResponse(httpExchange, jsonTasks, 200);
                return;
            } else if(pathParts[1].equals("subtask")){
                List<Subtask> subtasks = taskManager.getAllSubtasks();
                String jsonSubtasks = gson.toJson(subtasks);
                writeResponse(httpExchange, jsonSubtasks, 200);
                return;
            } else if(pathParts[1].equals("epic")){
                List<Epic> epics = taskManager.getAllEpics();
                String jsonEpics = gson.toJson(epics);
                writeResponse(httpExchange, jsonEpics, 200);
                return;
            } else {
                writeResponse(httpExchange, "Такого эндпоинта не существует", 404);
            }
        }

        private int parsePathId(String path){
            try{
                return Integer.parseInt(path);
            } catch (NumberFormatException exception){
                return -1;
            }
        }

        private void writeResponse(HttpExchange exchange,
                                   String responseString,
                                   int responseCode) throws IOException {
            if(responseString.isBlank()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {
                byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
                exchange.sendResponseHeaders(responseCode, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
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


        HttpTaskManager httpTaskManager2 = HttpTaskManager.load();
        System.out.println("1 - " + httpTaskManager.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList()));
        httpTaskManager2.getTask(1);
        System.out.println("2 - " + httpTaskManager2.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList()));
        //server.stop();
    }
}

