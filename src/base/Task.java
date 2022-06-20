package base;

import util.TaskStatus;
import util.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private String name;        //Название
    private String details;     //Описание/дополнение
    private Integer num;        //Уникальный идентификационный номер задачи
    private TaskStatus status;  //Статус задачи
    private TaskType taskType;//Тмп задачи

    protected LocalDateTime startTime; // дата начала

    protected Duration duration; // продолжительность


    //Конструктор класса
    public Task(Integer num, String name, String details) {
        this.num = num;
        this.name = name;
        this.details = details;
        status = TaskStatus.NEW;
        this.taskType = TaskType.TASK;
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
    public Task(String name, String details, TaskType taskType,LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.details = details;
        status = TaskStatus.NEW;
        this.taskType = taskType;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(Integer num, String details, String name, LocalDateTime startTime, Duration duration) {
        this.num = num;
        this.details = details;
        this.name = name;
        this.startTime = startTime;
        this.duration = duration;

    }

    public Task(int num, String name, String details) {
        this.num = num;
        this.name = name;
        this.details = details;
    }

    public Task(String name, String details) {
        this.name = name;
        this.details = details;
    }

    public Task(String name, String details, TaskType taskType) {
        this.name = name;
        this.details = details;
        this.taskType = taskType;
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

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    // вычисление времени выполнения задачи
    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plusSeconds(duration.getSeconds());
        } else {
            return null;
        }
    }

    //Отображение задачи
    @Override
    public String toString() {
        return getNum()
                + ","
                + getType()
                + ","
                + getName()
                + ","
                + getStatus()
                + ","
                + getDetails()
                + ","
                + getStartTime()
                + ","
                + (getDuration() == Duration.ZERO ? "" : getDuration());
    }
}
