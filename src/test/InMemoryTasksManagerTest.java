package test;

import controller.InMemoryTasksManager;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTasksManagerTest extends TaskManagerTest {
    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTasksManager(); // получение менеджера задач
    }
}
