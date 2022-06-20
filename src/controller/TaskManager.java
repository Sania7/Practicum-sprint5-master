package controller;

import base.Epic;
import base.SubTask;
import base.Task;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public interface TaskManager {
    //Получение списка всех задач (Эпики + Задачи + Подзадачи).
    Map<Integer, Task> getAllTasksList();

    //Получение списка задач (Эпики + Задачи).
    List<Task> getTasksList();

    //Получение списка всех Эпиков.
    List<Task> getEpics();

    //Получение списка всех подзадач определённого эпика.
    List<SubTask> getSubTasks(Epic epic);

    //Получение списка всех задач отсортированных по приоритету (дате начала)
    TreeSet<Task> getPrioritizedTasks();


    //Получение задачи по идентификатору.
    Task getTask(int num);

    //Добавление новой Задачи, Эпика и Подзадачи. Сам объект должен передаваться в качестве параметра.
    void addTask(Task newTask);

    //Обновление задачи любого типа по идентификатору. Новая версия объекта передаётся в виде параметра.
    void updateTask(Task newTask);

    //Удаление ранее добавленных задач — всех и по идентификатору.
    void delTask(Integer num);

    //Формирование идентификатора задачи
    int calcNewNum();

    //Возвращает последние 10 просмотренных задач.
    // (полученных через getTask(), изменённых updateTask() или удалённых delTask()).
    TreeSet<Task> history();
}
