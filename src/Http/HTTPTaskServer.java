package Http;

import base.Epic;
import base.SubTask;
import base.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TaskManager;
import util.DurationTypeAdapter;
import util.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;

public class HTTPTaskServer {

    TaskManager taskManager;
    private final int PORT = 8080;    //Порт для прослушивания
    private HttpServer server;


    public static void main(String[] args) throws IOException {
        HTTPTaskServer serv = new HTTPTaskServer();
        serv.start();
    }

    //Запуск сервера
    public void start() {
        System.out.println("Запуск HttpTaskServer сервера на порту " + PORT);
        server.start();
    }

    //Остаровка сервера
    public void stop() {
        System.out.println("Остановка сервера HttpTaskServer на порту " + PORT);
        server.stop(1);
    }

    //Конструктор класса
    public HTTPTaskServer() throws IOException {
        taskManager = new HTTPTasksManager("url");    //Создание менеджера задач
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        createContext(server);
    }

    //Конструктор класса с заданием именем сохранения менеджера на сервере
    public HTTPTaskServer(String saveKey) throws IOException {
        taskManager = (TaskManager) Managers.getDefault();    //Создание менеджера задач
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        createContext(server);
    }

    //Конструктор класса на основе загрузки образа менеджера с сервера
    public HTTPTaskServer(String newKey, String loadKey) throws IOException {
        taskManager = (TaskManager) Managers.getDefault();    //Создание менеджера задач
        // manager.loadFromJson("url");
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        createContext(server);
        start();
    }

    //Процедура формирования контекста сервера
    private void createContext(HttpServer server){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();

        //Получение списка задач с сортировкой по приоритету для выполнения
        server.createContext("/tasks", (h) ->{
            JsonObject resp = new JsonObject();
            try {
                System.out.println("\n/tasks");
                if ("GET".equals(h.getRequestMethod())) {
                    JsonArray priority = new JsonArray();
                    resp.add("taskPriority", priority);
                    for (Task task : taskManager.getPrioritizedTasks()){
                        priority.add(task.getNum());
                    }
                    sendText(h, resp.toString());
                } else {
                    System.out.println("/tasks ждёт GET - запрос, а получил " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });

        //Получение истории обращения к задачам
        server.createContext("/tasks/history", (h) -> {
            JsonObject resp = new JsonObject();
            try {
                System.out.println("\n/tasks/history");
                if ("GET".equals(h.getRequestMethod())) {
                    JsonArray hist = new JsonArray();
                    resp.add("history", hist);
                    for (Task task : taskManager.history()) {
                        hist.add(task.getNum());
                    }
                    sendText(h, resp.toString());
                } else {
                    System.out.println("/tasks/history ждёт GET - запрос, а получил " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });

        //Работа с задачами: получение(клиентом), создание, удаление
        server.createContext("/tasks/task", (h) ->{
            JsonObject resp = new JsonObject();
            Integer id = getIntParam(h.getRequestURI(), "id");
            try {
                System.out.println("\n/tasks/task");
                switch (h.getRequestMethod()) {
                    case "GET":     //Отправить задачу(задачи) по запросу
                        if (id == null) {   //Если идентификатор не указан - выдать все
                            resp.add("tasks", gson.toJsonTree(taskManager.getTasksList()));
                            sendText(h, resp.toString());
                        } else {
                            sendText(h, gson.toJson(taskManager.getTask(id)));
                        }
                        break;
                    case "POST":
                        String body = readText(h);
                        if (body.isEmpty()) {
                            h.sendResponseHeaders(400,0);
                            return;
                        }
                        Task task = gson.fromJson(body,Task.class);
                        if (task.getNum() == null) {
                            taskManager.addTask(task);
                        } else {
                            taskManager.updateTask(task);
                        }
                    case "DELETE":  //Удаление задачи (либо - задач, если идентификатор пустой)
                        taskManager.delTask(id);
                        h.sendResponseHeaders(200, 0);
                        break;
                    default:
                        System.out.println("/tasks/task ждёт GET, POST, DELETE - запрос, а получил " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });

        //Работа с подзадачами: получение(клиентом), создание, удаление
        server.createContext("/tasks/subtask", (h) -> {
            Integer id = getIntParam(h.getRequestURI(), "id");
            try {
                System.out.println("\n/tasks/subtask");
                switch (h.getRequestMethod()) {
                    case "GET":     //Отправить подзадачу по запросу
                        if (id != null) {
                            sendText(h, gson.toJson((SubTask)taskManager.getTask(id)));
                        } else {
                            h.sendResponseHeaders(404, 0);
                        }
                        break;
                    case "POST":    //Создать задачу на основе полученного Json
                        String body = readText(h);
                        if (body.isEmpty()) {
                            h.sendResponseHeaders(400,0);
                            return;
                        }
                        SubTask task = gson.fromJson(body,SubTask.class);
                        if (task.getNum() == null) {
                            taskManager.addTask(task);
                        } else {
                            taskManager.updateTask(task);
                        }
                    case "DELETE":  //Удаление подзадачи
                        if (id != null) {
                            taskManager.delTask(id);
                        }
                        h.sendResponseHeaders(200, 0);
                        break;
                    default:
                        System.out.println("/tasks/task ждёт GET, POST, DELETE - запрос, а получил " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });

        //Получение эпика подзадачи
        server.createContext("/tasks/subtask/epic", (h) ->{
            Integer id = getIntParam(h.getRequestURI(), "id");
            try {
                System.out.println("\n/tasks/subtask/epic");
                if ("GET".equals(h.getRequestMethod())) {
                    if (id != null) {
                        sendText(h, gson.toJson(((SubTask)taskManager.getTask(id)).getEpic()));
                    } else {
                        h.sendResponseHeaders(404, 0);
                    }
                } else {
                    System.out.println("/tasks/subtask/epic ждёт GET - запрос, а получил " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });

        //Работа с эпиками: получение(клиентом), создание, удаление
        server.createContext("/tasks/epic", (h) ->{
            Integer id = getIntParam(h.getRequestURI(), "id");
            try {
                System.out.println("\n/tasks/epic");
                switch (h.getRequestMethod()) {
                    case "GET":     //Отправить эпик по запросу
                        if (id != null) {
                            sendText(h, gson.toJson((Epic)taskManager.getTask(id)));
                        } else {
                            h.sendResponseHeaders(404, 0);
                        }
                        break;
                    case "POST":
                        String body = readText(h);
                        if (body.isEmpty()) {
                            h.sendResponseHeaders(400,0);
                            return;
                        }
                        Epic epic = gson.fromJson(body,Epic.class);
                        if (epic.getNum() == null) {
                            taskManager.addTask(epic);
                        } else {
                            taskManager.updateTask(epic);
                        }
                    case "DELETE":  //Удаление эпика (с подзадачами)
                        if (id != null) {
                            taskManager.delTask(id);
                        }
                        h.sendResponseHeaders(200, 0);
                        break;
                    default:
                        System.out.println("/tasks/task ждёт GET, POST, DELETE - запрос, а получил " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });


    }

    //Процедура получения заданного числового параметра из строки запроса
    private Integer getIntParam(URI uri, String name){
        if (uri != null && name != null){
            String query = uri.getQuery();
            if (query != null){
                String[] pairs = query.split("&");
                for(String pair : pairs){
                    String[] param = pair.split("=");
                    if (param.length >= 1 && name.equals(param[0]))
                        return Integer.parseInt(param[1]);
                }
            }
        }
        return null;
    }

    //Чтение ответа на запрос
    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), "UTF-8");
    }

    //Отправка ответа на запрос
    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes("UTF-8");
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}
