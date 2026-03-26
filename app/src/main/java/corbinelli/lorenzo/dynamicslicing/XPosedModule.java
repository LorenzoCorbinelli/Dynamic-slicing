package corbinelli.lorenzo.dynamicslicing;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XPosedModule implements IXposedHookLoadPackage {

    private static final String LOG_TAG = "LSPosedLog";
    private final Serializer serializer = new Serializer(LOG_TAG);
    private final JSONReader JSONReader = new JSONReader("res/raw/hooks.json");
    private final VariableName variableName = VariableName.getInstance();

    private void addCallback(Object[] parametersAndHook) {
        parametersAndHook[parametersAndHook.length - 1] = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Member hockedMember = param.method;
                String beforeMemberName = "";
                String assertStatement = "";
                if (hockedMember instanceof Constructor) {
                    beforeMemberName = hockedMember.getDeclaringClass().getCanonicalName()
                            + " " + variableName.getVariableName(param.thisObject) + " = new ";
                } else {    // it's a method
                    String returnVariableName = variableName.getVariableName(param.getResult());
                    Method method = (Method)hockedMember;
                    boolean isVoid = method.getReturnType().equals(void.class);
                    if(!isVoid) {
                        beforeMemberName = method.getReturnType().getCanonicalName() + " " + returnVariableName + " = ";
                        // if it is not void manage also the returned value
                        assertStatement = "assert Objects.equals(" + returnVariableName + ", gson.fromJson(\""
                                + serializer.serializeObjectAndEscapeJson(param.getResult())
                                + "\", " + method.getReturnType().getCanonicalName() + ".class));";
                    }
                    if (Modifier.isStatic(method.getModifiers())) {
                        // take the class name
                        beforeMemberName += method.getDeclaringClass().getCanonicalName();
                    } else {
                        // serialize the "this" reference
                        beforeMemberName += serializer.logObjectSerialization(param.thisObject);
                    }
                    beforeMemberName += ".";
                }

                String memberName = hockedMember.getName();
                StringBuilder args = new StringBuilder();

                for (Object arg : param.args) {
                    serializer.extractArgumentValues(arg, args);
                }
                // take off the last comma and space
                if (args.length() > 2) {
                    args.setLength(args.length() - 2);
                }

                String statement = beforeMemberName + memberName + "(" + args + ");";
                Log.i(LOG_TAG, statement);
                if(!assertStatement.isEmpty())
                    Log.i(LOG_TAG, assertStatement);
            }
        };
    }

    private Object[] getParametersTypeAndCallback(JSONObject hook) throws JSONException {
        JSONArray jsonParameters = hook.getJSONArray("parametersType");
        Object[] parametersAndHook = new Object[jsonParameters.length() + 1];   // as the last element will be the callback
        for (int i = 0; i < jsonParameters.length(); i++) {
            // take each parameterType as string
            parametersAndHook[i] = jsonParameters.getString(i);
        }
        addCallback(parametersAndHook);
        return parametersAndHook;
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Log.d("LSPosedDebug", "Hooking into " + lpparam.packageName);

        try {
            JSONArray jsonArray = JSONReader.readJSON();

            for (int i = 0; i < jsonArray.length(); i++) {
                // take each element in the json
                JSONObject hook = jsonArray.getJSONObject(i);
                Object[] parametersAndHook = getParametersTypeAndCallback(hook);

                if (hook.has("methodName")) {
                    XposedHelpers.findAndHookMethod(
                            hook.getString("className"),
                            lpparam.classLoader,
                            hook.getString("methodName"),
                            parametersAndHook
                    );
                } else {
                    XposedHelpers.findAndHookConstructor(
                            hook.getString("className"),
                            lpparam.classLoader,
                            parametersAndHook
                    );
                }
            }
        } catch(Throwable e) {
            Log.e("LSPosedDebug", "LSPosed Error: " + Log.getStackTraceString(e));
        }
    }
}
