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

    /**
     * This method check if a given object has already been encountered.
     * In other words, it checks if the given object has already an associated variable name.
     * @param obj The object to check
     * @return True if the object has not an associated variable name (it has never been encountered before).
     * False otherwise.
     */
    public boolean isANewObject(Object obj) {
        return !ihm.containsKey(obj);
    }

    /**
     * This method returns the name of the variable associated to a given object,
     * if that object it has been already encountered,
     * otherwise it return a new variable name associated to that object.
     * @param obj The object of which the associated variable name will be returned
     * @return The variable name already associated to the given object,
     * or a new variable name if the given object is never been encountered before
     */
    public String getVariableName(Object obj) {
        return variableName + ihm.computeIfAbsent(obj, o -> counter.incrementAndGet());
    }
}
