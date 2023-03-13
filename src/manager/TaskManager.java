package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

interface TaskManager {

    //Создание задачи
    int addNewTask(Task task);

    //Создание эпика
    int addNewEpic(Epic epic);
    //Создание подзадачи
     int addNewSubtask(Subtask subtask);

    //Получение по идентификатору 
     Task getTask(int id);
     Epic getEpic(int id);
     Subtask getSubtask(int id);

    //Обновление 
     void updateTask(Task task);
     void updateEpic(Epic epic);
     void updateSubtask(Subtask subtask);

    //Удаление по идентификатору 
     void deleteByIDTasks(int id);
     void deleteByIDEpic(int id);
     void deleteByIDSubtask(int id);

    //Удаление всех задач 
     void deleteAllTasks();
     void deleteAllEpics();
     void deleteAllSubtasks();

    //Получение списка всех задач 
     ArrayList<String> getAllTasks();
     ArrayList<String> getAllEpics();
     ArrayList<String> getAllSubtasks();

     //Отображение последних просмотренных пользователем задач
     List<Task> getHistory();
}
