package test;

import base.Epic;
import base.SubTask;
import base.Task;
import controller.FileBackedTasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest {
    FileBackedTasksManager taskManager1;

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTasksManager("save_tasks.txt");
    }

    //Тестирование метода сохранения задач в файл
    @Test
    void save() {
        //a. Пустой список задач.
        assertDoesNotThrow(()->((FileBackedTasksManager)taskManager).save(),
                "Сохранение менеджера с пустогым списком задач не должно вызывать исключений!");

        //b. Эпик без подзадач.
        epic = new Epic(200,"Переезд", "Телефон перевозчика: +123 456 78 90");
        taskManager.addTask(epic);
        ((FileBackedTasksManager)taskManager).save();
        taskManager1 = ((FileBackedTasksManager)taskManager).loadFromFile(Paths.get("save_tasks.txt"));
        assertEquals(1, taskManager1.getAllTasksList().size(),
                "Количество задач менеджера после восстановления не совпало!");

        //c. Пустой список истории.
        assertEquals(0, taskManager1.history().size(),
                "Количество задач в истории обращения после восстановления не совпало!");

    }

    //Тестирование метода загрузки списка задач из файла
    @Test
    void loadFromFile() {   //public static FileBackedTasksManager loadFromFile(Path file)
        //b. Эпик без подзадач.
        epic = new Epic(200,"Эпик 1", "Пустой эпик");
        taskManager.addTask(epic);
        ((FileBackedTasksManager)taskManager).save();

        taskManager1 = ((FileBackedTasksManager)taskManager).loadFromFile(Paths.get("save_tasks.txt"));
        assertEquals(1, taskManager1.getAllTasksList().size(),
                "Количество задач менеджера после восстановления не совпало!");

        //c. Пустой список истории.
        assertEquals(0, taskManager1.history().size(),
                "Количество задач в истории обращения после восстановления не совпало!");

        //a. Со стандартным поведением.
        epic = new Epic(300,"Эпик 2", "Эпик с подзадачами");
        taskManager.addTask(epic);
        subTask1 = new SubTask("Собрать коробки",
                "Коробки на чердаке",
                epic,
                LocalDateTime.now(),
                Duration.ofHours(1).plusMinutes(30));
        taskManager.addTask(subTask1);

        subTask2 = new SubTask("Упаковать кошку", "Переноска за дверью", epic);
        taskManager.addTask(subTask2);

        task = new Task(100,
                "Задача 1",
                "Задача для наполнения менеджера",
                LocalDateTime.now(),
                Duration.ofHours(2).plusMinutes(15));
        taskManager.addTask(task);

        taskManager.getTask(100);   //Добавление истории

        taskManager1 = ((FileBackedTasksManager)taskManager).loadFromFile(Paths.get("save_tasks.txt"));
        assertEquals(5, taskManager1.getAllTasksList().size(),
                "Количество задач менеджера после восстановления не совпало!");
    }
}
