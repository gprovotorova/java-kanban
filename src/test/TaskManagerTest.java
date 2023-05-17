package test;

import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static model.Status.*;
import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest <T extends TaskManager>{

    T taskManager;

    @DisplayName("создавать задачу и возвращать ее id")
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

    @DisplayName("создавать эпик и возвращать его id")
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

    @DisplayName("создавать подзадачу и возвращать ее id")
    @Test
    void shouldReturnSubtaskId() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        int epicId = 7;
        Epic epic = taskManager.getEpic(epicId);
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = taskManager.addNewSubtask(new Subtask("Subtask1", "...", epicId, Status.IN_PROGRESS, 1677672000L, 43200L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        subtaskId = taskManager.addNewSubtask(new Subtask("Subtask2", "...", epicId, Status.NEW, 1677758400L, 43200L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        subtaskId = taskManager.addNewSubtask(new Subtask("Subtask3", "...", epicId, Status.DONE, 1677844800L, 43200L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        Subtask subtask = new Subtask("Test addNewSubtask4", "...", epicId, Status.DONE, 1677931200L, 43200L);
        subtaskId = taskManager.addNewSubtask(subtask);
        subtasks.add(taskManager.getSubtask(subtaskId));
        taskManager.getEpic(epicId).setSubtasks(subtasks);

        assertNotNull(epic, "Эпик не найден.");

        Subtask savedSubtask = taskManager.getSubtask(subtaskId);
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
    }

    @DisplayName("вычислять статус эпика")
    @Test
    void shouldReturnEpicStatus(){
        ArrayList<Subtask> subtasks = new ArrayList<>();
        int epicId = 7;
        Epic epic = taskManager.getEpic(epicId);
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = taskManager.addNewSubtask(new Subtask("Subtask1", "...", epicId, Status.IN_PROGRESS, 1681387200L, 28800L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        subtaskId = taskManager.addNewSubtask(new Subtask("Subtask2", "...", epicId, Status.NEW, 1681473600L, 28800L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        subtaskId = taskManager.addNewSubtask(new Subtask("Subtask3", "...", epicId, Status.DONE, 1681819200L, 43200L));
        subtasks.add(taskManager.getSubtask(subtaskId));
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

    @DisplayName("возвращать задачу")
    @Test
    void shouldReturnTask() {
        Task task = new Task("Test addNewTask1", "Test addNewTask description", Status.NEW, 1678017600L, 12000456L);
        taskManager.addNewTask(task);

        List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(3, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(2), "Задачи не совпадают.");
    }

    @DisplayName("возвращать эпик")
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

    @DisplayName("возвращать подзадачу")
    @Test
    void shouldReturnSubtask() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        int epicId = 3;
        Epic epic = taskManager.getEpic(epicId);
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        Subtask subtask = new Subtask("Subtask4", "...", epicId, Status.DONE, 1677931200L, 43200L);
        int subtaskId = taskManager.addNewSubtask(subtask);
        subtasks.add(taskManager.getSubtask(subtaskId));
        taskManager.getEpic(epicId).setSubtasks(subtasks);

        assertNotNull(epic, "Эпик не найден.");

        List<Subtask> savedSubtasks = taskManager.getAllSubtasks();

        assertNotNull(savedSubtasks, "Подзадачи не возвращаются.");
        assertEquals(5, savedSubtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, savedSubtasks.get(4), "Подзадачи не совпадают.");
    }

    @DisplayName("обновлять задачу и возвращать ее")
    @Test
    void shouldReturnUpdatedTask() {
        int taskId = 2;

        Task savedTask = taskManager.getTask(taskId);
        assertNotNull(savedTask, "Задача не найдена.");

        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");

        savedTask.setDescription("UPDATED");
        taskManager.updateTask(savedTask);
        Task updatedTask = taskManager.getTask(taskId);

        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(savedTask, updatedTask, "Задача не изменена.");
    }

    @DisplayName("обновлять эпик и возвращать его")
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

    @DisplayName("обновлять подзадачу и возвращать ее")
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

    @DisplayName("возвращать все сохраненные задачи")
    @Test
    void shouldReturnAllTasks() {
        taskManager.addNewTask(new Task("Task1", "...", Status.NEW, 1678449600L, 28800L));
        taskManager.addNewTask(new Task("Task2", "...", Status.NEW, 1678536000L, 28800L));
        taskManager.addNewTask(new Task("Task3", "...", Status.NEW, 1678622400L, 28800L));

        List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(5, tasks.size(), "Неверное количество задач.");
    }

    @DisplayName("возвращать все сохраненные эпики")
    @Test
    void shouldReturnAllEpics() {
        //Создание эпика 1
        ArrayList<Subtask> subtasks = new ArrayList<>();
        int epicId = taskManager.addNewEpic(new Epic("Epic1", "..."));
        taskManager.getEpic(epicId).setSubtasks(subtasks);

        //Создание эпика 2
        subtasks = new ArrayList<>();
        epicId = taskManager.addNewEpic(new Epic("Epic2", "..."));
        taskManager.getEpic(epicId).setSubtasks(subtasks);

        //Создание эпика 3
        subtasks = new ArrayList<>();
        epicId = taskManager.addNewEpic(new Epic("Epic3", "..."));
        taskManager.getEpic(epicId).setSubtasks(subtasks);

        List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(5, epics.size(), "Неверное количество эпиков.");
    }

    @DisplayName("возвращать все сохраненные подзадачи")
    @Test
    void shouldReturnAllSubtasks() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        Epic epic = new Epic("Epic1", "...");
        int epicId = taskManager.addNewEpic(epic);
        taskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = taskManager.addNewSubtask(new Subtask("Subtask1", "...", epicId, Status.IN_PROGRESS, 1678449600L, 28800L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        subtaskId = taskManager.addNewSubtask(new Subtask("Subtask2", "...", epicId, Status.NEW, 1678536000L, 28800L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        subtaskId = taskManager.addNewSubtask(new Subtask("Subtask3", "...", epicId, Status.DONE, 1678622400L, 28800L));
        subtasks.add(taskManager.getSubtask(subtaskId));
        Subtask subtask = new Subtask("Subtask4", "...", epicId, Status.DONE, 1682251200L, 67000L);
        subtaskId = taskManager.addNewSubtask(subtask);
        subtasks.add(taskManager.getSubtask(subtaskId));
        taskManager.getEpic(epicId).setSubtasks(subtasks);

        List<Subtask> savedSubtasks = taskManager.getAllSubtasks();

        assertNotNull(savedSubtasks, "Подзадачи не возвращаются.");
        assertEquals(8, savedSubtasks.size(), "Неверное количество подзадач.");
    }

    @DisplayName("возвращать историю просмотра")
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

    @DisplayName("удалять задачу по id")
    @Test
    void shouldNotReturnTask() {
        Task task = new Task("Task1", "...", Status.NEW, 1681224328L, 28800L);
        int taskId = taskManager.addNewTask(task);
        taskManager.addNewTask(new Task("Task2", "...", Status.NEW, 1682424000L, 129600L));
        taskManager.addNewTask(new Task("Task3", "...", Status.NEW, 1683234000L, 120000L));

        taskManager.deleteByIdTask(taskId);

        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");

        assertEquals(4, tasks.size(), "Количество задач должно быть меньше.");
        assertFalse(tasks.contains(task), "Задача не должна возвращаться.");
    }

    @DisplayName("удалять эпик по id")
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

    @DisplayName("удалять подзадачу по id")
    @Test
    void shouldNotReturnSubtask() {
        int subtaskId = 5;
        Subtask subtask = taskManager.getSubtask(subtaskId);

        taskManager.deleteByIdSubtask(subtaskId);

        List<Subtask> savedSubtasks = taskManager.getAllSubtasks();
        assertNotNull(savedSubtasks, "Подзадачи не возвращаются.");

        assertEquals(3, savedSubtasks.size(), "Количество подзадач должно быть меньше.");
        assertFalse(savedSubtasks.contains(subtask), "Подзадача не должна возвращаться.");
    }

    @DisplayName("возвращать все подзадачи эпика")
    @Test
    void shouldReturnAllSubtasksOfEpic(){
        int epicId = 3;
        Epic epic = taskManager.getEpic(epicId);

        List<Subtask> savedSubtasks = taskManager.getAllSubtasksOfEpic(epic);
        assertNotNull(savedSubtasks, "Подзадачи не возвращаются.");
        assertEquals(3, savedSubtasks.size(), "Неверное количество подзадач.");
    }

    @DisplayName("удалять все задачи")
    @Test
    void shouldNotReturnTasks() {
        List<Task> tasksBeforeDelete = taskManager.getAllTasks();
        assertNotNull(tasksBeforeDelete, "Задачи не возвращаются.");

        taskManager.deleteAllTasks();

        List<Task> tasksAfterDelete = taskManager.getAllTasks();
        assertEquals(0, tasksAfterDelete.size(), "В списке не должно быть задач.");
    }

    @DisplayName("удалять все эпики")
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

    @DisplayName("удалять все подзадачи")
    @Test
    void shouldNotReturnSubtasks() {
        List<Subtask> subtasksBeforeDelete = taskManager.getAllSubtasks();
        assertNotNull(subtasksBeforeDelete, "Подзадачи не возвращаются.");

        taskManager.deleteAllSubtasks();

        List<Subtask> subtasksAfterDelete = taskManager.getAllSubtasks();
        assertEquals(0, subtasksAfterDelete.size(), "В списке не должно быть подзадач.");
    }
}

