package com.example.geography_quiz_java.utils;

import android.content.Context;
import android.content.res.Configuration;
import java.util.Locale;

public class TranslationUtils {

    // Private constructor to prevent instantiation
    private TranslationUtils() {}

    public static String getTranslatedString(Context context, int resId, String languageCode) {
        Configuration config = new Configuration(context.getResources().getConfiguration());
        Locale locale = new Locale(languageCode);
        config.setLocale(locale);
        Context localizedContext = context.createConfigurationContext(config);
        return localizedContext.getResources().getString(resId);
    }

    public static String getTranslatedStringWithFormat(
            Context context,
            int resId,
            String languageCode,
            Object... args
    ) {
        String baseString = getTranslatedString(context, resId, languageCode);
        return String.format(baseString, args);
    }
}