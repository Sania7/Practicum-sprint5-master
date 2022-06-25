package base;

import com.google.gson.annotations.SerializedName;
import util.TaskStatus;
import util.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {

    @SerializedName("id")
    private Integer num;                //Уникальный идентификационный номер задачи

    @SerializedName("type")
    private TaskType taskType;          //Тип задачи
    private TaskStatus status;          //Статус задачи
    private String name;                //Название

    @SerializedName("description")
    private String details;             //Описание/дополнение

    @SerializedName("start")
    protected LocalDateTime startTime;  //Дата начала
    protected Duration duration;        //Продолжительность

    //Конструктор класса
    public Task(Integer num, String name, String details) {
        this.num = num;
        this.name = name;
        this.details = details;
        status = TaskStatus.NEW;
        taskType = TaskType.TASK;
    }

    //Конструктор класса
    public Task(String name, String details) {
        this.name = name;
        this.details = details;
        status = TaskStatus.NEW;
        taskType = TaskType.TASK;
    }

    //Конструктор класса
    public Task(Integer num, String name, String details, TaskType taskType) {
        this.num = num;
        this.name = name;
        this.details = details;
        status = TaskStatus.NEW;
        this.taskType = taskType;
    }

    //Конструктор
    public Task(String name, String details, TaskType taskType) {
        this.name = name;
        this.details = details;
        status = TaskStatus.NEW;
        this.taskType = taskType;
    }

    //Конструктор класса
    public Task(Integer num, String name, String details, LocalDateTime startTime, Duration duration) {
        this.num = num;
        this.name = name;
        this.details = details;
        status = TaskStatus.NEW;
        taskType = TaskType.TASK;
        this.startTime = startTime;
        this.duration = duration;
    }

    //Конструктор класса
    public Task(String name, String details, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.details = details;
        status = TaskStatus.NEW;
        taskType = TaskType.TASK;
        this.startTime = startTime;
        this.duration = duration;
    }

    //Конструктор класса
    public Task(Integer num, String name, String details, TaskType taskType, LocalDateTime startTime, Duration duration) {
        this.num = num;
        this.name = name;
        this.details = details;
        status = TaskStatus.NEW;
        this.taskType = taskType;
        this.startTime = startTime;
        this.duration = duration;
    }

    //Конструктор
    public Task(String name, String details, TaskType taskType, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.details = details;
        status = TaskStatus.NEW;
        this.taskType = taskType;
        this.startTime = startTime;
        this.duration = duration;
    }

    //Получение имени задачи
    public String getName() {
        return name;
    }

    //Задание имени задачи
    public void setName(String name) {
        this.name = name;
    }

    //Получение описания
    public String getDetails() {
        return details;
    }

    //Задание описания
    public void setDetails(String details) {
        this.details = details;
    }

    //Получние номера задачи
    public Integer getNum() {
        return num;
    }

    //Задание номера задачи
    public void setNum(Integer num) {
        this.num = num;
    }

    //Получение статуса
    public TaskStatus getStatus() {
        return status;
    }

    //Задание статуса
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    //Получение типа задачи
    public TaskType getType(){
        return taskType;
    }

    //Получение длительности выполнения задачи
    public Duration getDuration() {
        return duration;
    }

    //Задание длительности выполнения задачи
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    //Получение даты начала выполнения задачи
    public LocalDateTime getStartTime() {
        return startTime;
    }

    //Задание даты начала выполнения задачи
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    //Вычисление времени окончания задачи
    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null)
            return startTime.plusSeconds(duration.toSeconds());
        else
            return null;
    }

    //Отображение задачи
    @Override
    public String toString() {
        return getNum() + "," +
                getType() + "," +
                getName() + "," +
                getStatus() + "," +
                getDetails()  + ",," +
                getStartTime() + "," +
                (getDuration() == Duration.ZERO ? "" : getDuration());
    }
}