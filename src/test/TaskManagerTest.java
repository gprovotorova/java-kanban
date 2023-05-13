package test;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static model.Status.*;
import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest <T extends TaskManager>{

    T taskManager;

    @Test
    void shouldReturnTaskId() {
        Task task = new Task("Test addNewTask1", "...", Status.NEW, 1681224328L, 28800L);
        int taskId = taskManager.addNewTask(task);

        Task savedTask = taskManager.getTask(taskId);
        List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertTrue(task.getId() > 0, "У задачи неправильно определяется id.");
        assertEquals(3, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void shouldReturnEpicId() {
        //Создание эпика 1
        ArrayList<Subtask> subtasks = new ArrayList<>();
        Epic epic = new Epic("Test addNewEpic1", "...");
        int epicId = taskManager.addNewEpic(epic);
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = taskManager.addNewSubtask(new Subtask("Subtask1", "...", epicId, IN_PROGRESS, 1678276800L, 43200L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        epic.countEpicTime();

        Epic savedEpic = taskManager.getEpic(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        List<Epic> epics = taskManager.getAllEpics();
        assertEquals(3, epics.size(), "Неверное количество эпиков.");
    }

    @Test
    void shouldReturnSubtaskId() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        int epicId = 7;
        Epic epic = taskManager.getEpic(epicId);
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId1 = taskManager.addNewSubtask(new Subtask("Subtask1", "...", epicId, Status.IN_PROGRESS, 1677672000L, 43200L));
        subtasks.add(taskManager.getSubtask(subtaskId1));
        int subtaskId2 = taskManager.addNewSubtask(new Subtask("Subtask2", "...", epicId, Status.NEW, 1677758400L, 43200L));
        subtasks.add(taskManager.getSubtask(subtaskId2));
        int subtaskId3 = taskManager.addNewSubtask(new Subtask("Subtask3", "...", epicId, Status.DONE, 1677844800L, 43200L));
        subtasks.add(taskManager.getSubtask(subtaskId3));
        Subtask subtask = new Subtask("Test addNewSubtask4", "...", epicId, Status.DONE, 1677931200L, 43200L);
        int subtaskId4 = taskManager.addNewSubtask(subtask);
        subtasks.add(taskManager.getSubtask(subtaskId4));
        taskManager.getEpic(epicId).setSubtasks(subtasks);

        assertNotNull(epic, "Эпик не найден.");

        Subtask savedSubtask = taskManager.getSubtask(subtaskId4);
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
    }

    @Test
    void shouldReturnEpicStatus(){
        ArrayList<Subtask> subtasks = new ArrayList<>();
        int epicId = 7;
        Epic epic = taskManager.getEpic(epicId);
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId1 = taskManager.addNewSubtask(new Subtask("Subtask1", "...", epicId, Status.IN_PROGRESS, 1681387200L, 28800L));
        subtasks.add(taskManager.getSubtask(subtaskId1));
        int subtaskId2 = taskManager.addNewSubtask(new Subtask("Subtask2", "...", epicId, Status.NEW, 1681473600L, 28800L));
        subtasks.add(taskManager.getSubtask(subtaskId2));
        int subtaskId3 = taskManager.addNewSubtask(new Subtask("Subtask3", "...", epicId, Status.DONE, 1681819200L, 43200L));
        subtasks.add(taskManager.getSubtask(subtaskId3));
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        taskManager.setEpicStatus(epicId);
        List<Subtask> subtasksOfEpic = taskManager.getAllSubtasksOfEpic(epic);
        assertNotNull(subtasksOfEpic, "У данного эпика нет подзадач.");
        Status status = epic.getStatus();
        assertNotNull(status, "Статус эпика не возвращается.");
        List<Status> statusOfSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasksOfEpic) {
            statusOfSubtasks.add(subtask.getStatus());
        }
        for (int i = 0; i < statusOfSubtasks.size()-1; i++) {
            if(statusOfSubtasks.get(i) != statusOfSubtasks.get(i+1)){
                assertEquals(IN_PROGRESS, status, "Неправильно определяется статус эпика.");
                break;
            }
            if(statusOfSubtasks.get(i).equals(NEW)){
                assertEquals(NEW, status, "Неправильно определяется статус эпика.");
            }
            if(statusOfSubtasks.get(i).equals(DONE)){
                assertEquals(DONE, status, "Неправильно определяется статус эпика.");
            }
        }
    }

    @Test
    void shouldReturnTask() {
        Task task = new Task("Test addNewTask1", "Test addNewTask description", Status.NEW, 1678017600L, 12000456L);
        int taskId3 = taskManager.addNewTask(task);

        List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(3, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(2), "Задачи не совпадают.");
    }

    @Test
    void shouldReturnEpic() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        Epic epic = new Epic("Test addNewEpic1", "...");
        int epicId = taskManager.addNewEpic(epic);
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = taskManager.addNewSubtask(new Subtask("Subtask1", "...", epicId, IN_PROGRESS, 1678017600L, 13769200L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        taskManager.getEpic(epicId).setSubtasks(subtasks);

        List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(3, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(2), "Эпики не совпадают.");
    }

    @Test
    void shouldReturnSubtask() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        int epicId = 3;
        Epic epic = taskManager.getEpic(epicId);
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        Subtask subtask = new Subtask("Subtask4", "...", epicId, Status.DONE, 1677931200L, 43200L);
        int subtaskId4 = taskManager.addNewSubtask(subtask);
        subtasks.add(taskManager.getSubtask(subtaskId4));
        taskManager.getEpic(epicId).setSubtasks(subtasks);

        assertNotNull(epic, "Эпик не найден.");

        List<Subtask> savedSubtasks = taskManager.getAllSubtasks();

        assertNotNull(savedSubtasks, "Подзадачи не возвращаются.");
        assertEquals(5, savedSubtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, savedSubtasks.get(4), "Подзадачи не совпадают.");
    }

    @Test
    void shouldReturnUpdatedTask() {
        int taskId2 = 2;

        Task savedTask = taskManager.getTask(taskId2);
        assertNotNull(savedTask, "Задача не найдена.");

        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");

        savedTask.setDescription("UPDATED");
        taskManager.updateTask(savedTask);
        Task updatedTask = taskManager.getTask(taskId2);

        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(savedTask, updatedTask, "Задача не изменена.");
    }

    @Test
    void shouldReturnUpdatedEpic() {
        int epicId = 3;
        Epic savedEpic = taskManager.getEpic(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");

        savedEpic.setDescription("UPDATED");
        taskManager.updateEpic(savedEpic);
        Epic updatedEpic = taskManager.getEpic(epicId);

        assertEquals(2, epics.size(), "Неверное количество эпиков.");
        assertEquals(savedEpic, updatedEpic, "Эпик не изменен.");
    }

    @Test
    void shouldReturnUpdatedSubtask() {
        int subtaskId = 5;

        List<Subtask> savedSubtasks = taskManager.getAllSubtasks();
        assertNotNull(savedSubtasks, "Подзадачи не возвращаются.");

        Subtask savedSubtask = taskManager.getSubtask(subtaskId);
        assertNotNull(savedSubtask, "Подзадача не найдена.");

        savedSubtask.setDescription("UPDATED");
        taskManager.updateSubtask(savedSubtask);
        Subtask updatedSubtask = taskManager.getSubtask(subtaskId);

        assertEquals(4, savedSubtasks.size(), "Неверное количество подзадач.");
        assertEquals(savedSubtask, updatedSubtask, "Подзадача не изменена.");
    }

    @Test
    void shouldReturnAllTasks() {
        int taskId1 = taskManager.addNewTask(new Task("Task1", "...", Status.NEW, 1678449600L, 28800L));
        int taskId2 = taskManager.addNewTask(new Task("Task2", "...", Status.NEW, 1678536000L, 28800L));
        int taskId3 = taskManager.addNewTask(new Task("Task3", "...", Status.NEW, 1678622400L, 28800L));

        List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(5, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void shouldReturnAllEpics() {
        //Создание эпика 1
        ArrayList<Subtask> subtasks = new ArrayList<>();
        int epicId1 = taskManager.addNewEpic(new Epic("Epic1", "..."));
        taskManager.getEpic(epicId1).setSubtasks(subtasks);

        //Создание эпика 2
        subtasks = new ArrayList<>();
        Epic epic2 = new Epic("Epic2", "...");
        int epicId2 = taskManager.addNewEpic(epic2);
        taskManager.getEpic(epicId2).setSubtasks(subtasks);

        //Создание эпика 3
        subtasks = new ArrayList<>();
        int epicId3 = taskManager.addNewEpic(new Epic("Epic3", "..."));
        taskManager.getEpic(epicId3).setSubtasks(subtasks);

        List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(5, epics.size(), "Неверное количество эпиков.");
    }

    @Test
    void shouldReturnAllSubtasks() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        Epic epic = new Epic("Epic1", "...");
        int epicId = taskManager.addNewEpic(epic);
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId1 = taskManager.addNewSubtask(new Subtask("Subtask1", "...", epicId, Status.IN_PROGRESS, 1678449600L, 28800L));
        subtasks.add(taskManager.getSubtask(subtaskId1));
        int subtaskId2 = taskManager.addNewSubtask(new Subtask("Subtask2", "...", epicId, Status.NEW, 1678536000L, 28800L));
        subtasks.add(taskManager.getSubtask(subtaskId2));
        int subtaskId3 = taskManager.addNewSubtask(new Subtask("Subtask3", "...", epicId, Status.DONE, 1678622400L, 28800L));
        subtasks.add(taskManager.getSubtask(subtaskId3));
        Subtask subtask = new Subtask("Subtask4", "...", epicId, Status.DONE, 1682251200L, 67000L);
        int subtaskId4 = taskManager.addNewSubtask(subtask);
        subtasks.add(taskManager.getSubtask(subtaskId4));
        taskManager.getEpic(epicId).setSubtasks(subtasks);

        List<Subtask> savedSubtasks = taskManager.getAllSubtasks();

        assertNotNull(savedSubtasks, "Подзадачи не возвращаются.");
        assertEquals(8, savedSubtasks.size(), "Неверное количество подзадач.");
    }

    @Test
    void shouldReturnHistory() {
        //Вызов задач, подзадач и эпика
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getTask(2);
        taskManager.getSubtask(4);
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getSubtask(6);

        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не пустая.");
        List<Integer> tasksId = List.of(5, 8, 7, 2, 4, 1, 3, 6);
        assertEquals(tasksId.size(), history.size(), "Количество элементов истории не совпадают.");
        for (int i = 0; i < history.size(); i++) {
            assertEquals(tasksId.get(i), history.get(i).getId(), "Элементы истории не совпадают.");
        }
    }

    @Test
    void shouldNotReturnTask() {
        Task task1 = new Task("Task1", "...", Status.NEW, 1681224328L, 28800L);
        int taskId1 = taskManager.addNewTask(task1);
        int taskId2 = taskManager.addNewTask(new Task("Task2", "...", Status.NEW, 1682424000L, 129600L));
        int taskId3 = taskManager.addNewTask(new Task("Task3", "...", Status.NEW, 1683234000L, 120000L));

        taskManager.deleteByIdTask(taskId1);

        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");

        assertEquals(4, tasks.size(), "Количество задач должно быть меньше.");
        assertFalse(tasks.contains(task1), "Задача не должна возвращаться.");
    }

    @Test
    void shouldNotReturnEpic() {
        int epicId = 3;
        Epic epic = taskManager.getEpic(epicId);

        taskManager.deleteByIdEpic(epicId);
        assertNotNull(epic, "Эпик возвращается после удаления.");

        List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Количество эпиков должно быть меньше.");
        assertFalse(epics.contains(epic), "Эпик не должен возвращаться.");
    }

    @Test
    void shouldNotReturnSubtask() {
        int subtaskId5 = 5;
        Subtask subtask = taskManager.getSubtask(subtaskId5);

        taskManager.deleteByIdSubtask(subtaskId5);

        List<Subtask> savedSubtasks = taskManager.getAllSubtasks();
        assertNotNull(savedSubtasks, "Подзадачи не возвращаются.");

        assertEquals(3, savedSubtasks.size(), "Количество подзадач должно быть меньше.");
        assertFalse(savedSubtasks.contains(subtask), "Подзадача не должна возвращаться.");
    }

    @Test
    void shouldReturnAllSubtasksOfEpic(){
        int epicId = 3;
        Epic epic = taskManager.getEpic(epicId);

        List<Subtask> savedSubtasks = taskManager.getAllSubtasksOfEpic(epic);
        assertNotNull(savedSubtasks, "Подзадачи не возвращаются.");
        assertEquals(3, savedSubtasks.size(), "Неверное количество подзадач.");
    }

    @Test
    void shouldNotReturnTasks() {
        List<Task> tasksBeforeDelete = taskManager.getAllTasks();
        assertNotNull(tasksBeforeDelete, "Задачи не возвращаются.");

        taskManager.deleteAllTasks();

        List<Task> tasksAfterDelete = taskManager.getAllTasks();
        assertEquals(0, tasksAfterDelete.size(), "В списке не должно быть задач.");
    }

    @Test
    void shouldNotReturnEpics() {
        List<Epic> epicsBeforeDelete = taskManager.getAllEpics();
        List<Subtask> subtasksBeforeDelete = taskManager.getAllSubtasks();
        assertNotNull(epicsBeforeDelete, "Эпики не возвращаются.");
        assertNotNull(subtasksBeforeDelete, "Подзадачи не возвращаются.");

        taskManager.deleteAllEpics();

        List<Epic> epicsAfterDelete = taskManager.getAllEpics();
        List<Subtask> subtasksAfterDelete = taskManager.getAllSubtasks();
        assertEquals(0, epicsAfterDelete.size(), "В списке не должно быть эпиков.");
        assertEquals(0, subtasksAfterDelete.size(), "В списке не должно быть подзадач.");
    }

    @Test
    void shouldNotReturnSubtasks() {
        List<Subtask> subtasksBeforeDelete = taskManager.getAllSubtasks();
        assertNotNull(subtasksBeforeDelete, "Подзадачи не возвращаются.");

        taskManager.deleteAllSubtasks();

        List<Subtask> subtasksAfterDelete = taskManager.getAllSubtasks();
        assertEquals(0, subtasksAfterDelete.size(), "В списке не должно быть подзадач.");
    }
}

