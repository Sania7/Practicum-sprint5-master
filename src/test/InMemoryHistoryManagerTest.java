package test;

import base.Task;
import managers.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    InMemoryHistoryManager histManager;
    Task task;

    @BeforeEach
    public void beforeEach() {
        histManager = new InMemoryHistoryManager();
    }

    //Тестирование процедуры добавления задачи в историю
    @Test
    void add() {
        //a. Пустая история задач.
        assertNotNull(histManager, "История не пустая!");
        assertEquals(0, histManager.getHistory().size(), "В пустой истории не должно быть элементов!");

        task = new Task(100,"Задача 1", "Задача для проверки дублирования.");

        histManager.add(task);
        assertEquals(1, histManager.getHistory().size(), "В истории должна быть одна задача!");

        //b. Дублирование.
        histManager.add(task);  //Повторное добавление той же задачи
        assertEquals(1, histManager.getHistory().size(), "В истории должна быть одна задача!");
    }

    //Тестирование процедуры удаления задачи из истории
    @Test
    void remove() {
        //a. Пустая история задач.
        assertDoesNotThrow(()->histManager.remove(1), "Удаление из пустой истории не должно вызывать исключений!");

        //с. Удаление из истории: начало, середина, конец.
        task = new Task(100,"Задача 1", "Задача для удаления из начала истории");
        histManager.add(task);

        task = new Task(200,"Задача 2", "Промежуточная задача для тестирования удаления");
        histManager.add(task);

        task = new Task(300,"Задача 3", "Задача для удаления из середины истории");
        histManager.add(task);

        task = new Task(400,"Задача 3", "Задача для удаления с конца истории");
        histManager.add(task);

        histManager.remove(100);
        assertEquals(3, histManager.getHistory().size(), "Задача в начале истории не была удалена!");

        histManager.remove(300);
        assertEquals(2, histManager.getHistory().size(), "Задача из середины истории не была удалена!");

        histManager.remove(400);
        assertEquals(1, histManager.getHistory().size(), "Задача в конце истории не была удалена!");
    }

    //Тестирование процедуры получения истории
    @Test
    void getHistory() {
        //a. Пустая история задач.
        assertNotNull(histManager, "История не пустая!");
        assertEquals(0, histManager.getHistory().size(), "История должна быть пустой!");

        task = new Task(100,"Задача 1", "Задача для проверки дублирования.");

        histManager.add(task);
        assertEquals(1, histManager.getHistory().size(), "В истории должна быть одна задача!");

        //b. Дублирование.
        histManager.add(task);  //Повторное добавление той же задачи
        assertEquals(1, histManager.getHistory().size(), "В истории должна быть одна задача!");

        //с. Удаление из истории: начало, середина, конец.
        task = new Task(100,"Задача 1", "Задача для удаления из начала истории");
        histManager.add(task);

        task = new Task(200,"Задача 2", "Промежуточная задача для тестирования");
        histManager.add(task);

        task = new Task(300,"Задача 3", "Задача для удаления из середины истории");
        histManager.add(task);

        task = new Task(400,"Задача 3", "Задача для удаления с конца истории");
        histManager.add(task);

        histManager.remove(100);
        assertEquals(3, histManager.getHistory().size(), "Задача в начале истории не была удалена!");

        histManager.remove(300);
        assertEquals(2, histManager.getHistory().size(), "Задача из середины истории не была удалена!");

        histManager.remove(400);
        assertEquals(1, histManager.getHistory().size(), "Задача в конце истории не была удалена!");
    }
}
