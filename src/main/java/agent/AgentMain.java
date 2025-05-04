package agent;

import agent.transformer.MethodInterceptorTransformer;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Properties;

public class AgentMain {

    private static Properties agentConfig;

    public static void premain(String agentArgs, Instrumentation inst) throws IOException {
        System.out.println("Agent is loaded");
        // Add transformer to intercept methods
        inst.addTransformer(new MethodInterceptorTransformer());
    }

    // Load properties from file
    private static void loadConfig(String configFile) throws IOException {
        agentConfig = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            agentConfig.load(fis);
        }
    }
}
