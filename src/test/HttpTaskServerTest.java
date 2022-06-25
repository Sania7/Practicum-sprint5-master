package test;

import Http.HTTPTaskServer;
import Http.KVServer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.http.HttpClient.newHttpClient;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {

    java.net.http.HttpClient client;
    static KVServer kvServ;
    HTTPTaskServer tasksServ;
    final String tServURL = "http://localhost:8080";
    int lastResponseStatusCode;

    //Тестовый образ структуры задач менеджера для первичной загрузки на KVServer
    static final String testManagerJson = "{" +
            "\"tasks\": [" +
            "{" +
            "\"id\": 100," +
            "\"type\": \"TASK\"," +
            "\"status\": \"NEW\"," +
            "\"name\": \"Задача 1\"," +
            "\"description\": \"Задача для наполнения менеджера\"," +
            "\"start\": \"2022-04-08T03:45:28.564173500\"," +
            "\"duration\": \"PT1H15M\"" +
            "}," +
            "{" +
            "\"subTasks\": []," +
            "\"id\": 200," +
            "\"type\": \"EPIC\"," +
            "\"status\": \"NEW\"," +
            "\"name\": \"Эпик 1\"," +
            "\"description\": \"Пустой эпик\"" +
            "}," +
            "{" +
            "\"subTasks\": [" +
            "{" +
            "\"epic\":300," +
            "\"id\": 1," +
            "\"type\": \"SUBTASK\"," +
            "\"status\": \"NEW\"," +
            "\"name\": \"Собрать коробки\"," +
            "\"description\": \"Коробки на чердаке\"," +
            "\"start\": \"2022-04-08T02:05:28.559173500\"," +
            "\"duration\": \"PT30M\"" +
            "}," +
            "{" +
            "\"epic\":300," +
            "\"id\": 2," +
            "\"type\": \"SUBTASK\"," +
            "\"status\": \"NEW\"," +
            "\"name\": \"Упаковать кошку\"," +
            "\"description\": \"Переноска за дверью\"," +
            "\"start\": \"2022-04-08T05:25:28.561173500\"," +
            "\"duration\": \"PT1H30M\"" +
            "}]," +
            "\"endTime\": \"2022-04-08T06:55:28.561173500\"," +
            "\"id\": 300," +
            "\"type\": \"EPIC\"," +
            "\"status\": \"NEW\"," +
            "\"name\": \"Эпик 2\"," +
            "\"description\": \"Эпик с подзадачами\"," +
            "\"start\": \"2022-04-08T02:05:28.559173500\"," +
            "\"duration\": \"PT2H\"" +
            "}]," +
            "\"history\": [1,100,200,300]}";

    //Эталонный json задчи
    static final String taskJson = "{" +
            "\"id\":100," +
            "\"type\":\"TASK\"," +
            "\"status\":\"NEW\"," +
            "\"name\":\"Задача 1\"," +
            "\"description\":\"Задача для наполнения менеджера\"," +
            "\"start\":\"2022-04-08T03:45:28.564173500\"," +
            "\"duration\":\"PT1H15M\"" +
            "}";

    //Эталонный json подзадачи
    static final String subtaskJson = "{" +
            "\"epic\":300," +
            "\"id\":1," +
            "\"type\":\"SUBTASK\"," +
            "\"status\":\"NEW\"," +
            "\"name\":\"Собрать коробки\"," +
            "\"description\":\"Коробки на чердаке\"," +
            "\"start\":\"2022-04-08T02:05:28.559173500\"," +
            "\"duration\":\"PT30M\"" +
            "}";

    //Эталонный json эпика
    static final String epicJson = "{" +
            "\"subTasks\":[" +
            "{" +
            "\"epic\":300," +
            "\"id\":1," +
            "\"type\":\"SUBTASK\"," +
            "\"status\":\"NEW\"," +
            "\"name\":\"Собрать коробки\"," +
            "\"description\":\"Коробки на чердаке\"," +
            "\"start\":\"2022-04-08T02:05:28.559173500\"," +
            "\"duration\":\"PT30M\"" +
            "}," +
            "{" +
            "\"epic\":300," +
            "\"id\":2," +
            "\"type\":\"SUBTASK\"," +
            "\"status\":\"NEW\"," +
            "\"name\":\"Упаковать кошку\"," +
            "\"description\":\"Переноска за дверью\"," +
            "\"start\":\"2022-04-08T05:25:28.561173500\"," +
            "\"duration\":\"PT1H30M\"" +
            "}]," +
            "\"endTime\":\"2022-04-08T06:55:28.561173500\"," +
            "\"id\":300," +
            "\"type\":\"EPIC\"," +
            "\"status\":\"NEW\"," +
            "\"name\":\"Эпик 2\"," +
            "\"description\":\"Эпик с подзадачами\"," +
            "\"start\":\"2022-04-08T02:05:28.559173500\"," +
            "\"duration\":\"PT2H\"" +
            "}";

    //Запуск KVServer
    @BeforeAll
    public static void beforeAll() throws IOException {
        kvServ = new KVServer();    //Создание и запуск сервера данных
        kvServ.start();
    }


    //Остановка сервера
    @AfterAll
    public static void afterAll() {
        kvServ.stop();
    }

    //Приведение менеджера задач на сервере в исходное состояние
    @BeforeEach
    public void beforeEach() throws IOException {
        //Загрузка подготовленного образа менеджера задач на KVServer
        post("http://localhost:8078/save/PreparedTestServer_1?API_KEY=DEBUG", testManagerJson);

        //Создание менеджера задач на основе эталонного образа с сервера
        tasksServ = new HTTPTaskServer("TestServer_1","PreparedTestServer_1");
    }

    //Остановка сервера HTTPTaskServer перед проведением следующего теста
    @AfterEach
    public void afterEach(){
        tasksServ.stop();
    }

    //Получение списка задач с сортировкой по приоритету для выполнения /tasks
    @Test
    public void tasksEndPoint(){
        assertEquals("{\"taskPriority\":[1,100,2]}", get("http://localhost:8080/tasks"),
                "Список приоритетов задач не совпал с ожидаемым!");

    }

    //Получение истории обращения к задачам /tasks/history
    @Test
    public void tasksHistoryEndPoint(){
        assertEquals("{\"history\":[1,100,200,300]}", get("http://localhost:8080/tasks/history"),
                "Список истории обращения к задачам не совпал с ожидаемым!");
    }

    //Работа с задачами: получение, создание, удаление /tasks/task
    @Test
    public void tasksTaskEndPoint(){
        String url = tServURL + "/tasks/task";

        //Получение задачи
        assertEquals(taskJson, get(url + "?id=100"),"Возвращённый json задачи не совпал с эталонным!");

        //Удаление задачи
        delete(url + "?id=100");
        assertEquals(200, lastResponseStatusCode,"Проблема с удалением задачи!");

        //Проверка что задача удалена. Проверка запроса несуществующей задачи.
        assertNotNull(get(url + "?id=100"), "Задача не удалена!");

        //Создание задачи по эталонному json
        post(url, taskJson);
        assertEquals(200, lastResponseStatusCode,"Проблема с созданием задачи!");
        assertEquals(taskJson, get(url + "?id=100"),"Проблема с созданием задачи!");

        //Удаление всех задач
        delete(url);
        assertEquals(200, lastResponseStatusCode,"Проблема с удалением задачи!");
        assertEquals("{\"tasks\":[]}", get(url),"Проблема с удалением всех задач!");
    }

    //Работа с подзадачами: получение, создание, удаление /tasks/subtask
    @Test
    public void tasksSubtaskEndPoint(){
        String url = tServURL + "/tasks/subtask";

        //Получение подзадачи
        assertEquals(subtaskJson, get(url + "?id=1"),"Возвращённый json подзадачи не совпал с эталонным!");

        //Удаление подзадачи
        delete(url + "?id=1");
        assertEquals(200, lastResponseStatusCode,"Проблема с удалением подзадачи!");

        //Проверка что подзадача удалена. Проверка запроса несуществующей подзадачи.
        assertNotNull(get(url + "?id=1"), "Задача не удалена!");

        //Создание подзадачи по эталонному json
        post(url, subtaskJson);
        assertEquals(200, lastResponseStatusCode,"Проблема с созданием подзадачи!");
        assertEquals(subtaskJson, get(url + "?id=1"),"Проблема с созданием подзадачи!");
    }

    //Получение эпика подзадачи /tasks/subtask/epic
    @Test
    public void tasksSubtaskEpicEndPoint(){
        assertEquals(epicJson, get("http://localhost:8080/tasks/subtask/epic?id=1"),
                "Возвращённый json эпика не совпал с эталонным!");

    }

    //Работа с эпиками: получение, создание, удаление /tasks/epic
    @Test
    public void tasksEpicEndPoint(){
        String url = tServURL + "/tasks/epic";

        //Получение эпика
        assertEquals(epicJson, get(url + "?id=300"),"Возвращённый json эпика не совпал с эталонным!");

        //Удаление эпика
        delete(url + "?id=300");
        assertEquals(200, lastResponseStatusCode,"Проблема с удалением эпика!");

        //Проверка что эпик удалён. Проверка запроса несуществующего эпика.
        assertNotNull(get(url + "?id=300"), "Задача не удалена!");

        //Создание эпика по эталонному json
        post(url, epicJson);
        assertEquals(200, lastResponseStatusCode,"Проблема с созданием эпика!");
        assertEquals(epicJson, get(url + "?id=300"),"Проблема с созданием эпика!");

    }

    //Вспомогательный метод для отправки GET запроса
    public String get(String url){
        lastResponseStatusCode = -1;
        client = newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            lastResponseStatusCode = response.statusCode();
            return response.body();
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса \"" + url + "\" возникла ошибка: " + e.getMessage());
        }

        return "";
    }

    //Вспомогательный метод для отправки POST запроса
    public void post(String url, String body){
        lastResponseStatusCode = -1;
        client = newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            lastResponseStatusCode = response.statusCode();
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса \"" + url + "\" возникла ошибка: " + e.getMessage());
        }
    }

    //Вспомогательный метод для отправки DELETE запроса
    public void delete(String url){
        lastResponseStatusCode = -1;
        client = newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();


        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            lastResponseStatusCode = response.statusCode();
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса \"" + url + "\" возникла ошибка: " + e.getMessage());
        }
    }

}
