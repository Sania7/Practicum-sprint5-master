package managers;

import Http.HTTPTasksManager;

public class Managers {

    private static InMemoryTasksManager inMemoryManager;
    private static FileBackedTasksManager fileBackedManager;
    private static HTTPTasksManager httpManager;



    public static TaskManager getDefault() {
        return new HTTPTasksManager("localhost");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
