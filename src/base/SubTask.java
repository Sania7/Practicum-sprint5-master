package base;

import com.google.gson.annotations.SerializedName;
import util.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private transient Epic epic;    //Ссылка на родительский Эпик

    @SerializedName("epic")
    private Integer epicNum;

    //Конструктор
    public SubTask(Integer num, String name, String details, Epic epic) {
        super(num, name, details, TaskType.SUBTASK);

        this.epic = epic;
        epicNum = epic.getNum();
    }

    //Конструктор
    public SubTask(String name, String details, Epic epic) {
        super(name, details, TaskType.SUBTASK);

        this.epic = epic;
        epicNum = epic.getNum();
    }

    //Конструктор
    public SubTask(Integer num, String name, String details, Epic epic, LocalDateTime startTime, Duration duration) {
        super(num, name, details, TaskType.SUBTASK, startTime, duration);

        this.epic = epic;
        epicNum = epic.getNum();
    }

    //Конструктор
    public SubTask(String name, String details, Epic epic, LocalDateTime startTime, Duration duration) {
        super(name, details, TaskType.SUBTASK, startTime, duration);

        this.epic = epic;
        epicNum = epic.getNum();
    }

    //Получение Эпика
    public Epic getEpic() {
        return epic;
    }

    //Задание Эпика
    public void setEpic(Epic epic) {
        this.epic = epic;
        epicNum = epic.getNum();
    }

    //Получение номера эпика
    public int getEpicNum(){
        return epicNum;
    }

    //Отображение задачи
    @Override
    public String toString() {
        return getNum() + "," +
                getType() + "," +
                getName() + "," +
                getStatus() + "," +
                getDetails() + "," +
                //(epic == null ? "" : epic.getNum()) + "," +
                epicNum  + "," +
                getStartTime() + "," +
                (getDuration() == Duration.ZERO ? "" : getDuration());
    }
}
