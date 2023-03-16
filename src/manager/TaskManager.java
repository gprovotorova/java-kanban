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
     void deleteByIdTasks(int id);
     void deleteByIdEpic(int id);
     void deleteByIdSubtask(int id);

    //Удаление всех задач 
     void deleteAllTasks();
     void deleteAllEpics();
     void deleteAllSubtasks();

    //Получение списка всех задач 
     ArrayList<Task> getAllTasks();
     ArrayList<Epic> getAllEpics();
     ArrayList<Subtask> getAllSubtasks();

     //Отображение последних просмотренных пользователем задач
     List<Task> getHistory();
}
