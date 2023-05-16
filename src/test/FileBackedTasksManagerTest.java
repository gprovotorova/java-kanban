package test;

import manager.FileBackedTasksManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.CSVutils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static model.Status.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileBackedTasksManager должен")
class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager>{

    private static File file = new File("check.csv");
    FileBackedTasksManager fileTaskManager;

    @BeforeEach
    public void BeforeEach(){
        fileTaskManager = new FileBackedTasksManager(file);

        fileTaskManager.addNewTask(new Task("...", "...", Status.NEW, 1682812800L, 180000L));
        fileTaskManager.addNewTask(new Task("...", "...", Status.NEW, 1683425253L, 129600L));

        ArrayList<Subtask> subtasks = new ArrayList<>();
        int epicId = fileTaskManager.addNewEpic(new Epic("...", "..."));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = fileTaskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.IN_PROGRESS, 1683166053L, 172800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        subtaskId = fileTaskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.NEW, 1682906853L, 43200L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        subtaskId = fileTaskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.DONE, 1683029253L, 86400L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        fileTaskManager.getEpic(epicId).countEpicTime();

        subtasks = new ArrayList<>();
        epicId = fileTaskManager.addNewEpic(new Epic("EPIC 2", "..."));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        subtaskId = fileTaskManager.addNewSubtask(new Subtask("SUBTASK 4", "...", epicId, Status.DONE, 1684029700L, 67000L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        fileTaskManager.getEpic(epicId).countEpicTime();
    }

    @DisplayName("создавать задачу и возвращать ее id")
    @Test
    void shouldReturnTaskId() {
        Task task = new Task("Test addNewTask1", "...", Status.NEW, 1681224328L, 28800L);
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
        int subtaskId = fileTaskManager.addNewSubtask(new Subtask("Subtask1", "...", epicId, IN_PROGRESS, 1678276800L, 43200L));
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
        int subtaskId1 = fileTaskManager.addNewSubtask(new Subtask("Subtask1", "...", epicId, Status.IN_PROGRESS, 1677672000L, 43200L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId1));
        int subtaskId2 = fileTaskManager.addNewSubtask(new Subtask("Subtask2", "...", epicId, Status.NEW, 1677758400L, 43200L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId2));
        int subtaskId3 = fileTaskManager.addNewSubtask(new Subtask("Subtask3", "...", epicId, Status.DONE, 1677844800L, 43200L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId3));
        Subtask subtask = new Subtask("Test addNewSubtask4", "...", epicId, Status.DONE, 1677931200L, 43200L);
        int subtaskId4 = fileTaskManager.addNewSubtask(subtask);
        subtasks.add(fileTaskManager.getSubtask(subtaskId4));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);

        assertNotNull(epic, "Эпик не найден.");

        Subtask savedSubtask = fileTaskManager.getSubtask(subtaskId4);
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
        int subtaskId1 = fileTaskManager.addNewSubtask(new Subtask("Subtask1", "...", epicId, Status.IN_PROGRESS, 1681387200L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId1));
        int subtaskId2 = fileTaskManager.addNewSubtask(new Subtask("Subtask2", "...", epicId, Status.NEW, 1681473600L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId2));
        int subtaskId3 = fileTaskManager.addNewSubtask(new Subtask("Subtask3", "...", epicId, Status.DONE, 1681819200L, 43200L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId3));
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
        Task task = new Task("Test addNewTask1", "Test addNewTask description", Status.NEW, 1678017600L, 12000456L);
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
        int subtaskId = fileTaskManager.addNewSubtask(new Subtask("Subtask1", "...", epicId, IN_PROGRESS, 1678017600L, 13769200L));
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
        Subtask subtask = new Subtask("Subtask4", "...", epicId, Status.DONE, 1677931200L, 43200L);
        int subtaskId4 = fileTaskManager.addNewSubtask(subtask);
        subtasks.add(fileTaskManager.getSubtask(subtaskId4));
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
        int taskId2 = 2;

        Task savedTask = fileTaskManager.getTask(taskId2);
        assertNotNull(savedTask, "Задача не найдена.");

        List<Task> tasks = fileTaskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");

        savedTask.setDescription("UPDATED");
        fileTaskManager.updateTask(savedTask);
        Task updatedTask = fileTaskManager.getTask(taskId2);

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
        fileTaskManager.addNewTask(new Task("Task1", "...", Status.NEW, 1678449600L, 28800L));
        fileTaskManager.addNewTask(new Task("Task2", "...", Status.NEW, 1678536000L, 28800L));
        fileTaskManager.addNewTask(new Task("Task3", "...", Status.NEW, 1678622400L, 28800L));

        List<Task> tasks = fileTaskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(5, tasks.size(), "Неверное количество задач.");
    }

    @DisplayName("возвращать все сохраненные эпики")
    @Test
    void shouldReturnAllEpics() {
        //Создание эпика 1
        ArrayList<Subtask> subtasks = new ArrayList<>();
        int epicId1 = fileTaskManager.addNewEpic(new Epic("Epic1", "..."));
        fileTaskManager.getEpic(epicId1).setSubtasks(subtasks);

        //Создание эпика 2
        subtasks = new ArrayList<>();
        Epic epic2 = new Epic("Epic2", "...");
        int epicId2 = fileTaskManager.addNewEpic(epic2);
        fileTaskManager.getEpic(epicId2).setSubtasks(subtasks);

        //Создание эпика 3
        subtasks = new ArrayList<>();
        int epicId3 = fileTaskManager.addNewEpic(new Epic("Epic3", "..."));
        fileTaskManager.getEpic(epicId3).setSubtasks(subtasks);

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
        int subtaskId1 = fileTaskManager.addNewSubtask(new Subtask("Subtask1", "...", epicId, Status.IN_PROGRESS, 1678449600L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId1));
        int subtaskId2 = fileTaskManager.addNewSubtask(new Subtask("Subtask2", "...", epicId, Status.NEW, 1678536000L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId2));
        int subtaskId3 = fileTaskManager.addNewSubtask(new Subtask("Subtask3", "...", epicId, Status.DONE, 1678622400L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId3));
        Subtask subtask = new Subtask("Subtask4", "...", epicId, Status.DONE, 1682251200L, 67000L);
        int subtaskId4 = fileTaskManager.addNewSubtask(subtask);
        subtasks.add(fileTaskManager.getSubtask(subtaskId4));
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
        Task task1 = new Task("Task1", "...", Status.NEW, 1681224328L, 28800L);
        int taskId1 = fileTaskManager.addNewTask(task1);
        fileTaskManager.addNewTask(new Task("Task2", "...", Status.NEW, 1682424000L, 129600L));
        fileTaskManager.addNewTask(new Task("Task3", "...", Status.NEW, 1683234000L, 120000L));

        fileTaskManager.deleteByIdTask(taskId1);

        List<Task> tasks = fileTaskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");

        assertEquals(4, tasks.size(), "Количество задач должно быть меньше.");
        assertFalse(tasks.contains(task1), "Задача не должна возвращаться.");
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
        int subtaskId5 = 5;
        Subtask subtask = fileTaskManager.getSubtask(subtaskId5);

        fileTaskManager.deleteByIdSubtask(subtaskId5);

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

        FileBackedTasksManager manager2 = fileTaskManager.loadFromFile(new File("check.csv"));
        List<Task> tasks = fileTaskManager.getAllTasks();
        assertNotNull(tasks, "Подзадачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество подзадач.");
        List<Subtask> subtasks = fileTaskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(4, subtasks.size(), "Неверное количество подзадач.");
        List<Epic> epics = fileTaskManager.getAllEpics();
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