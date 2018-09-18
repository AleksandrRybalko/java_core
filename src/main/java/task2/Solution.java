package task2;

import java.util.Calendar;
import java.util.Collections;

/**
 * Created by arybalko on 17/09/2018.
 */
public class Solution {
    public static void main(String[] args) {
        new Solution().run();
    }

    private void run() {
        WorkQueue workQueue = new WorkQueue(10);
        try {
            workQueue.execute(() -> System.out.println("Task 1 is running!"), 100);
            workQueue.execute(() -> System.out.println("Task 2 is running!"), 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
