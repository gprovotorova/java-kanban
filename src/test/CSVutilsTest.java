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

import static org.junit.jupiter.api.Assertions.*;

class CSVutilsTest {
    private static File file = new File("check.csv");
    private FileBackedTasksManager fileTaskManager;

    @BeforeEach
    public void BeforeEach(){
        fileTaskManager = new FileBackedTasksManager(file);

        fileTaskManager.addNewTask(new Task("...", "...", Status.NEW, 1687521600L, 28800L));
        fileTaskManager.addNewTask(new Task("...", "...", Status.NEW, 1687608000L, 28800L));

        ArrayList<Subtask> subtasks = new ArrayList<>();
        int epicId = fileTaskManager.addNewEpic(new Epic("...", "..."));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        int subtaskId = fileTaskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.IN_PROGRESS, 1687694400L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        subtaskId = fileTaskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.NEW, 1687780800L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        subtaskId = fileTaskManager.addNewSubtask(new Subtask("...", "...", epicId, Status.DONE, 1687867200L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        fileTaskManager.getEpic(epicId).countEpicTime();

        subtasks = new ArrayList<>();
        epicId = fileTaskManager.addNewEpic(new Epic("EPIC 2", "..."));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        subtaskId = fileTaskManager.addNewSubtask(new Subtask("SUBTASK 4", "...", epicId, Status.DONE, 1687953600L, 28800L));
        subtasks.add(fileTaskManager.getSubtask(subtaskId));
        fileTaskManager.getEpic(epicId).setSubtasks(subtasks);
        fileTaskManager.getEpic(epicId).countEpicTime();
    }

    @DisplayName("сохранять информацию в файл")
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