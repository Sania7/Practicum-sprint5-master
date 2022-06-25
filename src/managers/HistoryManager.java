package managers;

import base.Task;

import java.util.List;
import java.util.TreeSet;

public interface HistoryManager {


    //Добавление нового просмотра задачи
    void add(Task task);

    //Удаление просмотра из истории
    void remove(int id);

    //Получение истории последних просмотров
    List<Task> getHistory();
}
