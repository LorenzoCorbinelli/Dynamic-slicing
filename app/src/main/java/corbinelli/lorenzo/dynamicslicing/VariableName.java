package corbinelli.lorenzo.dynamicslicing;

import java.util.IdentityHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class VariableName {

    private final String variableName = "x";
    private static VariableName INSTANCE;
    private IdentityHashMap<Object, Integer> ihm = new IdentityHashMap<>();
    private AtomicInteger counter = new AtomicInteger();

    private VariableName() {}

    public static VariableName getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VariableName();
        }
        return INSTANCE;
    }

    public boolean isANewObject(Object obj) {
        return !ihm.containsKey(obj);
    }

    public String getVariableName(Object obj) {
        return variableName + ihm.computeIfAbsent(obj, o -> counter.incrementAndGet());
    }

    public String getSimpleVariableName() {
        return variableName + counter.incrementAndGet();
    }
}
