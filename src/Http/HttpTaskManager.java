package Http;

import controller.FileBackedTasksManager;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVClient kvClient;

    public HttpTaskManager(int port) {
        super();
        this.kvClient = new KVClient();
    }
}
