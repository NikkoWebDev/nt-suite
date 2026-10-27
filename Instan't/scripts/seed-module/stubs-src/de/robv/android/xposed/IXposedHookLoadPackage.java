package de.robv.android.xposed;

/**
 * Compile-only stub of the legacy Xposed API. The real classes come from LSPatch's
 * bridge at runtime; these stubs live in the stub classpath but are never emitted
 * into the module dex, so no duplicate classes can end up in the target process.
 */
public interface IXposedHookLoadPackage {
    void handleLoadPackage(de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam lpparam)
            throws Throwable;
}
