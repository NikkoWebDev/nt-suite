package de.robv.android.xposed;

/** Compile-only stub of the legacy Xposed API (see IXposedHookLoadPackage). */
public class XposedHelpers {

    private XposedHelpers() {}

    public static Object findAndHookMethod(
            String className, ClassLoader classLoader, String methodName,
            Object... parameterTypesAndCallback) {
        throw new UnsupportedOperationException("stub only");
    }

    public static Object findAndHookMethod(
            Class<?> clazz, String methodName, Object... parameterTypesAndCallback) {
        throw new UnsupportedOperationException("stub only");
    }
}
