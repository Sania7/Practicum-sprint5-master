package controller;

import base.*;
import exception.ManagerSaveException;
import util.TaskStatus;
import util.TaskType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTasksManager {
    private Path saveFile;

    public static final String FILE_HEADER = "ID,TYPE,NAME,STATUS,DESCRIPTION,EPIC";
    //Конструктор класса
    public FileBackedTasksManager(String saveFile){
        super();
        this.saveFile = Paths.get(saveFile);
    }

    public FileBackedTasksManager() {
    }

    //Метод для проверки работы менеджера
    public static void main(String[] args){

        FileBackedTasksManager manager  = new FileBackedTasksManager("save_tasks.txt");
        FileBackedTasksManager manager1 = new FileBackedTasksManager("save_tasks.txt");    //Менеджер для проверки загрузки из файла

        Epic epic;
        Task task;
        SubTask subTask;

        //Заведение нескольких разных задач, эпиков и подзадач.
        task = new Task(101,"План № 1", "Пояснение к плану № 1");
        manager.addTask(task);

        task = new Task(102,"План № 2", "Пояснение к плану № 2");
        task.setStatus(TaskStatus.IN_PROGRESS);
        manager.addTask(task);

        //Создание первого Эпика с тремя подзадачами
        epic = new Epic(200,"Отпуск", "Телефон туроператора: +777 77 721");
        manager.addTask(epic);
        //Подзадачи к первому Эпику
        subTask = new SubTask("Собрать вещи в отпуск", "Чемоданы в гардеробе", epic);
        manager.addTask(subTask);

        subTask = new SubTask("Купить одежду на пляж", "Пляжная одежда с интернет магазина", epic);
        manager.addTask(subTask);

        subTask = new SubTask("Сказать всем пока на месяц", "Предупредить маму" , epic);
        manager.addTask(subTask);

        //Создание втрого Эпика (без подзадач)
        epic = new Epic(300,"Эпик 2, после отпуска", "Без задач");
        manager.addTask(epic);

        //Вывод списка задач
        System.out.println("Всего создано задач - " + manager.getAllTasksList().size());
        for (Task taskFor: manager.getAllTasksList().values())
            System.out.println(taskFor);

        //Запрос некоторых задач, чтобы заполнилась история просмотра.
        System.out.println("\n----------Просмотр задач (200, 1, 102, 300, 101):");
        task = manager.getTask(200);
        task = manager.getTask(1);
        task = manager.getTask(102);
        task = manager.getTask(300);
        task = manager.getTask(101);
        //Просмотр истории обращения к задачам
        System.out.println("\nСписок просмотров задач:");
        for (Task taskFor : manager.history())
            System.out.println(taskFor);

        System.out.println("\n----------Создание второго менеджера на основе файла первого экземпляра.");

        //Создание нового FileBackedTasksManager менеджера из этого же файла.
        manager1 = loadFromFile(Paths.get("save_tasks.txt"));

        //Вывод списка задач
        System.out.println("\nВсего загружено задач из файла - " + manager1.getAllTasksList().size());
        for (Task taskFor: manager1.getAllTasksList().values())
            System.out.println(taskFor);

        System.out.println("\nСписок обращений к задачам после загрузки из файла:");
        for (Task taskFor : manager1.history())
            System.out.println(taskFor);

    }

    //Метод сохранения задач в файл
    public void save(){
        List<String> lines = new ArrayList<>();

        //Подготовка данных
        lines.add(FILE_HEADER);
        for(Task task : super.taskLists.values()){
            lines.add(task.toString());
        }
        lines.add("\n" + getHistoryManager().toString());

        //Запись в файл
        try {
            Files.write(saveFile,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при записи в файл: " + e.getMessage());
        }
    }

    //Перегрузка метода для добавления записи изменений в файл
    @Override
    public Task getTask(int num){
        Task task = super.getTask(num);
        save();
        return task;
    }

    //Перегрузка метода для добавления записи изменений в файл
    @Override
    public void addTask(Task newTask){
        super.addTask(newTask);
        save();
    }

    //Перегрузка метода для добавления записи изменений в файл
    @Override
    public void updateTask(Task newTask){
        super.updateTask(newTask);
        save();
    }
    //Перегрузка метода для добавления записи изменений в файл
    @Override
    public void delTask(Integer num){
        super.delTask(num);
        save();
    }

    //Метод создания задачи из строки
    //Пример входной строки: 3,SUBTASK,Sub Task2,DONE,Description sub task3,2
    public Task fromString(String str){
        Task newTask = null;

        //Разбиение строки на поля
        String[] fields = str.split(",");

        //Получение типа новой задачи
        TaskType newType = TaskType.valueOf(fields[1]);

        switch (newType){
            case TASK:
                newTask = new Task(Integer.parseInt(fields[0]), fields[2], fields[4]);
                break;
            case EPIC:
                newTask = new Epic(Integer.parseInt(fields[0]), fields[2], fields[4]);
                break;
            case SUBTASK:
                newTask = new SubTask(Integer.parseInt(fields[0]),
                        fields[2],
                        fields[4],
                        (Epic) super.getTask(Integer.parseInt(fields[5])));
        }

        return newTask;
    }
    //Загрузка списка задач из файла
    public static FileBackedTasksManager loadFromFile(Path file){
        FileBackedTasksManager fm = new FileBackedTasksManager(file.toString());
        List<String> lines = null;

        //Считывание файла
        try {
            lines = Files.readAllLines(file);
        }
        catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при чтении файла " + file.toString() + ": " + e.getMessage());
        }

        //Первыми загружаются Эпики
        for (int i = 1; i < lines.size(); i++){     //В первой строке - заголовки
            if (lines.get(i) != null && lines.get(i).contains(",EPIC,")) {
                fm.addTask(fm.fromString(lines.get(i)));
            }
        }

        //Цикл по строкам из файла
        for (int i = 1; i < lines.size(); i++){     //В первой строке - заголовки
            if (lines.get(i).isEmpty()){            //После пустой строки - считвание истории
                if (lines.size() > (i + 1)){

                    fm.getHistoryManager().remove(i);             //Очистка истории перед её загрузкой
                    for(Integer num : InMemoryHistoryManager.fromString(lines.get(i + 1))){
                        fm.getTask(num);     //Обращение к задаче
                    }
                }
                break;
            } else if (lines.get(i) != null && !lines.get(i).contains(",EPIC,")){    //Загрузка остальных задач
                fm.addTask(fm.fromString(lines.get(i)));
            }
        }
        return fm;
    }
}
