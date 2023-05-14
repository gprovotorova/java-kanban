package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
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
    void deleteByIdTask(int id);
    void deleteByIdEpic(int id);
    void deleteByIdSubtask(int id);

    //Удаление всех задач 
    void deleteAllTasks();
    void deleteAllEpics();
    void deleteAllSubtasks();

    //Получение списка всех задач 
    List<Task> getAllTasks();
    List<Epic> getAllEpics();
    List<Subtask> getAllSubtasks();

    //Отображение последних просмотренных пользователем задач
    List<Task> getHistory();
    //Возвращение всех подзадач конкретного эпика
    List<Subtask> getAllSubtasksOfEpic(Epic epic);
    //Определение статуса эпика
    void setEpicStatus(int epicId);
}
