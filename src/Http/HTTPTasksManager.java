package Http;

import base.Epic;
import base.SubTask;
import base.Task;
import com.google.gson.*;
import managers.FileBackedTasksManager;
import managers.InMemoryTasksManager;
import managers.TaskManager;
import util.DurationTypeAdapter;
import util.LocalDateTimeTypeAdapter;
import util.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

import static sun.nio.ch.IOUtil.load;

public class HTTPTasksManager extends FileBackedTasksManager {
    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    private final KVTaskClient client;
    InMemoryTasksManager manager;

    TaskManager taskManager;

    //Конструктор класса
    public HTTPTasksManager(String url) {
        this(url, false);
    }

    //Конструктор класса
    public HTTPTasksManager(String url, boolean load) {
        super(null);
        client = new KVTaskClient(url);
        if (load) {
            load();
        }
    }

    //Перегрузка метода для добавления записи изменений в файл
    public void addTask(Task newTask){
        super.addTask(newTask);
        save();
    }

    //Перегрузка метода для добавления записи изменений в файл
    public void updateTask(Task newTask){
        super.updateTask(newTask);
        save();
    }

    //Перегрузка метода для добавления записи изменений в файл
    public void delTask(Integer num){
        super.delTask(num);
        save();
    }

    //Перегрузка метода для добавления записи изменений
    public Task getTask(int num){
        Task task = super.getTask(num);
        save();
        return task;
    }
    //Сохранение данных на сервер

    public void save() {
        new GsonBuilder();
        JsonObject result = new JsonObject();
        result.add("tasks", gson.toJsonTree(getTasksList()));
        JsonArray hist = new JsonArray();
        result.add("history", hist);
        for (Task task : history.getHistory()){
            hist.add(task.getNum());
        }

    }

    //Создание нового экземпляра менеджера на основе данных с сервера
    public void loadFromJson(String loadKey) {
        KVTaskClient kvClient = new KVTaskClient("localhost");
        HTTPTasksManager newManager = new HTTPTasksManager("url");
        new GsonBuilder();
        JsonElement mngElement = JsonParser.parseString(kvClient.load(loadKey));
        if(!mngElement.isJsonObject()) {    // проверяем, точно ли мы получили JSON-объект
            System.out.println("Ответ от сервера не соответствует ожидаемому.");
        }
        JsonObject mngJsonObj = mngElement.getAsJsonObject();

        //Формирование структуры задач нового менеджера
        JsonArray tasksJsonArray = mngJsonObj.getAsJsonArray("tasks");
        for(JsonElement taskElement : tasksJsonArray) {
            String taskType = taskElement.getAsJsonObject().get("type").getAsString();
            switch(TaskType.valueOf(taskType)) {
                case TASK:  //Загрузка обычных задач
                    newManager.addTask(gson.fromJson(taskElement, Task.class));
                    break;
                case EPIC:  //Загрузка эпиков с подзадачами
                    Epic epic = gson.fromJson(taskElement, Epic.class);
                    newManager.addTask(epic);
                    //Коррекция подзадач после десериализации
                    for(SubTask subTask : epic.getSubTasks()) {
                        subTask.setEpic(epic);                              //Восстановление обратной связи с эпиком
                        newManager.taskList.put(subTask.getNum(), subTask); //Прописывание подзадачи в общем списке менеджера
                    }
                    break;
                default:
                    System.out.println("Ошибочный тип задачи: " + taskType);
            }
        }

        newManager.refreshSortedSet();
        //Формирование истории нового менеджена задач
        JsonArray histJsonArray = mngJsonObj.getAsJsonArray("history");
        for (JsonElement histElement : histJsonArray){
            newManager.getTask(histElement.getAsInt());
        }
    }
}
