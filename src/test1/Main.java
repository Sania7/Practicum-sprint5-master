package test1;

import java.time.Instant;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int chickenUnixSecond = new Random().nextInt(1000000000);
        Instant chickenMoment = Instant.ofEpochSecond(chickenUnixSecond);
        // получаем рандомно число
        int eggUnixSecond = new Random().nextInt(1_000_000_000);
        // переводим число в
        Instant eggMoment = Instant.ofEpochSecond(eggUnixSecond);

        System.out.println(chickenMoment);
        System.out.println();
        System.out.println(eggMoment);
        System.out.println();
        System.out.println(chickenMoment.isBefore(eggMoment));
    }
}
