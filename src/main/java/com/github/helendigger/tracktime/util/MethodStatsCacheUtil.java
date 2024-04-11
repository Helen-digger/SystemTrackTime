package com.github.helendigger.tracktime.util;

import com.github.helendigger.tracktime.model.TimeRecord;

public class MethodStatsCacheUtil {
    public static String getCacheKey(String methodName, String className, String packageName) {
        StringBuilder sb = new StringBuilder();
        if (methodName != null) {
            sb.append(methodName);
            sb.append("#");
        }
        if (className != null) {
            sb.append(className);
            sb.append(".");
        }
        if (packageName != null) {
            sb.append(packageName);
        }
        return sb.toString();
    }
    public static <S extends TimeRecord> String getMethodKey(S record) {
        if (record != null) {
            return getCacheKey(record.getMethodName(), record.getClassName(), record.getPackageName());
        }
        return "";
    }
}
