package test;

import manager.HistoryManager;
import manager.InMemoryTaskManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest <T extends HistoryManager>{
    private InMemoryTaskManager taskManager;

    @BeforeEach
    public void BeforeEach(){
        taskManager = new InMemoryTaskManager();
        taskManager.addNewTask(new Task("...", "...", Status.NEW, 1685620800L, 28800L));
        taskManager.addNewTask(new Task("...", "...", Status.DONE, 1685793600L, 28800L));
        taskManager.addNewTask(new Task("...", "...", Status.IN_PROGRESS, 1685966400L, 28800L));
        taskManager.addNewTask(new Task("...", "...", Status.NEW, 1686225600L, 28800L));
        taskManager.addNewTask(new Task("...", "...", Status.DONE, 1686312000L, 28800L));
    }

    @AfterEach
    public void AfterEach(){
        taskManager.deleteAll();
    }

    @DisplayName("добавлять задачу в двусвязный список и определять ссылки на предыдущий и последующий элемент")
    @Test
    void addTask() {
        Task task = new Task("...", "...", Status.DONE, 1686398400L, 28800L);
        int taskId = taskManager.addNewTask(task);

        List<Task> savedTasks = taskManager.memoryHistoryManager.getAll();
        assertNotNull(savedTasks, "Задачи не возвращаются.");
        assertTrue(savedTasks.contains(task), "Задача не добавлена.");

        taskManager.getTask(3);
        taskManager.getTask(1);
        taskManager.getTask(taskId);
        List<Integer> tasksId = List.of(2, 4, 5, 3, 1, 6);
        assertEquals(tasksId.size(), taskManager.memoryHistoryManager.getAll().size(), "Количество элементов истории не совпадают.");
        for (int i = 0; i < taskManager.memoryHistoryManager.getAll().size(); i++) {
            assertEquals(tasksId.get(i), taskManager.memoryHistoryManager.getAll().get(i).getId(), "Элементы истории не совпадают.");
        }
    }

    @DisplayName("удалять задачу из двусвязного списка и переопределять ссылки на предыдущий и последующий элемент")
    @Test
    void removeTask() {
        taskManager.deleteByIdTask(3);
        List<Integer> order1 = List.of(1, 2, 4, 5);
        assertEquals(order1.size(), taskManager.memoryHistoryManager.getAll().size(), "Количество элементов истории не совпадают.");
        for (int i = 0; i < taskManager.memoryHistoryManager.getAll().size(); i++) {
            assertEquals(order1.get(i), taskManager.memoryHistoryManager.getAll().get(i).getId(), "Элементы истории не совпадают.");
        }

        taskManager.deleteByIdTask(1);
        List<Integer> order2 = List.of(2, 4, 5);
        assertEquals(order2.size(), taskManager.memoryHistoryManager.getAll().size(), "Количество элементов истории не совпадают.");
        for (int i = 0; i < taskManager.memoryHistoryManager.getAll().size(); i++) {
            assertEquals(order2.get(i), taskManager.memoryHistoryManager.getAll().get(i).getId(), "Элементы истории не совпадают.");
        }

        taskManager.deleteByIdTask(5);
        List<Integer> order3 = List.of(2, 4);
        assertEquals(order3.size(), taskManager.memoryHistoryManager.getAll().size(), "Количество элементов истории не совпадают.");
        for (int i = 0; i < taskManager.memoryHistoryManager.getAll().size(); i++) {
            assertEquals(order3.get(i), taskManager.memoryHistoryManager.getAll().get(i).getId(), "Элементы истории не совпадают.");
        }
    }

    @DisplayName("возвращать историю просмотра задач")
    @Test
    void getAll() {
        List<Task> savedTasks = taskManager.memoryHistoryManager.getAll();
        assertNotNull(savedTasks, "Задачи не возвращаются.");
        List<Integer> order = List.of(1, 2, 3, 4, 5);
        assertEquals(order.size(), savedTasks.size(), "Количество элементов истории не совпадают.");
        for (int i = 0; i < savedTasks.size(); i++) {
            assertEquals(order.get(i), savedTasks.get(i).getId(), "Элементы истории не совпадают.");
        }

        taskManager.memoryHistoryManager.cleanViewHistory();
        assertTrue(taskManager.memoryHistoryManager.getAll().isEmpty(), "История не очистилась.");
    }
}