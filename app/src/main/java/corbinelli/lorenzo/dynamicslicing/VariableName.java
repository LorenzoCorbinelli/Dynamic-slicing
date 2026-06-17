package corbinelli.lorenzo.dynamicslicing;

import java.util.HashMap;
import java.util.Map;

public final class VariableName {

    private final String variableName = "x";
    private final String returnVariableName = "y";
    private int counterX = 1;
    private int counterY = 1;
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
        if(obj == null)
            return "null";
        int hash = getHash(obj);
        if(variables.containsKey(hash)) {
            return variables.get(hash);
        }
        String varName = variableName + counterX++;
        variables.put(hash, varName);
        return varName;
    }

    public String getReturnVariableName(Object obj) {
        if(obj == null)
            return "null";
        int hash = getHash(obj);
        // a new name for each variable representing a return value
        String varName = returnVariableName + counterY++;
        if(!variables.containsKey(hash)) {
            variables.put(hash, varName);
        }
        return varName;
    }

    public boolean isTheObjectAlreadyCreated(Object obj) {
        if(obj == null)
            return true;
        return variables.containsKey(getHash(obj));
    }

    private int getHash(Object obj) {
        return obj.hashCode();
    }

}
