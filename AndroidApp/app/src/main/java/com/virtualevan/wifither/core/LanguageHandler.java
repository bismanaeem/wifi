package com.virtualevan.wifither.core;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

/**
 * Created by VirtualEvan on 26/05/2017.
 */

public class LanguageHandler {
    //Select locale
    public static void changeLocale(Resources res, String locale) {
        Configuration config;
        config = new Configuration(res.getConfiguration());

        //Locale selector
        switch(locale) {
            case "es":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    config.setLocale(new Locale("es"));
                } else {
                    config.locale = new Locale("es");
                }

                break;
            case "en":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    config.setLocale(Locale.ENGLISH);
                } else {
                    config.locale = Locale.ENGLISH;
                }
                break;
        }

        res.updateConfiguration( config, res.getDisplayMetrics() );
    }

    public static String getLocale(Resources res) {
        Locale locale;

        //Save locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = res.getConfiguration().getLocales().get(0);
        }
        else {
            locale = res.getConfiguration().locale;
        }

        return locale.toString();
    }
}
