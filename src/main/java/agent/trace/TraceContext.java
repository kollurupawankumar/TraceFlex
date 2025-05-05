package agent.trace;

import java.util.Stack;
import java.util.UUID;

public class TraceContext {
    private static final ThreadLocal<Stack<TraceNode>> nodeStack = ThreadLocal.withInitial(Stack::new);
    private static final ThreadLocal<TraceNode> rootNode = new ThreadLocal<>();
    private static final ThreadLocal<String> traceId = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());

    public static void startTrace(String methodName) {
        TraceNode root = new TraceNode(methodName);
        rootNode.set(root);
        nodeStack.get().push(root);
    }

    public static void enter(String methodName) {
        TraceNode node = new TraceNode(methodName);
        Stack<TraceNode> stack = nodeStack.get();
        if (stack.isEmpty()) {
            startTrace(methodName);
        } else {
            stack.peek().children.add(node);
            stack.push(node);
        }
    }

    public static void exit() {
        TraceNode node = nodeStack.get().pop();
        node.end();
        if (nodeStack.get().isEmpty()) {
            dumpTrace();
            nodeStack.remove();
            rootNode.remove();
            traceId.remove();
        }
    }

    public static void dumpTrace() {
        TraceNode root = rootNode.get();
        System.out.println("\n==== Trace Tree [" + traceId.get() + "] ====");
        printNode(root, 0);
    }

    private static void printNode(TraceNode node, int indent) {
        System.out.printf("%s%s (%d ms)\n", "  ".repeat(indent), node.methodName, node.getDuration());
        for (TraceNode child : node.children) {
            printNode(child, indent + 1);
        }
    }
}
