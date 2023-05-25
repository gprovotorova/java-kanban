package manager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import constans.Constans;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskHandler implements HttpHandler {
    private Gson gson;
    private TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = Managers.getGson();
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
            InputStreamReader streamReader = new InputStreamReader(httpExchange.getRequestBody(), Constans.DEFAULT_CHARSET);
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
            InputStreamReader streamReader = new InputStreamReader(httpExchange.getRequestBody(), Constans.DEFAULT_CHARSET);
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
            InputStreamReader streamReader = new InputStreamReader(httpExchange.getRequestBody(), Constans.DEFAULT_CHARSET);
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
        } else if(pathParts[1].equals("subtask")){
            List<Subtask> subtasks = taskManager.getAllSubtasks();
            String jsonSubtasks = gson.toJson(subtasks);
            writeResponse(httpExchange, jsonSubtasks, 200);
        } else if(pathParts[1].equals("epic")){
            List<Epic> epics = taskManager.getAllEpics();
            String jsonEpics = gson.toJson(epics);
            writeResponse(httpExchange, jsonEpics, 200);
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
            byte[] bytes = responseString.getBytes(Constans.DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }
}
