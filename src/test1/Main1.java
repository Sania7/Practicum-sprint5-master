package test1;

import java.util.TreeSet;

public class Main1 {
    public static void main(String[] args) {
        TreeSet<String> playerSet = new TreeSet<>();
        playerSet.add("Иван");
        playerSet.add("Богдан");
        playerSet.add("Дима");
        playerSet.add("Надя");
        playerSet.add("Катя");
        playerSet.add("Слава");
        playerSet.add("Женя");
        playerSet.add("Виктор");
        playerSet.add("Сергей");
        playerSet.add("Александр");
        playerSet.add("Леша");
        System.out.println("Original Set" + playerSet);
        System.out.println("First name " + playerSet.first());
        System.out.println("Last name " + playerSet.last());
        TreeSet<String> newPlySet = (TreeSet<String>) playerSet.subSet("Mahi", "Virat");
        System.out.println("Sub Set " + newPlySet);
    }
}
