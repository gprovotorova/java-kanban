package test;

import manager.HttpTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.*;
import server.KVServer;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest {
    
    public static KVServer server;
    public static HttpTaskManager manager;

    @BeforeAll
    public static void BeforeAll() {
        try {
            server = new KVServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.start();
        manager = new HttpTaskManager("http://localhost:8078");

        manager.addNewTask(new Task("Task_1", "...", Status.NEW, 1687262400L, 28800L));
        manager.addNewTask(new Task("Task_2", "...", Status.NEW, 1687348800L, 28800L));

        List <Subtask >subtasks = new ArrayList<>();
        int epicId = manager.addNewEpic(new Epic("EPIC_3", "..."));
        manager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = manager.addNewSubtask(new Subtask("SUBTASK_4", "...", epicId, Status.DONE, 1687435200L, 28800L));
        subtasks.add(manager.getSubtask(subtaskId));
        manager.getEpic(epicId).setSubtasks(subtasks);
        manager.getEpic(epicId).countEpicTime();
    }

    @DisplayName("должен сохранять данные на сервер и восстанавливать")
    @Test
    void shouldSaveAndLoad() {
        manager.save();

        HttpTaskManager managerLoad = HttpTaskManager.load();

        Map<Integer, Task> tasks = manager.mergeAllTasks();
        Map<Integer, Task> tasksLoad = managerLoad.mergeAllTasks();
        assertEquals(tasks, tasksLoad, "Задачи восстановлены неправильно.");

        List<Task> history = manager.getHistory();
        List<Task> historyLoad = managerLoad.getHistory();
        assertEquals(history, historyLoad, "История восстановлена неправильно.");

        Map<Instant, Task> tasksPrioritized = manager.getPrioritizedTasks();
        Map<Instant, Task> tasksPrioritizedLoad = managerLoad.getPrioritizedTasks();
        assertEquals(tasksPrioritized, tasksPrioritizedLoad, "История восстановлена неправильно.");
    }
}