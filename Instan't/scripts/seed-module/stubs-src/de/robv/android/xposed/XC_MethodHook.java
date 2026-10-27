package de.robv.android.xposed;

/** Compile-only stub of the legacy Xposed API (see IXposedHookLoadPackage). */
public class XC_MethodHook {

    public XC_MethodHook() {}

    public XC_MethodHook(int priority) {}

    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {}

    protected void afterHookedMethod(MethodHookParam param) throws Throwable {}

    public static class MethodHookParam {
        public java.lang.reflect.Member method;
        public Object thisObject;
        public Object[] args;
    }
}
