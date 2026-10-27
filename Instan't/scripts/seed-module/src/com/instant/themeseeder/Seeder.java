package com.instant.themeseeder;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;

import java.util.Iterator;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Instan't palette seeder: an embedded Xposed module that writes the theme + feature
 * configuration into Instagram's own SharedPreferences before InstaEclipse loads them.
 *
 * <p>Loaded in-process by LSPatch alongside InstaEclipse-Legacy (both via assets/xposed_init).
 * It hooks Application#attach; the InstaEclipse module re-reads preferences in its own
 * after-hook, so the configuration is effective on the first launch with zero taps.
 *
 * <p>Stamps every seeded key with a {@code __igts_} timestamp so a companion-app value older
 * than the seed can never overwrite it (CompanionSync merge semantics). The seed runs whenever
 * {@link SeedData#VERSION} differs from the recorded one — a replayed profile or palette in a
 * new build — and never touches the user's own later edits.
 */
public class Seeder implements IXposedHookLoadPackage {

    private static final String TAG = "InstantSeeder";
    private static final String PREFS_NAME = "instaeclipse_prefs";
    private static final String VERSION_KEY = "__seeder_version";
    private static final String IG_PACKAGE = "com.instagram.android";

    private volatile boolean hooked;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        if (!IG_PACKAGE.equals(lpparam.packageName)) return;
        if (lpparam.processName != null && !lpparam.processName.equals(lpparam.packageName)) return;
        if (hooked) return;
        hooked = true;
        try {
            XposedHelpers.findAndHookMethod(
                    "android.app.Application", lpparam.classLoader, "attach",
                    Context.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                Context context = (Context) param.args[0];
                                if (context != null) seed(context);
                            } catch (Throwable t) {
                                Log.w(TAG, "seeding failed", t);
                            }
                        }
                    });
        } catch (Throwable t) {
            Log.w(TAG, "Application#attach hook failed", t);
        }
    }

    private static void seed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (prefs.getInt(VERSION_KEY, -1) == SeedData.VERSION) return;

        SharedPreferences.Editor editor = prefs.edit();
        long now = System.currentTimeMillis();

        applyJson(editor, SeedData.FEATURES_JSON, now);

        // Theme block: dark mode uses the custom palette (preset id 0), light mode the built-in
        // mint preset (custom palettes are not supported in light mode, InstaEclipse #251).
        editor.putBoolean("customThemeEnabled", true);
        editor.putInt("themePresetId", 0);
        editor.putInt("themeLightPresetId", SeedData.LIGHT_PRESET_ID);
        editor.putString("themePaletteJson", SeedData.PALETTE_JSON);
        editor.putBoolean("themeLightMigratedV1", true); // skip the module's own v1 migration
        editor.putLong("__igts_customThemeEnabled", now);
        editor.putLong("__igts_themePresetId", now);
        editor.putLong("__igts_themeLightPresetId", now);
        editor.putLong("__igts_themePaletteJson", now);

        editor.putInt(VERSION_KEY, SeedData.VERSION);
        editor.apply();
        Log.i(TAG, "seeded Instan't configuration v" + SeedData.VERSION);
    }

    /** Writes every boolean/int/string entry of a flat JSON object plus its IG-side stamp. */
    private static void applyJson(SharedPreferences.Editor editor, String json, long stamp) {
        try {
            JSONObject object = new JSONObject(json);
            Iterator<String> keys = object.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = object.get(key);
                if (value instanceof Boolean) {
                    editor.putBoolean(key, (Boolean) value);
                } else if (value instanceof Integer) {
                    editor.putInt(key, (Integer) value);
                } else if (value instanceof String) {
                    editor.putString(key, (String) value);
                } else {
                    continue;
                }
                editor.putLong("__igts_" + key, stamp);
            }
        } catch (Throwable t) {
            Log.w(TAG, "embedded configuration is not valid JSON", t);
        }
    }
}
