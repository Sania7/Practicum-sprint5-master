package controller;

public class Managers {

    private Managers() {}
    //Получение Менеджера задач по умолчанию

    public static TaskManager getDefault(){
        return new InMemoryTasksManager();
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
