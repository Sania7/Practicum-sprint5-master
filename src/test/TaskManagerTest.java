package test;

import base.Epic;
import base.SubTask;
import base.Task;
import managers.TaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// класс тестирования менеджеров задач
abstract class TaskManagerTest<T extends TaskManager> {

    TaskManager taskManager;    //Получение менеджера задач
    Task task;
    Epic epic;
    SubTask subTask1;
    SubTask subTask2;

    //Проверка функции получения списка всех задач (Эпики + Задачи + Подзадачи).
    @Test
    void getAllTasksList(){ //HashMap<Integer, Task> getAllTasksList();
        //b. С пустым списком задач.
        assertDoesNotThrow(()->taskManager.getAllTasksList(),
                "Запрос пустого списка задач не должен вызывать исключений!");

        //a. Со стандартным поведением.
        task = new Task(100,"Задача 1", "Задача для наполнения менеджера");
        taskManager.addTask(task);
        assertEquals(1, taskManager.getAllTasksList().size(), "Количество задач в менеджере не верно!");
    }

    //Проверка функции получения списка задач (Эпики + Задачи).
    @Test
    void getTasksList(){    //ArrayList<Task> getTasksList();
        //b. С пустым списком задач.
        assertDoesNotThrow(()->taskManager.getTasksList(),
                "Запрос пустого списка задач не должен вызывать исключений!");

        //a. Со стандартным поведением.
        task = new Task(100,"Задача 1", "Задача для наполнения менеджера задач");
        taskManager.addTask(task);
        assertEquals(1, taskManager.getTasksList().size(), "Количество задач в менеджере не верно!");
    }

    //Проверка функции получения отсортированного списка всех задач
    //public TreeSet<Task> getPrioritizedTasks(){
    @Test
    void getPrioritizedTasks(){
        assertNotNull(taskManager.getPrioritizedTasks(), "Список задач не пустой!");

        epic = new Epic(200,"Эпик 1", "Пустой эпик");
        taskManager.addTask(epic);

        epic = new Epic(300,"Эпик 2", "Эпик с подзадачами");
        taskManager.addTask(epic);

        subTask1 = new SubTask("Собрать коробки",
                "Коробки на чердаке",
                epic,
                LocalDateTime.now().plusMinutes(100),
                Duration.ofMinutes(30));
        taskManager.addTask(subTask1);

        subTask2 = new SubTask("Упаковать кошку",
                "Переноска за дверью",
                epic,
                LocalDateTime.now().plusMinutes(300),
                Duration.ofHours(1).plusMinutes(30));
        taskManager.addTask(subTask2);

        task = new Task(100,
                "Задача 1",
                "Задача для наполнения менеджера",
                LocalDateTime.now().plusMinutes(200),
                Duration.ofHours(1).plusMinutes(15));
        taskManager.addTask(task);

        assertEquals(3, taskManager.getPrioritizedTasks().size(),
                "Количество задач в отсортированном списке не верно!");
    }

    //Проверка функции получения списка всех Эпиков.
    @Test
    void getEpics(){    //ArrayList<Task> getEpics();
        //b. С пустым списком задач.
        assertDoesNotThrow(()->taskManager.getEpics(),
                "Запрос пустого списка задач не должен вызывать исключений!");

        //a. Со стандартным поведением.
        epic = new Epic(200,"Переезд", "Телефон перевозчика: +123 456 78 90");
        taskManager.addTask(epic);
        assertEquals(1, taskManager.getEpics().size(), "Количество эпиков в истории не верно!");
    }

    //Проверка функции получения списка всех подзадач определённого эпика.
    @Test
    void getSubTasks(){    //ArrayList<SubTask> getSubTasks(Epic epic);
        //b. С пустым списком подзадач.
        epic = new Epic(200,"Переезд", "Телефон перевозчика: +123 456 78 90");
        taskManager.addTask(epic);
        assertDoesNotThrow(()->taskManager.getSubTasks(epic),
                "Запрос пустого списка подзадач Эпика не должен вызывать исключений!");

        //a. Со стандартным поведением.
        subTask1 = new SubTask("Собрать коробки", "Коробки на чердаке", epic);
        taskManager.addTask(subTask1);
        assertEquals(1, taskManager.getSubTasks(epic).size(), "Количество подзадач в эпике не верно!");
    }

