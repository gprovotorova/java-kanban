package utils;

import exceptions.ManagerSaveException;
import manager.FileBackedTasksManager;
import manager.InMemoryHistoryManager;
import model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSVutils {

    public CSVutils(File file, FileBackedTasksManager fileManager, InMemoryHistoryManager historyManager) {
        this.file = file;
        this.fileManager = fileManager;
        this.historyManager = historyManager;
    }

    public CSVutils(File file, InMemoryHistoryManager historyManager) {
        this.file = file;
        this.historyManager = historyManager;
    }

    private static FileBackedTasksManager fileManager;
    private static InMemoryHistoryManager historyManager;
    private File file;

    public static FileBackedTasksManager loadFromFile(File file){
        List<String> info = new ArrayList<>();
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))){
            while(fileReader.ready()){
                String line = fileReader.readLine();
                if(!line.isEmpty()){
                    info.add(line);
                }
            }
            fileReader.close();
            info.remove(0);
            int index = info.size()-1;
            String history = info.get(index);
            info.remove(index);
            for(String line : info){
                manager.fromString(line);
            }
            List <Integer> loadHistory = manager.historyFromString(history);
            manager.reloadHistory(loadHistory);
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время чтения файла.");
        }
        return manager;
    }


    public void save() {
        try {

            Writer fileWriter = new FileWriter("history.csv");
            fileWriter.write("id,type,name,status,description,epic");
            fileWriter.write("\n");
            HashMap<Integer, Task> savedTasks = fileManager.mergeAllTasks();
            for (Integer key : savedTasks.keySet()) {
                Task task = savedTasks.get(key);
                fileWriter.write(fileManager.toString(task) + "\n");
            }
            fileWriter.write("\n");
            fileWriter.write(fileManager.historyToString(historyManager));
            fileWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла.");
        }

    }


}
