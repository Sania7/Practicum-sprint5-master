package test;

import base.Epic;
import base.SubTask;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager taskManager;    //Получение менеджера задач
    Epic epic;
    SubTask subTask1;
    SubTask subTask2;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault(1);    //Получение менеджера задач
    }

    //Тестирование процедуры получения статуса Эпика
    @Test
    void getStatus() {
        //Формирование первого Эпика с подзадачами
        epic = new Epic(200,"Переезд", "Телефон перевозчика: +123 456 78 90");
        taskManager.addTask(epic);

        //a. Пустой список подзадач.
        assertEquals(TaskStatus.NEW, epic.getStatus(),
                "Эпик в пустым списком задач должен иметь статус NEW!");

        //b. Все подзадачи со статусом NEW.
        subTask1 = new SubTask("Собрать коробки", "Коробки на чердаке", epic);
        taskManager.addTask(subTask1);
        subTask2 = new SubTask("Упаковать кошку", "Переноска за дверью", epic);
        taskManager.addTask(subTask2);
        assertEquals(TaskStatus.NEW, epic.getStatus(),
                "Эпик с подзадачами в статусе NEW должен иметь статус NEW!");

        //d. Подзадачи со статусами NEW и DONE.
        subTask1.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Эпик с подзадачами в статусе NEW и DONE должен иметь статус IN_PROGRESS!");

        //c. Все подзадачи со статусом DONE.
        subTask2.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, epic.getStatus(),
                "Эпик со всеми подзадачами в статусе DONE должен иметь статус DONE!");

        //e. Подзадачи со статусом IN_PROGRESS.
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Эпик с подзадачей в статусе IN_PROGRESS должен иметь статус IN_PROGRESS!");

        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Эпик со всеми подзадачами в статусе IN_PROGRESS должен иметь статус IN_PROGRESS!");
    }

    //Тестирование процедуры получения длительности Эпика
    @Test
    void getDuration() {
        epic = new Epic(300,"Эпик 2", "Эпик с подзадачами");
        taskManager.addTask(epic);

        //a. Пустой список подзадач.
        assertDoesNotThrow(()->epic.getDuration(),
                "Запрос длительности у пустого эпика не должен вызывать исключений!");

        //a. Со стандартным поведением.
        subTask1 = new SubTask("Собрать коробки",
                "Коробки на чердаке",
                epic,
                LocalDateTime.now(),
                Duration.ofHours(1).plusMinutes(30));
        taskManager.addTask(subTask1);

        subTask2 = new SubTask("Подзадача 2",
                "Подзадача для проверки подсчёта продолжительности эпика",
                epic,
                LocalDateTime.now(),
                Duration.ofHours(2).plusMinutes(15));
        taskManager.addTask(subTask2);

        assertEquals(Duration.ofHours(3).plusMinutes(45), epic.getDuration(),
                "Продолжительность эпика должна быть равна сумме его подзадач!");

    }
}