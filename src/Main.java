import base.Epic;
import base.SubTask;
import base.Task;
import util.TaskStatus;
import managers.Managers;
import managers.TaskManager;

public class Main {


    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault(3);    //Получение менеджера задач
        // 1 - InMemoryTasksManager
        // 2 - FileBackedTasksManager
        // 3 - HTTPTasksManager

        Epic epic;
        Task task;
        SubTask subTask;

        //Формирование двух отдельных Задач
        task = new Task(100,"Задача 1", "Пояснение к задаче 1");
        manager.addTask(task);

        task = new Task(110,"Задача 2", "Пояснение к задаче 2");
        task.setStatus(TaskStatus.IN_PROGRESS);
        manager.addTask(task);

        //Формирование первого Эпика с тремя подзадачами
        epic = new Epic(200,"Переезд", "Телефон перевозчика: +123 456 78 90");
        manager.addTask(epic);

        //Подзадачи к первому Эпику
        subTask = new SubTask("Собрать коробки", "Коробки на чердаке", epic);
        manager.addTask(subTask);

        subTask = new SubTask("Упаковать кошку", "Переноска за дверью", epic);
        manager.addTask(subTask);

        subTask = new SubTask("Сказать слова прощания", "Можно на английском", epic);
        manager.addTask(subTask);

        //Формирование втрого Эпика (без подзадач)
        epic = new Epic(300,"Эпик 2", "Эпик без подзадач");
        manager.addTask(epic);

        //Обращение к задачам
        System.out.println("\n----------Первое обращение к задачам (200, 1, 110, 300, 100):");
        task = manager.getTask(200);
        task = manager.getTask(1);
        task = manager.getTask(110);
        task = manager.getTask(300);
        task = manager.getTask(100);

        //Просмотр истории обращения к задачам через общий метод getTask()
        System.out.println("Список обращений к задачам:");
        for (Task taskFor : manager.history())
            System.out.println("#" + taskFor.getNum() + " - " + taskFor.getName() + "(" + taskFor.getStatus() + ")");

        //Обращение к задачам
        System.out.println("\n----------Второе обращение к задачам (300, 2, 100, 200, 110):");
        task = manager.getTask(300);
        task = manager.getTask(2);
        task = manager.getTask(100);
        task = manager.getTask(200);
        task = manager.getTask(110);

        //Просмотр истории обращения к задачам через общий метод getTask()
        System.out.println("Список обращений к задачам:");
        for (Task taskFor : manager.history())
            System.out.println("#" + taskFor.getNum() + " - " + taskFor.getName() + "(" + taskFor.getStatus() + ")");

        //Обращение к задачам
        System.out.println("\n----------Третье обращение к задачам (100, 3, 110, 200, 300):");
        task = manager.getTask(100);
        task = manager.getTask(3);
        task = manager.getTask(110);
        task = manager.getTask(200);
        task = manager.getTask(300);

        //Просмотр истории обращения к задачам через общий метод getTask()
        System.out.println("Список обращений к задачам:");
        for (Task taskFor : manager.history())
            System.out.println("#" + taskFor.getNum() + " - " + taskFor.getName() + "(" + taskFor.getStatus() + ")");

        System.out.println("\nПри обращении задача вставляется/переносится в конец истории.");
        System.out.println("Соответственно, задачи к которым не было обращений постепенно смещаются в начало.");

        //Удаление Задачи
        manager.delTask(110);

        //Просмотр истории обращения к задачам через общий метод getTask()
        System.out.println("\nСписок обращений к задачам после удаления задачи #110:");
        for (Task taskFor : manager.history())
            System.out.println("#" + taskFor.getNum() + " - " + taskFor.getName() + "(" + taskFor.getStatus() + ")");

        //Удаление Эпика
        manager.delTask(200);

        //Просмотр истории обращения к задачам через общий метод getTask()
        System.out.println("\nСписок обращений к задачам после удаления Эпика #200:");
        for (Task taskFor : manager.history())
            System.out.println("#" + taskFor.getNum() + " - " + taskFor.getName() + "(" + taskFor.getStatus() + ")");
    }
}