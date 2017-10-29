package com.example.android.funkygridlibrary.common;

import android.content.Context;
import android.util.Log;

/**
 * Created by Paul Gallini on 10/29/17.
 */

public class BuildConfigUtils
{

    public static Object getBuildConfigValue (Context context, String fieldName)
    {
        Class<?> buildConfigClass = resolveBuildConfigClass(context);
        return getStaticFieldValue(buildConfigClass, fieldName);
    }

    public static Class<?> resolveBuildConfigClass (Context context)
    {
        int resId = context.getResources().getIdentifier("build_config_package",
                "string",
                context.getPackageName());
        if (resId != 0)
        {
            // defined in build.gradle
            try {
                return Class.forName(context.getString(resId) + ".BuildConfig");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        // not defined in build.gradle
        // try packageName + ".BuildConfig"
        return loadClass(context.getPackageName() + ".BuildConfig");

    }

    private static Class<?> loadClass (String className)
    {
        Log.i("BuildConfigUtils", "try class load : " + className);
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Object getStaticFieldValue (Class<?> clazz, String fieldName)
    {
        try { return clazz.getField(fieldName).get(null); }
        catch (NoSuchFieldException e) { e.printStackTrace(); }
        catch (IllegalAccessException e) { e.printStackTrace(); }
        return null;
    }
}