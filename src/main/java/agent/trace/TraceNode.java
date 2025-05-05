package agent.trace;


import java.util.ArrayList;
import java.util.List;

public class TraceNode {
    public String methodName;
    public long startTime;
    public long endTime;
    public List<TraceNode> children = new ArrayList<>();

    public TraceNode(String methodName) {
        this.methodName = methodName;
        this.startTime = System.currentTimeMillis();
    }

    public void end() {
        this.endTime = System.currentTimeMillis();
    }

    public long getDuration() {
        return endTime - startTime;
    }
}