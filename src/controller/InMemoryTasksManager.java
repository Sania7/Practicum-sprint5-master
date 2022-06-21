package controller;

import base.Epic;
import base.SubTask;
import base.Task;
import exception.TimeErrorException;

import java.util.*;

public class InMemoryTasksManager implements TaskManager {
    protected Map<Integer, Task> taskLists = new HashMap<>(); //Список всех задач

    protected TreeSet<Task> sortedTaskSet = new TreeSet<>((task1, task2) -> {
        if(task1.getStartTime() != null && task2.getStartTime() != null) {
            return task1.getStartTime().compareTo(task2.getStartTime());
        } else if (task1.getStartTime() == null && task2.getStartTime() == null) {
            return task1.getNum().compareTo(task2.getNum());
        } else if (task1.getStartTime() == null) {
            return 1;
        } else return -1;
    });

    // проверка задачи на пересечение по времени
    private void checkTime(Task checkTask) {
        if (checkTask != null) {
            for (Task task : sortedTaskSet) {
                if (task.getNum().equals(checkTask.getNum())) {
                    continue;
                }
                if ((!checkTask.getEndTime().isAfter(task.getStartTime()))) {
                    continue;
                }
                if (!task.getEndTime().isAfter(checkTask.getStartTime())) {
                    continue;
                }
                throw new TimeErrorException("Выявлено пересечение во времени. " + task + checkTask);
            }
        }
    }

    private HistoryManager historyManager = Managers.getDefaultHistory();

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    //Получение списка всех задач (Эпики + Задачи + Подзадачи).
    @Override
    public Map<Integer, Task> getAllTasksList(){
        return new HashMap<>(taskLists);
    }

    //Получение списка задач верхнего уровня (Эпики + Задачи).
    @Override
    public List<Task> getTasksList() {
        ArrayList<Task> result = new ArrayList<>();

        for (Integer key : taskLists.keySet()) {
            if (taskLists.get(key).getClass() != SubTask.class)
                result.add(taskLists.get(key));
        }
        return result;
    }
    //Получение списка всех Эпиков.
    @Override
    public List<Task> getEpics() {
        ArrayList<Task> result = new ArrayList<>();

        for (Task task : taskLists.values()) {
            if (task instanceof Epic)  //Если Эпик - добавить к результату
                result.add(task);
        }
        return result;
    }

    //Получение списка всех подзадач определённого Эпика.
    @Override
    public List<SubTask> getSubTasks(Epic epic){
        return epic.getSubTasks();
    }

    // список задач отсортированных по дате начала
    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return sortedTaskSet;
    }
    //Получение задачи по идентификатору.
    @Override
    public Task getTask(int num) {
        //Сохранение обращения к задаче в истории обращений
        historyManager.add(taskLists.get(num));

        return taskLists.get(num);
    }
    //Добавление новой Задачи, Эпика и Подзадачи. Сам объект должен передаваться в качестве параметра.



    @Override
    public void addTask(Task newTask) {
        if (taskLists.containsKey(newTask.getNum())) {    //Проверка занятости идентификатора
            System.out.println("Задача с номером #" + newTask.getNum() + " уже существует!");
            return;
        }
        if (newTask instanceof SubTask) {
            SubTask subTask = (SubTask) newTask;
            checkTime(subTask);
            sortedTaskSet.add(subTask);
        }
        // Отсутствует добавление в отсортированный по времени список, в этот список добавляют только подзадачи и задачи
        //Если номер задачи не задан вручную - сгенерировать его автоматически
        if (newTask.getNum() == null) {
            newTask.setNum(calcNewNum());
        }
        taskLists.put(newTask.getNum(), newTask);    //Вставить Задачу в список менеджера
        if (newTask instanceof SubTask){
            ((SubTask)newTask).getEpic().getSubTasks().add((SubTask)newTask); //Присоединить Подзадачу к Эпику
            ((SubTask) newTask).getEpic().getStatus(); // обновить статус Эпика
        }
    }

    //Обновление задачи любого типа по идентификатору. Новая версия объекта передаётся в виде параметра.
    //Отсутствует обновление и валидация перед обновлением задачи
    // в отсортированном списке
    @Override
    public void updateTask(Task newTask) {

        Task oldTask = getTask(newTask.getNum());       //Получение изменяемой задачи по идентификатору новой
        taskLists.put(newTask.getNum(), newTask);        //Вставить задачу в список менеджера
        //валидация времени должна происходить только для подзадачи
        // и задачи, далее следует удалить старую
        // версию из отсортированного списка и поместить в него новую

        if (newTask.getClass() != oldTask.getClass()) {
            checkTime(newTask);
            sortedTaskSet.add(newTask);
            System.out.println("Не совпал тип обновляемой задачи(" + oldTask.getClass()
                    + ") с типом задачи для обновления (" + newTask.getClass() + ")!");
            return;
        }
        if (newTask instanceof Epic) {         //Обновление для Эпика
            for (SubTask subTask : ((Epic)oldTask).getSubTasks()) {
                //Заполнение списка Подзадач в новом Эпике
                ((Epic)newTask).getSubTasks().add(subTask);
                //Обновление ссылки на Эпик в старых Подзадачах
                subTask.setEpic((Epic) newTask);
            }
        } else if (newTask instanceof SubTask) { //Обновление для Подзадачи
            SubTask subTask = (SubTask)newTask;
            checkTime(subTask);
            sortedTaskSet.add(subTask);
            subTask.setEpic(((SubTask)oldTask).getEpic());          //Подключить к старому Эпику
            subTask.getEpic().getSubTasks().add(subTask);           //Добавить новую версию к Эпику
            subTask.getEpic().getSubTasks().remove(oldTask);        //Удалить из Эпика старую версию
            ((SubTask) newTask).getEpic().getStatus();              // обновить статус Эпика
        }
    }
    //Удаление ранее добавленных задач — всех и по идентификатору.

    // Отсутствует удаление задачи/подзадачи из отсортированного списка
    @Override
    public void delTask(Integer num) {
        if (num == null) {   //Если идентификатор пустой - удаляем всё
            taskLists.clear();
            historyManager.remove(num);
        } else {
            Task delTask = getTask(num); //Получение экземпляра удаляемой задачи
            if (delTask instanceof SubTask) {
                //Подзадачу - удалить из Эпика
                ((SubTask)delTask).getEpic().getSubTasks().remove(delTask);
                ((SubTask) delTask).getEpic().getStatus();  // обновить статус Эпика
            } else if (delTask instanceof Epic){
                //Подзадачи Эпика тоже нужно удалить из списка
                for (SubTask subTask : ((Epic) delTask).getSubTasks()) {
                    taskLists.remove(subTask.getNum());
                    historyManager.remove(subTask.getNum());
                    sortedTaskSet.remove(subTask); // удалить подзадачу из отсортированного списка
                }
            }
            taskLists.remove(num);
            historyManager.remove(num);
            sortedTaskSet.remove(delTask);
        }
    }

    //Формирование идентификатора задачи
    @Override
    public int calcNewNum() {
        int result = 0;
        //Поиск первого не занятого идентификатора
        for (int i = 1; i <= (taskLists.size() + 1); i++) {
            if (!taskLists.containsKey(i)){
                result = i;
                break;
            }
        }
        return result;
    }

    //Возвращает историю просмотренных задач.
    @Override
    public TreeSet<Task> history() {
        return historyManager.getHistory();
    }
}
