package de.robv.android.xposed.callbacks;

/** Compile-only stub of the legacy Xposed API (see IXposedHookLoadPackage). */
public class XC_LoadPackage {

    private XC_LoadPackage() {}

    public static class LoadPackageParam {
        public String packageName;
        public String processName;
        public ClassLoader classLoader;
        public android.content.pm.ApplicationInfo appInfo;
        public boolean isFirstApplication;
    }
}