    //Проверка функции получения задачи по идентификатору.
    @Test
    void getTask(){    //Task getTask(int num);
        //c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
        assertDoesNotThrow(()->taskManager.getTask(1),
                "Запрос несуществующей задачи не должен вызывать исключений!");

        //a. Со стандартным поведением.
        task = new Task(100,"Задача 1", "Задача для удаления из начала истории");
        taskManager.addTask(task);

        assertEquals(task, taskManager.getTask(100),
                "Возвращённая задача не соответствует добавленой!");    }

    //Проверка функции добавления новой Задачи, Эпика и Подзадачи. Сам объект должен передаваться в качестве параметра.
    @Test
    void addTask(){    //void addTask(Task newTask);
        //a. Со стандартным поведением.
        task = new Task(100,"Задача 1", "Задача для добавления");
        taskManager.addTask(task);
        assertEquals(1, taskManager.getAllTasksList().size(),
                "Количество задач в менеджере после добавления новой задачи не верно!");
    }

    //Проверка функции обновления задачи любого типа по идентификатору. Новая версия объекта передаётся в виде параметра.
    @Test
    void updateTask(){    //void updateTask(Task newTask);
        //c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
        taskManager.updateTask(null);
        assertDoesNotThrow(()->taskManager.updateTask(null),
                "Попытка пустого обновления не должна вызывать исключение!");

        //a. Со стандартным поведением.
        task = new Task(100,"Задача 1", "Задача для проверки обновления");
        taskManager.addTask(task);

        task = new Task(100,"Задача 1", "Обновлённый вариант задачи");
        taskManager.updateTask(task);
        assertEquals("Обновлённый вариант задачи", task.getDetails(),
                "Описание задачи не было обновлено!");
    }

    //Проверка функции удаления ранее добавленных задач — всех и по идентификатору.
    @Test
    void delTask(){    //void delTask(Integer num);
        //b. С пустым списком задач.
        assertDoesNotThrow(()->taskManager.delTask(1),
                "Попытка удаления несуществующей задачи не должна вызывать исключение!");

        //a. Со стандартным поведением.
        task = new Task(100,"Задача 1", "Задача для проверки удаления");
        taskManager.addTask(task);
        task = new Task(200,"Задача 2", "Задача для проверки удаления");
        taskManager.addTask(task);
        assertEquals(2, taskManager.getAllTasksList().size(),
                "Количество задач в менеджере после добавления новой задачи не верно!");
        taskManager.delTask(100);
        assertEquals(1, taskManager.getAllTasksList().size(),
                "Количество задач в менеджере после удаления задачи не верно!");

        //c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
        taskManager.delTask(null);  //Пустой идентификатор означает удаление всех задач
        assertEquals(0, taskManager.getAllTasksList().size(),
                "После удаления всех задач список не пустой!");
    }

    //Проверка функции формирования идентификатора задачи
    @Test
    void calcNewNum(){    //int calcNewNum();
        //b. С пустым списком задач.
        assertEquals(1, taskManager.calcNewNum(),
                "Нумерация должна начинаться с 1!");
        assertEquals(1, taskManager.calcNewNum(),
                "Если номер не был присвоен какой-либо задаче, он выдаётся снова!");

        //a. Со стандартным поведением.
        task = new Task("Задача 1", "Задача для проверки выдачи номера");
        taskManager.addTask(task);
        assertEquals(2, taskManager.calcNewNum(),
                "Должен выдаться наименьший свободный номер!");

        task = new Task(100,"Задача 2", "Промежуточная задача с произвольным номером");
        taskManager.addTask(task);

        task = new Task("Задача 2", "Задача для проверки выдачи номера");
        taskManager.addTask(task);
        assertEquals(3, taskManager.calcNewNum(),
                "Должен выдаться наименьший свободный номер!");

    }

    //Проверка функции получения просмотренных задачи.
    // (полученных через getTask(), изменённых updateTask() или удалённых delTask()).
    @Test
    void history(){    //ArrayList<Task> history();
        //b. С пустым списком задач.
        assertDoesNotThrow(()->taskManager.history(),
                "Запрос пустой истории обращений не должен вызывать исключений!");

        assertNotNull(taskManager.history(), "История не пустая!");

        //a. Со стандартным поведением.
        task = new Task(100,"Задача 1", "Задача для удаления из начала истории");
        taskManager.addTask(task);
        task = taskManager.getTask(100);
        assertEquals(1, taskManager.history().size(),
                "Размер истории обращений не верен!");
    }
}