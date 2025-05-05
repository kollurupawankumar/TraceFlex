package agent.helper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class StatsHelper {

    static class MethodStats {
        AtomicLong invocationCount = new AtomicLong();
        AtomicLong totalExecutionTime = new AtomicLong();
        volatile long maxExecutionTime = 0;
        volatile long minExecutionTime = Long.MAX_VALUE;
        volatile long lastLogTime = System.currentTimeMillis();
    }

    private static final ConcurrentHashMap<String, MethodStats> statsMap = new ConcurrentHashMap<>();

    public static void updateStats(String methodKey, long executionTime, long statInterval) {
        MethodStats stats = statsMap.computeIfAbsent(methodKey, k -> new MethodStats());
        stats.invocationCount.incrementAndGet();
        stats.totalExecutionTime.addAndGet(executionTime);
        stats.maxExecutionTime = Math.max(stats.maxExecutionTime, executionTime);
        stats.minExecutionTime = Math.min(stats.minExecutionTime, executionTime);

        long now = System.currentTimeMillis();
        if (now - stats.lastLogTime >= statInterval) {
            long count = stats.invocationCount.get();
            double throughput = count / ((double)statInterval / 1000);
            System.out.printf(
                    "[%s] Invocation Count: %d, Total Time: %d ms, Max: %d ms, Min: %d ms, Throughput: %.2f/sec%n",
                    methodKey, count, stats.totalExecutionTime.get(), stats.maxExecutionTime, stats.minExecutionTime, throughput
            );
            stats.lastLogTime = now;
        }
    }
}
