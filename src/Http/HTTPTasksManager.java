package Http;

import base.Epic;
import base.SubTask;
import base.Task;
import com.google.gson.*;
import managers.FileBackedTasksManager;
import util.DurationTypeAdapter;
import util.LocalDateTimeTypeAdapter;
import util.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class HTTPTasksManager extends FileBackedTasksManager {

    //Общий клиент экземпляров класса для отправки данных на KV сервер
    private static KVTaskClient kvClient = new KVTaskClient("localhost");

    private final String saveKey;   //Ключ для сохранения менеджера на KV сервер

    //Конструктор класса
    public HTTPTasksManager() {
        super("");
        saveKey = "HTTPTaskManager";
    }

    //Конструктор класса
    public HTTPTasksManager(String saveKey) {
        super("");
        this.saveKey = saveKey;
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

    //Перегрузка метода для добавления записи изменений
    @Override
    public Task getTask(int num){
        Task task = super.getTask(num);
        save();
        return task;
    }
    //Сохранение данных на сервер
    @Override
    public void save() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();

        JsonObject result = new JsonObject();
        result.add("tasks", gson.toJsonTree(getTasksList()));

        JsonArray hist = new JsonArray();
        result.add("history", hist);
        for (Task task : history.getHistory()){
            hist.add(task.getNum());
        }

        kvClient.put(saveKey, result.toString());   //Отправка образа менеджера на сервер
    }

    //Создание нового экземпляра менеджера на основе данных с сервера
    public static HTTPTasksManager loadFromJson(String loadKey, String newKey){
        HTTPTasksManager newManager = new HTTPTasksManager(newKey);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();

        JsonElement mngElement = JsonParser.parseString(kvClient.load(loadKey));
        if(!mngElement.isJsonObject()) {    // проверяем, точно ли мы получили JSON-объект
            System.out.println("Ответ от сервера не соответствует ожидаемому.");
            return null;
        }
        JsonObject mngJsonObj = mngElement.getAsJsonObject();

        //Формирование структуры задач нового менеджера
        JsonArray tasksJsonArray = mngJsonObj.getAsJsonArray("tasks");
        for(JsonElement taskElement : tasksJsonArray){
            String taskType = taskElement.getAsJsonObject().get("type").getAsString();
            switch(TaskType.valueOf(taskType)){
                case TASK:  //Загрузка обычных задач
                    newManager.addTask(gson.fromJson(taskElement, Task.class));
                    break;
                case EPIC:  //Загрузка эпиков с подзадачами
                    Epic epic = gson.fromJson(taskElement, Epic.class);
                    newManager.addTask(epic);
                    //Коррекция подзадач после десериализации
                    for(SubTask subTask : epic.getSubTasks()){
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
        return newManager;
    }
}
