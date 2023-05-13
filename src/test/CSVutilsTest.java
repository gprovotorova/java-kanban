package test;

import manager.FileBackedTasksManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.CSVutils;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CSVutilsTest {
    private static File file = new File("check.csv");
    FileBackedTasksManager fileTaskManager;

    @BeforeEach
    public void BeforeEach(){
        fileTaskManager = new FileBackedTasksManager(file);

        int taskId1 = fileTaskManager.addNewTask(new Task("...", "...", Status.NEW, 1682812800L, 180000L));
        int taskId2 = fileTaskManager.addNewTask(new Task("...", "...", Status.NEW, 1683425253L, 129600L));

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

    @Test
    void save() {
        fileTaskManager.getEpic(3);
        fileTaskManager.getTask(1);
        fileTaskManager.getSubtask(6);
        fileTaskManager.getSubtask(4);
        CSVutils.save(fileTaskManager);
        assertTrue((file.length() != 0), "Файл пуст.");
    }
}