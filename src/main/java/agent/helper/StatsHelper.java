package agent.helper;

import java.util.concurrent.atomic.AtomicLong;

public class StatsHelper {
    // Stats tracking variables
    public static AtomicLong invocationCount = new AtomicLong(0);
    public static AtomicLong totalExecutionTime = new AtomicLong(0);
    public static long maxExecutionTime = 0;
    public static long minExecutionTime = Long.MAX_VALUE;
    public static long lastInvocationTime = 0;

    // Method to update stats
    public static void updateStats(long executionTime, long statInterval) {
        invocationCount.incrementAndGet();
        totalExecutionTime.addAndGet(executionTime);
        if (executionTime > maxExecutionTime) {
            maxExecutionTime = executionTime;
        }
        if (executionTime < minExecutionTime) {
            minExecutionTime = executionTime;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastInvocationTime >= statInterval) {
            System.out.println("Invocation Count: " + invocationCount.get() + ", "
                    + "Total Execution Time: " + totalExecutionTime.get() + " ms, "
                    + "Max Execution Time: " + maxExecutionTime + " ms, "
                    + "Min Execution Time: " + minExecutionTime + " ms, "
                    + "Throughput: " + invocationCount.get() / (statInterval / 1000) + " invocations/s");
            lastInvocationTime = currentTime;
        }
    }
}
