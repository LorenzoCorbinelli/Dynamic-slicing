package corbinelli.lorenzo.dynamicslicing;

import java.util.HashMap;
import java.util.Map;

public final class VariableName {

    private final String variableName = "x";
    private final String returnVariableName = "y";
    private int counter = 1;
    private static VariableName INSTANCE;
    private Map<Integer, String> variables = new HashMap<>();

    private VariableName() {}

    public static VariableName getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VariableName();
        }
        return INSTANCE;
    }

    public String getVariableName(Object obj) {
        int hash = System.identityHashCode(obj);
        if(variables.containsKey(hash)) {
            return variables.get(hash);
        }
        String varName = variableName + hash;
        variables.put(hash, varName);
        return varName;
    }

    public String getReturnVariableName(Object obj) {
        int hash = System.identityHashCode(obj);
        // a new name for each variable representing a return value
        String varName = returnVariableName + counter++;
        if(!variables.containsKey(hash)) {
            variables.put(hash, varName);
        }
        return varName;
    }

    public boolean isTheObjectAlreadyCreated(Object obj) {
        return variables.containsKey(System.identityHashCode(obj));
    }

}
