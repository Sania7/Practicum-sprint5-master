package managers;

import base.Task;
import util.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public class InMemoryHistoryManager implements HistoryManager {


    private HashMap<Integer, Node<Task>> nodeIndex = new HashMap<>();   //Индекс поиска задач в истории обращений
    private Node<Task> first;   //Первый узел двусвязного списка истории обращений к задачам
    private Node<Task> last;    //Последний узел двусвязного списка истории обращений к задачам

    //Добавление нового просмотра задачи в историю обращений (добавление/перенос в конец списка)
    @Override
    public void add(Task task){
        Node<Task> l = last;    //Временное сохранение последнего элемента
        Node<Task> newNode = null;

        //Если задача уже есть в списке - взять существующий узел
        if (nodeIndex.containsKey(task.getNum())){
            newNode = nodeIndex.get(task.getNum());
            unlink(newNode);    //Выключение узла из списка перед манипуляциями
        } else {
            newNode = new Node<>(l, task, null);   //Создание нового узла
            nodeIndex.put(task.getNum(), newNode);      //Вставка нового узла в индекс поиска
        }

        //Включение узла в конец списка
        linkLast(newNode);
    }

    //Удаление просмотра из истории
    @Override
    public void remove(int id){
        Node<Task> delNode = nodeIndex.get(id);
        if (delNode != null) {
            unlink(delNode);        //Отсоединение узла из списка
            nodeIndex.remove(id);   //Удаление узла из индекса поиска
        }
    }

    //Получение истории последних просмотров
    @Override
    public List<Task> getHistory(){
        List<Task> ret = new ArrayList<>();
        for (Node<Task> x = first; x != null; x = x.getNext())
            ret.add(x.getItem());

        return ret;
    }

    //Процедура выключения узла из списка
    private void unlink(Node<Task> node) {
        Node<Task> prev = node.getPrev();   //Предыдущий узел
        Node<Task> next = node.getNext();   //Следующий узел

        if (prev == null) {
            first = next;   //Это был первый элемент списка - теперь первым будет следующий
        } else {
            prev.setNext(next);
            node.setPrev(null);
        }

        if (next == null) {
            last = prev;    //Это был последний элемент - теперь последним станет предыдущий
        } else {
            next.setPrev(prev);
            node.setNext(null);
        }

    }

    //Процедура включения узла в конец списка
    private void linkLast(Node<Task> node) {
        Node<Task> l = last;    //Временное сохранение последнего элемента
        last = node;            //Вставка узла последним

        //Если список был пустым
        if (l == null)
            first = node;   //То этот же элемент будет и последним
        else
            l.setNext(node);

        node.setPrev(l);
    }

    //Очистка истории
    public void clear(){
        nodeIndex.clear();
        first = null;
        last = null;
    }

    //Формирование строкового отображения истории
    public String toString() {
        List<String> strList = new ArrayList<>();
        for(Task task : getHistory()){
            strList.add(String.valueOf(task.getNum()));
        }
        return String.join(",", strList);
    }

    static List<Integer> fromString(String str){
        List<Integer> ret = new ArrayList<>();
        String[] split = str.split(",");
        for (String num : split)
            ret.add(Integer.parseInt(num));
        return ret;
    }
}
