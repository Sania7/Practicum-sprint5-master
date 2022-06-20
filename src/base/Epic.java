package base;

import util.TaskStatus;
import util.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<SubTask> subTasks;

    private LocalDateTime endTime;

    public Epic(Integer num, String name, String details) {
        super(num, name, details, TaskType.EPIC);
        subTasks = new ArrayList<>();
    }

    //Получение статуса Эпика
    public TaskStatus getStatus() {
        updateDates();
        TaskStatus result;
        int statusSum = TaskStatus.NEW.ordinal();

        if (subTasks.size() == 0) {  //Если нет подзадач
            result = TaskStatus.NEW;  //Для пустого эпика вернуть статус NEW
        } else {  //Статус непустых эпиков определяется статусами подзадач
            for (SubTask subTask : subTasks)
                statusSum += subTask.getStatus().ordinal();

            if (statusSum == TaskStatus.NEW.ordinal()){ //Все подзадачи новые
                result = TaskStatus.NEW;
            } else if (statusSum == (TaskStatus.DONE.ordinal() * subTasks.size())){  //Все подзадачи выполнены
                result = TaskStatus.DONE;
            } else {
                result = TaskStatus.IN_PROGRESS;
            }
        }
        return result;
    }

    // обновление дат начала, окончания, продолжительности эпика
    public void updateDates() {
        Duration sumDuration = null;
        LocalDateTime firstDate = null;
        LocalDateTime lastDate = null;

        if (subTasks != null){
            for (SubTask subTask : subTasks){
                if (subTask.getDuration()!=null && subTask.getStartTime()!=null){
                    if (firstDate == null || firstDate.isAfter(subTask.getStartTime()))
                        firstDate = subTask.getStartTime();
                    if(lastDate == null || lastDate.isBefore(subTask.getEndTime()))
                        lastDate = subTask.getEndTime();
                    if (sumDuration == null)
                        sumDuration = subTask.getDuration();
                    else
                        sumDuration = sumDuration.plus(subTask.getDuration());
                }
            }
        }
        duration = sumDuration;
        startTime = firstDate;
        endTime = lastDate;
    }

    // получить дату окончания
    public LocalDateTime getEndTime() {
        return endTime;
    }
    //Получение списка всех подзадач
    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    //Отображение Эпика
    @Override
    public String toString() {
        return getNum() + "," + getType() + "," + getName() + "," + getStatus() + "," + getDetails() + ",";
    }
}
