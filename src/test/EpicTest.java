package test;

import base.Epic;
import base.SubTask;
import controller.Managers;
import controller.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager taskManager; // получение менеджера задач
    Epic epic;
    SubTask subTask1;
    SubTask subTask2;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    // тестирование получения статуса Эпика
    @Test
    void getStatus() {
        // создание первого Эпика с подзадачами
        epic = new Epic(200, "Отпуск", "Телефон туроператора: +777 21 721");
        taskManager.addTask(epic);

        // 1.пустой список подзадач
        assertEquals(TaskStatus.NEW, epic.getStatus(),
        "Пустой Эпик имеет статус NEW");

        // 2. подзадачи со статусом NEW
        subTask1 = new SubTask("Собрать вещи", "Сумки в гардеробе", epic);
        taskManager.addTask(subTask1);
        subTask2 = new SubTask("Взять одежду для купания", "Купить в интернете", epic);
        taskManager.addTask(subTask2);
        assertEquals(TaskStatus.NEW, epic.getStatus(),
        "Эпик с подзадачами со статусом NEW должен иметь статус NEW");

        // 3.подзадачи со статусами NEW и DONE
        subTask1.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
        "Эпик со всеми подзадачами в статусе NEW и DONE должен быть с статусе IN_PROGRESS");

        // 4. подзадачи со статусом DONE
        subTask2.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, epic.getStatus(),
                "Эпик с подзадачами в статусе DONE должен иметь статус DONE");

        // 5.подзадачи со статусом IN_PROGRESS
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
        "Эпик с подзадачей в статусе IN_PROGRESS должен иметь статус IN_PROGRESS");

        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Эпик со всеми подзадачами в статусе IN_PROGRESS должен иметь статус IN_PROGRESS!");
    }
}