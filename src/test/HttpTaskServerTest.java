package test;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.*;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.gson.JsonParser.parseString;
import static java.nio.charset.StandardCharsets.UTF_8;
import static manager.HttpTaskManager.historyFromString;
import static model.Status.IN_PROGRESS;
import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    Gson gson = Managers.getGson();
    static TaskManager taskManager;
    static HttpTaskServer taskServer;

    @BeforeEach
    void BeforeEach() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();

        taskManager.addNewTask(new Task("Task_1", "...", Status.NEW, 1675252800L, 36000L));
        taskManager.addNewTask(new Task("Task_2", "...", Status.DONE, 1675425600L, 36000L));

        List<Subtask> subtasks = new ArrayList<>();
        int epicId = taskManager.addNewEpic(new Epic("Epic_3", "..."));
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = taskManager.addNewSubtask(new Subtask("Subtask_3(4)", "...", epicId, Status.IN_PROGRESS, 1675598400L, 172800L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        subtaskId = taskManager.addNewSubtask(new Subtask("Subtask_3(5)", "...", epicId, Status.NEW, 1675684800L, 172800L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        taskManager.getEpic(epicId).countEpicTime();

        subtasks = new ArrayList<>();
        epicId = taskManager.addNewEpic(new Epic("Epic_6", "..."));
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        subtaskId = taskManager.addNewSubtask(new Subtask("Subtask_4.(7)", "...", epicId, Status.DONE, 1676030400L, 67000L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        taskManager.getEpic(epicId).countEpicTime();
    }

    @AfterEach
    void AfterEach() {
        taskManager.deleteAll();
        taskServer.stop();
    }

    @DisplayName("возвращать задачи с сервера")
    @Test
    void shouldReturnTasks() throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
    }

    @DisplayName("возвращать подзадачи с сервера")
    @Test
    void shouldReturnSubtasks() throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(3, subtasks.size(), "Неверное количество задач.");
    }

    @DisplayName("возвращать эпики с сервера")
    @Test
    void shouldReturnEpics() throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epics = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество задач.");
    }

    @DisplayName("возвращать задачу по id с сервера")
    @Test
    void shouldReturnTaskById() throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String line = response.body().toString().replaceFirst("Задача с идентификатором 1 - ", "");
        Task task = gson.fromJson(line, Task.class);

        assertNotNull(task, "Задача не возвращается.");
        assertEquals(taskManager.getTask(1), task, "Задача не совпадает.");
    }

    @DisplayName("возвращать подзадачу по id с сервера")
    @Test
    void shouldReturnSubtaskById() throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask?id=7");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String line = response.body().toString().replaceFirst("Подзадача с идентификатором 7 - ", "");
        Subtask subtask = gson.fromJson(line, Subtask.class);

        assertNotNull(subtask, "Подзадача не возвращается.");
        assertEquals(taskManager.getSubtask(7), subtask, "Подзадача не совпадает.");
    }

    @DisplayName("возвращать эпик по id с сервера")
    @Test
    void shouldReturnEpicById() throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic?id=6");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());


        String line = response.body().toString().replaceFirst("Эпик с идентификатором 6 - ", "");
        Epic epic = gson.fromJson(line, Epic.class);

        assertNotNull(epic, "Эпик не возвращается.");
        assertEquals(taskManager.getEpic(6), epic, "Эпик не совпадает.");
    }


    @DisplayName("возвращать историю просмотра задач с сервера")
    @Test
    void shouldReturnHistory() throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List <Integer> history = historyFromString(response.body().toString());
        assertNotNull(history, "История пустая.");
        List<Integer> tasksId = List.of(1, 2, 4, 5, 3, 7, 6);
        assertEquals(tasksId.size(), history.size(), "Количество элементов истории не совпадают.");
        for (int i = 0; i < history.size(); i++) {
            assertEquals(tasksId.get(i), history.get(i), "Элементы истории не совпадают.");
        }
    }

    @DisplayName("возвращать задачи в порядке приоритета с сервера")
    @Test
    void shouldReturnPrioritizedTasks() throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String jsonTasksResponse = gson.toJson(response.body());
        assertNotNull(jsonTasksResponse, "Задачи в порядке приоритета не возвращаются.");

        JsonElement jsonElementResponse = parseString(jsonTasksResponse);
        String jsonResponseString = jsonElementResponse.getAsString();

        Map<Instant, Task> tasks = taskManager.getPrioritizedTasks();
        String tasksString = gson.toJson(tasks);

        assertEquals(tasksString, jsonResponseString, "Количество элементов не совпадают.");
    }

    @DisplayName("создавать задачу и возвращать ее id")
    @Test
    void shouldReturnTaskId() throws IOException, InterruptedException{
        Task task = new Task("Task_5", "...", NEW, 1690977600L, 28800L);
        String jsonTask = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonTask)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String check = response.body().toString();
        int taskId = Integer.parseInt(check.toString().replaceFirst("Задача добавлена. Еe id  - ", ""));
        assertTrue(taskId > 0, "Неправильно определяется id задачи.");

        Task newTask = taskManager.getTask(taskId);

        assertNotNull(newTask, "Задачи не возвращаются.");
        assertEquals(task.getName(), newTask.getName(), "Задачи не совпадают.");
    }

    @DisplayName("создавать эпик и возвращать его id")
    @Test
    void shouldReturnEpicId() throws IOException, InterruptedException{
        Epic epic = new Epic("Epic_5", "...");

        String jsonTask = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonTask)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String check = response.body().toString();
        int epicId = Integer.parseInt(check.toString().replaceFirst("Эпик добавлен. Его id  - ", ""));
        assertTrue(epicId > 0, "Неправильно определяется id задачи.");

        Epic newEpic = taskManager.getEpic(epicId);

        assertNotNull(newEpic, "Эпик не возвращается.");
        assertEquals(epic.getName(), newEpic.getName(), "Эпик не совпадает.");
        assertTrue(newEpic.getId()>0, "Неправильно определяется id задачи.");
    }

    @DisplayName("создавать подзадачу и возвращать ее id")
    @Test
    void shouldReturnSubtaskId() throws IOException, InterruptedException{
        Subtask subtask = new Subtask("Subtask_3.3", "...", 3, IN_PROGRESS, 1690977600L, 43200L);
        String jsonSubtask = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonSubtask)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String check = response.body().toString();
        int subtaskId = Integer.parseInt(check.toString().replaceFirst("Подзадача добавлена. Еe id  - ", ""));
        assertTrue(subtaskId > 0, "Неправильно определяется id задачи.");

        Subtask newSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(newSubtask, "Задачи не возвращаются.");
        assertEquals(subtask.getName(), newSubtask.getName(), "Задачи не совпадают.");
    }

    @DisplayName("удалять задачу по id")
    @Test
    void shouldNotReturnTaskById() throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertNull(taskManager.getTask(1), "Задача возвращается после удаления.");

        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Количество задач должно быть меньше.");
    }

    @DisplayName("удалять эпик по id")
    @Test
    void shouldNotReturnEpicById() throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNull(taskManager.getEpic(3), "Задача возвращается после удаления.");

        List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Количество эпиков должно быть меньше.");
    }

    @DisplayName("удалять подзадачу по id")
    @Test
    void shouldNotReturnSubtaskById() throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask?id=7");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNull(taskManager.getSubtask(7), "Задача возвращается после удаления.");

        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Количество подзадач должно быть меньше.");
    }

    @DisplayName("возвращать все подзадачи эпика")
    @Test
    void shouldReturnAllSubtasksOfEpic() throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {
        }.getType());

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Количество подззадач должно быть меньше.");
    }

    @DisplayName("удалять все задачи")
    @Test
    void shouldNotReturnTasks() throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.isEmpty(), "Задачи возвращаются после удаления.");
    }

    @DisplayName("удалять все эпики")
    @Test
    void shouldNotReturnEpics() throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epics = taskManager.getAllEpics();
        assertTrue(epics.isEmpty(), "Эпики возвращаются после удаления.");
    }

    @DisplayName("удалять все подзадачи")
    @Test
    void shouldNotReturnSubtasks() throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertTrue(subtasks.isEmpty(), "Подзадачи возвращаются после удаления.");
    }

}