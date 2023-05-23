package repository;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskData;
import manager.TaskType;

public class CSVTaskRepository implements TaskRepository {
    private final String fileName;

    public CSVTaskRepository(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public TaskData load() {
        List<String> lines;
        try {
            lines = Files.readAllLines(Path.of(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }

        List<Task> tasks = new ArrayList<>();
        for (String line : lines) {
            final Task task = parseTask(line);
            tasks.add(task);
            if (line.isEmpty()) {
                break;
            }
        }

        List<Integer> history = parseHistory(lines.get(lines.size() - 1));
        return new TaskData(tasks, history);
    }

    @Override
    public void save(TaskData taskData) {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, StandardCharsets.UTF_8))) {
            // TODO  Заголовок id,type,name,status,description,epic
            for (Task task : taskData.getTasks()) {
                writer.append(taskToLine(task));
                writer.newLine();
            }
            writer.newLine();
            writer.write(historyToLine(taskData.getHistory()));
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error in " + fileName, e); // TODO ManagerSaveException
        }
    }

    private List<Integer> parseHistory(String line) {
        return null;
    }

    private String historyToLine(List<Integer> history) {
        return "";
    }

    private Task parseTask(String line) {
        String[] columns = line.split(",");
        TaskType type = TaskType.valueOf(columns[1]);
        Task task = null;
        switch (type) {
            case TASK:
                task = new Task();
                break;

            case SUBTASK:
                task = new Subtask();
                break;

            case EPIC:
                task = new Epic();
                break;
        }

        return task;
    }


    private String taskToLine(Task task) {
        return "null";
    }
}
