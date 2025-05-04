package agent.transformer;


import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Properties;
import javassist.*;

public class MethodInterceptorTransformer implements ClassFileTransformer {

    private Properties config;
    private final long statInterval;

    public MethodInterceptorTransformer() {
        loadProperties();
        this.statInterval = Long.parseLong(config.getProperty("statInterval", "1")) * 1000;
    }

    // Load properties from resources
    private void loadProperties() {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("application_xx.properties")) {
            if (inputStream == null) {
                System.err.println("Unable to find application_xx.properties in the classpath");
                return;
            }
            config = new Properties();
            config.load(inputStream);
            System.out.println("Loaded properties: " + config);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        try {
            String targetClassName = className.replace("/", ".");
            String[] instrumentedClasses = config.getProperty("instrumentedClasses", "").split(",");
            boolean isInstrumented = false;

            for (String instrumentedClass : instrumentedClasses) {
                if (targetClassName.equals(instrumentedClass.trim())) {
                    isInstrumented = true;
                    break;
                }
            }

            if (!isInstrumented) {
                return null;
            }

            ClassPool classPool = ClassPool.getDefault();
            classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
            CtClass ctClass = classPool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));

            CtMethod[] ctMethods = ctClass.getDeclaredMethods();
            for (CtMethod ctMethod : ctMethods) {
                ctMethod.addLocalVariable("startTime", CtClass.longType);
                ctMethod.addLocalVariable("executionTime", CtClass.longType);

                ctMethod.insertBefore("{ startTime = System.currentTimeMillis(); }");
                ctMethod.insertAfter("{ executionTime = System.currentTimeMillis() - startTime; " +
                        "agent.helper.StatsHelper.updateStats(executionTime, " + statInterval + "L); }");


            }

            return ctClass.toBytecode();  // Return the modified bytecode
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
