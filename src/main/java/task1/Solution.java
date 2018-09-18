package task1;

import externalSorting.*;

import java.io.IOException;
import java.util.Comparator;

/**
 * Created by arybalko on 17/09/2018.
 */
public class Solution {
    public static void main(String[] args) {
        new Solution().run();
    }

    void run() {
        String fileName = "test.csv";
        try {
            ExternalSorting.sort(fileName, (o1, o2) -> {
                Integer o11 = Integer.parseInt(o1[0].toString());
                String o12 = o1[1].toString();


                Integer o21 = Integer.parseInt(o2[0].toString());
                String o22 = o2[1].toString();
                if (o11.compareTo(o21) != 0) {
                    return o11.compareTo(o21);
                } else {
                    return o12.compareTo(o22);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
