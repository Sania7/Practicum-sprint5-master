package base;

import util.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    // ссылка на родителя Эпик
    private Epic epic;

    // конструктор
    public SubTask(Integer num, String name, String details, Epic epic) {
        super(num, name, details, TaskType.SUBTASK);
        this.epic = epic;
    }

    // конструктор
    public SubTask(String name, String details, Epic epic) {
        super(name, details, TaskType.SUBTASK);
        this.epic = epic;
    }

    public SubTask(String name, String details, Epic epic, LocalDateTime startTime, Duration duration) {
        super(name,details,TaskType.SUBTASK,startTime,duration);
    }

    public SubTask() {
    }

    //Получение Эпика
    public Epic getEpic() {
        return epic;
    }

    //Задание Эпика
    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    //Отображение задачи
    @Override
    public String toString() {
        return getNum() + "," + getType() + "," + getName() + "," + getStatus() + "," + getDetails() + "," + getEpic().getNum();
    }
}
