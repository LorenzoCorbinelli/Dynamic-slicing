package corbinelli.lorenzo.dynamicslicing;

import java.util.ArrayList;
import java.util.List;

public final class VariableName {

    private final String variableName = "x";
    private static VariableName INSTANCE;
    private List<Integer> hashCodeList = new ArrayList<>();

    private VariableName() {}

    public static VariableName getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VariableName();
        }
        return INSTANCE;
    }

    public String getVariableName(Object obj) {
        int hash = System.identityHashCode(obj);
        if(!hashCodeList.contains(hash))
            hashCodeList.add(hash);
        return variableName + hash;
    }

    public boolean isTheObjectAlreadyCreated(Object obj) {
        return hashCodeList.contains(System.identityHashCode(obj));
    }

}
