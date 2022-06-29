package managers;

import Http.HTTPTasksManager;

public class Managers {

    public static TaskManager getDefault() {
        return new HTTPTasksManager("localhost");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
