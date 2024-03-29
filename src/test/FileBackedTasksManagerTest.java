package test;

import manager.FileBackedTasksManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.CSVutils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static model.Status.*;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager>{

    private static File file = new File("check.csv");
    private FileBackedTasksManager fileTaskManager;

    @BeforeEach
    public void BeforeEach(){
        fileTaskManager = new FileBackedTasksManager(file);

        fileTaskManager.addNewTask(new Task("...", "...", Status.NEW, 1688212800L, 28800L));
        fileTaskManager.addNewTask(new Task("...", "...", Status.DONE, 1688299200L, 28800L));

        ArrayList<Subtask> subtasks = new ArrayList<>();
        int epicId = fileTaskManager.addNewEpic(new Epic("...", "..."));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = fileTaskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.IN_PROGRESS, 1688385600L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        subtaskId = fileTaskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.NEW, 1688472000L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        subtaskId = fileTaskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.DONE, 1688558400L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        fileTaskManager.getEpic(epicId).countEpicTime();

        subtasks = new ArrayList<>();
        epicId = fileTaskManager.addNewEpic(new Epic("EPIC 2", "..."));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        subtaskId = fileTaskManager.addNewSubtask(new Subtask("SUBTASK 4", "...", epicId, Status.DONE, 1688644800L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        fileTaskManager.getEpic(epicId).countEpicTime();
    }

    @AfterEach
    public void AfterEach(){
        fileTaskManager.deleteAll();
    }

    @DisplayName("создавать задачу и возвращать ее id")
    @Test
    void shouldReturnTaskId() {
        Task task = new Task("Test addNewTask1", "...", Status.NEW, 1688731200L, 28800L);
        int taskId = fileTaskManager.addNewTask(task);

        Task savedTask = fileTaskManager.getTask(taskId);
        List<Task> tasks = fileTaskManager.getAllTasks();

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
        int epicId = fileTaskManager.addNewEpic(epic);
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = fileTaskManager.addNewSubtask(new Subtask("Subtask1", "...", epicId, IN_PROGRESS, 1688817600L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        epic.countEpicTime();

        Epic savedEpic = fileTaskManager.getEpic(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        List<Epic> epics = fileTaskManager.getAllEpics();
        assertEquals(3, epics.size(), "Неверное количество эпиков.");
    }

    @DisplayName("создавать подзадачу и возвращать ее id")
    @Test
    void shouldReturnSubtaskId() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        int epicId = 7;
        Epic epic = fileTaskManager.getEpic(epicId);
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = fileTaskManager.addNewSubtask(new Subtask("Subtask1", "...", epicId, Status.IN_PROGRESS, 1688904000L, 43200L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        subtaskId = fileTaskManager.addNewSubtask(new Subtask("Subtask2", "...", epicId, Status.NEW, 1688990400L, 43200L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        subtaskId = fileTaskManager.addNewSubtask(new Subtask("Subtask3", "...", epicId, Status.DONE, 1689076800L, 43200L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        Subtask subtask = new Subtask("Test addNewSubtask4", "...", epicId, Status.DONE, 1689163200L, 43200L);
        subtaskId = fileTaskManager.addNewSubtask(subtask);
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);

        assertNotNull(epic, "Эпик не найден.");

        Subtask savedSubtask = fileTaskManager.getSubtask(subtaskId);
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
    }

    @DisplayName("вычислять статус эпика")
    @Test
    void shouldReturnEpicStatus(){
        ArrayList<Subtask> subtasks = new ArrayList<>();
        int epicId = 7;
        Epic epic = fileTaskManager.getEpic(epicId);
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = fileTaskManager.addNewSubtask(new Subtask("Subtask1", "...", epicId, Status.IN_PROGRESS, 1689249600L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        subtaskId = fileTaskManager.addNewSubtask(new Subtask("Subtask2", "...", epicId, Status.NEW, 1689336000L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        subtaskId = fileTaskManager.addNewSubtask(new Subtask("Subtask3", "...", epicId, Status.DONE, 1689422400L, 43200L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        fileTaskManager.setEpicStatus(epicId);
        List<Subtask> subtasksOfEpic = fileTaskManager.getAllSubtasksOfEpic(epic);
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
        Task task = new Task("Test addNewTask1", "Test addNewTask description", Status.NEW, 1689508800L, 28800L);
        fileTaskManager.addNewTask(task);

        List<Task> tasks = fileTaskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(3, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(2), "Задачи не совпадают.");
    }

    @DisplayName("возвращать эпик")
    @Test
    void shouldReturnEpic() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        Epic epic = new Epic("Test addNewEpic1", "...");
        int epicId = fileTaskManager.addNewEpic(epic);
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = fileTaskManager.addNewSubtask(new Subtask("Subtask1", "...", epicId, IN_PROGRESS, 1689595200L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);

        List<Epic> epics = fileTaskManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(3, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(2), "Эпики не совпадают.");
    }

    @DisplayName("возвращать подзадачу")
    @Test
    void shouldReturnSubtask() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        int epicId = 3;
        Epic epic = fileTaskManager.getEpic(epicId);
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        Subtask subtask = new Subtask("Subtask4", "...", epicId, Status.DONE, 1689681600L, 43200L);
        int subtaskId = fileTaskManager.addNewSubtask(subtask);
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);

        assertNotNull(epic, "Эпик не найден.");

        List<Subtask> savedSubtasks = fileTaskManager.getAllSubtasks();

        assertNotNull(savedSubtasks, "Подзадачи не возвращаются.");
        assertEquals(5, savedSubtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, savedSubtasks.get(4), "Подзадачи не совпадают.");
    }

    @DisplayName("обновлять задачу и возвращать ее")
    @Test
    void shouldReturnUpdatedTask() {
        int taskId = 2;

        Task savedTask = fileTaskManager.getTask(taskId);
        assertNotNull(savedTask, "Задача не найдена.");

        List<Task> tasks = fileTaskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");

        savedTask.setDescription("UPDATED");
        fileTaskManager.updateTask(savedTask);
        Task updatedTask = fileTaskManager.getTask(taskId);

        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(savedTask, updatedTask, "Задача не изменена.");
    }

    @DisplayName("обновлять эпик и возвращать его")
    @Test
    void shouldReturnUpdatedEpic() {
        int epicId = 3;
        Epic savedEpic = fileTaskManager.getEpic(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        List<Epic> epics = fileTaskManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");

        savedEpic.setDescription("UPDATED");
        fileTaskManager.updateEpic(savedEpic);
        Epic updatedEpic = fileTaskManager.getEpic(epicId);

        assertEquals(2, epics.size(), "Неверное количество эпиков.");
        assertEquals(savedEpic, updatedEpic, "Эпик не изменен.");
    }

    @DisplayName("обновлять подзадачу и возвращать ее")
    @Test
    void shouldReturnUpdatedSubtask() {
        int subtaskId = 5;

        List<Subtask> savedSubtasks = fileTaskManager.getAllSubtasks();
        assertNotNull(savedSubtasks, "Подзадачи не возвращаются.");

        Subtask savedSubtask = fileTaskManager.getSubtask(subtaskId);
        assertNotNull(savedSubtask, "Подзадача не найдена.");

        savedSubtask.setDescription("UPDATED");
        fileTaskManager.updateSubtask(savedSubtask);
        Subtask updatedSubtask = fileTaskManager.getSubtask(subtaskId);

        assertEquals(4, savedSubtasks.size(), "Неверное количество подзадач.");
        assertEquals(savedSubtask, updatedSubtask, "Подзадача не изменена.");
    }

    @DisplayName("возвращать все сохраненные задачи")
    @Test
    void shouldReturnAllTasks() {
        fileTaskManager.addNewTask(new Task("Task1", "...", Status.NEW, 1689768000L, 28800L));
        fileTaskManager.addNewTask(new Task("Task2", "...", Status.NEW, 1689854400L, 28800L));
        fileTaskManager.addNewTask(new Task("Task3", "...", Status.NEW, 1689940800L, 28800L));

        List<Task> tasks = fileTaskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(5, tasks.size(), "Неверное количество задач.");
    }

    @DisplayName("возвращать все сохраненные эпики")
    @Test
    void shouldReturnAllEpics() {
        //Создание эпика 1
        ArrayList<Subtask> subtasks = new ArrayList<>();
        int epicId = fileTaskManager.addNewEpic(new Epic("Epic1", "..."));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);

        //Создание эпика 2
        subtasks = new ArrayList<>();
        Epic epic = new Epic("Epic2", "...");
        epicId = fileTaskManager.addNewEpic(epic);
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);

        //Создание эпика 3
        subtasks = new ArrayList<>();
        epicId = fileTaskManager.addNewEpic(new Epic("Epic3", "..."));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);

        List<Epic> epics = fileTaskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(5, epics.size(), "Неверное количество эпиков.");
    }

    @DisplayName("возвращать все сохраненные подзадачи")
    @Test
    void shouldReturnAllSubtasks() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        Epic epic = new Epic("Epic1", "...");
        int epicId = fileTaskManager.addNewEpic(epic);
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = fileTaskManager.addNewSubtask(new Subtask("Subtask1", "...", epicId, Status.IN_PROGRESS, 1690027200L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        subtaskId = fileTaskManager.addNewSubtask(new Subtask("Subtask2", "...", epicId, Status.NEW, 1690113600L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        subtaskId = fileTaskManager.addNewSubtask(new Subtask("Subtask3", "...", epicId, Status.DONE, 1690200000L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        Subtask subtask = new Subtask("Subtask4", "...", epicId, Status.DONE, 1690286400L, 67000L);
        subtaskId = fileTaskManager.addNewSubtask(subtask);
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);

        List<Subtask> savedSubtasks = fileTaskManager.getAllSubtasks();

        assertNotNull(savedSubtasks, "Подзадачи не возвращаются.");
        assertEquals(8, savedSubtasks.size(), "Неверное количество подзадач.");
    }

    @DisplayName("возвращать историю просмотра")
    @Test
    void shouldReturnHistory() {
        //Вызов задач, подзадач и эпика
        fileTaskManager.getTask(1);
        fileTaskManager.getEpic(3);
        fileTaskManager.getTask(2);
        fileTaskManager.getSubtask(4);
        fileTaskManager.getTask(1);
        fileTaskManager.getEpic(3);
        fileTaskManager.getSubtask(6);

        List<Task> history = fileTaskManager.getHistory();
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
        Task task = new Task("Task1", "...", Status.NEW, 1690372800L, 28800L);
        int taskId = fileTaskManager.addNewTask(task);
        fileTaskManager.addNewTask(new Task("Task2", "...", Status.NEW, 1690459200L, 28800L));
        fileTaskManager.addNewTask(new Task("Task3", "...", Status.NEW, 1690545600L, 28800L));

        fileTaskManager.deleteByIdTask(taskId);

        List<Task> tasks = fileTaskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");

        assertEquals(4, tasks.size(), "Количество задач должно быть меньше.");
        assertFalse(tasks.contains(task), "Задача не должна возвращаться.");
    }

    @DisplayName("удалять эпик по id")
    @Test
    void shouldNotReturnEpic() {
        int epicId = 3;
        Epic epic = fileTaskManager.getEpic(epicId);

        fileTaskManager.deleteByIdEpic(epicId);
        assertNotNull(epic, "Эпик возвращается после удаления.");

        List<Epic> epics = fileTaskManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Количество эпиков должно быть меньше.");
        assertFalse(epics.contains(epic), "Эпик не должен возвращаться.");
    }

    @DisplayName("удалять подзадачу по id")
    @Test
    void shouldNotReturnSubtask() {
        int subtaskId = 5;
        Subtask subtask = fileTaskManager.getSubtask(subtaskId);

        fileTaskManager.deleteByIdSubtask(subtaskId);

        List<Subtask> savedSubtasks = fileTaskManager.getAllSubtasks();
        assertNotNull(savedSubtasks, "Подзадачи не возвращаются.");

        assertEquals(3, savedSubtasks.size(), "Количество подзадач должно быть меньше.");
        assertFalse(savedSubtasks.contains(subtask), "Подзадача не должна возвращаться.");
    }

    @DisplayName("возвращать все подзадачи эпика")
    @Test
    void shouldReturnAllSubtasksOfEpic(){
        int epicId = 3;
        Epic epic = fileTaskManager.getEpic(epicId);

        List<Subtask> savedSubtasks = fileTaskManager.getAllSubtasksOfEpic(epic);
        assertNotNull(savedSubtasks, "Подзадачи не возвращаются.");
        assertEquals(3, savedSubtasks.size(), "Неверное количество подзадач.");
    }

    @DisplayName("удалять все задачи")
    @Test
    void shouldNotReturnTasks() {
        List<Task> tasksBeforeDelete = fileTaskManager.getAllTasks();
        assertNotNull(tasksBeforeDelete, "Задачи не возвращаются.");

        fileTaskManager.deleteAllTasks();

        List<Task> tasksAfterDelete = fileTaskManager.getAllTasks();
        assertEquals(0, tasksAfterDelete.size(), "В списке не должно быть задач.");
    }

    @DisplayName("удалять все эпики")
    @Test
    void shouldNotReturnEpics() {
        List<Epic> epicsBeforeDelete = fileTaskManager.getAllEpics();
        List<Subtask> subtasksBeforeDelete = fileTaskManager.getAllSubtasks();
        assertNotNull(epicsBeforeDelete, "Эпики не возвращаются.");
        assertNotNull(subtasksBeforeDelete, "Подзадачи не возвращаются.");

        fileTaskManager.deleteAllEpics();

        List<Epic> epicsAfterDelete = fileTaskManager.getAllEpics();
        List<Subtask> subtasksAfterDelete = fileTaskManager.getAllSubtasks();
        assertEquals(0, epicsAfterDelete.size(), "В списке не должно быть эпиков.");
        assertEquals(0, subtasksAfterDelete.size(), "В списке не должно быть подзадач.");
    }

    @DisplayName("удалять все подзадачи")
    @Test
    void shouldNotReturnSubtasks() {
        List<Subtask> subtasksBeforeDelete = fileTaskManager.getAllSubtasks();
        assertNotNull(subtasksBeforeDelete, "Подзадачи не возвращаются.");

        fileTaskManager.deleteAllSubtasks();

        List<Subtask> subtasksAfterDelete = fileTaskManager.getAllSubtasks();
        assertEquals(0, subtasksAfterDelete.size(), "В списке не должно быть подзадач.");
    }

    @DisplayName("восстанавливать данные из файла")
    @Test
    void shouldReturnAllTasksFromFile(){
        CSVutils.save(fileTaskManager);

        FileBackedTasksManager newFileTaskManager = FileBackedTasksManager.loadFromFile(new File("check.csv"));
        List<Task> tasks = newFileTaskManager.getAllTasks();
        assertNotNull(tasks, "Подзадачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество подзадач.");
        List<Subtask> subtasks = newFileTaskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(4, subtasks.size(), "Неверное количество подзадач.");
        List<Epic> epics = newFileTaskManager.getAllEpics();
        assertNotNull(epics, "Подзадачи не возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество подзадач.");

        List<Task> savedTasks = new ArrayList<>();
        savedTasks.addAll(tasks);
        savedTasks.addAll(subtasks);
        savedTasks.addAll(epics);
        for(Task savedTask : savedTasks){
            assertTrue(savedTask.getId() > 0, "У задачи неправильно определяется id.");
        }
    }
}