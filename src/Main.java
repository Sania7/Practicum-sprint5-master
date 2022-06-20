import base.Epic;
import base.SubTask;
import base.Task;
import util.TaskStatus;
import controller.Managers;
import controller.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();    //Получение менеджера задач

        Epic epic;
        Task task;
        SubTask subTask;

        //Формирование двух отдельных Задач
        task = new Task(101, "Планирование № 1" ,"Пояснение к планированю № 1");
        manager.addTask(task);

        task = new Task(102, "Планирование № 2", "Пояснение к планированию № 2");
        task.setStatus(TaskStatus.IN_PROGRESS);
        manager.addTask(task);

        //Формирование первого Эпика с тремя подзадачами
        epic = new Epic(200, "Отпуск", "Телефон туроператора: +777 21 721");
        manager.addTask(epic);

        //Подзадачи к первому Эпику
        subTask = new SubTask("Собрать вещи", "Сумки в гардеробе", epic);
        manager.addTask(subTask);

        subTask = new SubTask("Взять одежду для купания", "Купить в интернете", epic);
        manager.addTask(subTask);

        subTask = new SubTask("Сказать всем пока", "Предупредить маму", epic);
        manager.addTask(subTask);

        //Формирование втрого Эпика (без подзадач)
        epic = new Epic(300, "Эпик № 2", "Эпик без подзадач ");
        manager.addTask(epic);

        //Обращение к задачам
        System.out.println("\n -----Первый просмотр задач (200, 1, 102, 300, 101):");
        System.out.println();
        task = manager.getTask(200);
        task = manager.getTask(1);
        task = manager.getTask(102);
        task = manager.getTask(300);
        task = manager.getTask(101);

        //Просмотр истории обращения к задачам через общий метод getTask()
        System.out.println("Список просмотров задач:");
        for (Task taskFor : manager.history())
            System.out.println("#" + taskFor.getNum() + " - " + taskFor.getName() + "(" + taskFor.getStatus() + ")");

        //Обращение к задачам
        System.out.println("\n-----Второй просмотр задач (300, 2, 101, 200, 102):");
        System.out.println();
        task = manager.getTask(300);
        task = manager.getTask(2);
        task = manager.getTask(101);
        task = manager.getTask(200);
        task = manager.getTask(102);

        //Просмотр истории обращения к задачам через общий метод getTask()
        System.out.println("Список просмотров задач:");
        for (Task taskFor : manager.history())
            System.out.println("#" + taskFor.getNum() + " - " + taskFor.getName() + "(" + taskFor.getStatus() + ")");

        //Обращение к задачам
        System.out.println("\n-----Третий просмотр задач (101, 3, 102, 200, 300):");
        System.out.println();
        task = manager.getTask(101);
        task = manager.getTask(3);
        task = manager.getTask(102);
        task = manager.getTask(200);
        task = manager.getTask(300);

        //Просмотр истории обращения к задачам через общий метод getTask()
        System.out.println("Список прсмотров задач:");
        for (Task taskFor : manager.history())
            System.out.println("#" + taskFor.getNum() + " - " + taskFor.getName() + "(" + taskFor.getStatus() + ")");


        //Удаление Задачи
        manager.delTask(102);

        //Просмотр истории обращения к задачам через общий метод getTask()
        System.out.println("\nСписок просмотров задач после удаления задачи #101:");
        for (Task taskFor : manager.history())
            System.out.println("#" + taskFor.getNum() + " - " + taskFor.getName() + "(" + taskFor.getStatus() + ")");

        //Удаление Эпика
        manager.delTask(200);

        //Просмотр истории обращения к задачам через общий метод getTask()
        System.out.println("\nСписок просмотров задач после удаления Эпика #200:");
        for (Task taskFor : manager.history())
            System.out.println("#" + taskFor.getNum() + " - " + taskFor.getName() + "(" + taskFor.getStatus() + ")");

    }
}