package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {

    public static <T> List<T> sample(List<T> vals, int n) {
        Random rand = new Random();
        List<T> newList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int randomIndex = rand.nextInt(vals.size());
            newList.add(vals.get(randomIndex));
        }
        return newList;
    }

}
