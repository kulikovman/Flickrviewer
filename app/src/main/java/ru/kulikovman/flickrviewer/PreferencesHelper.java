package ru.kulikovman.flickrviewer;

import android.content.Context;
import android.preference.PreferenceManager;

public class PreferencesHelper {

    public static String loadSearchQuery(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.search_query), "");
    }

    public static void saveSearchQuery(Context context, String searchQuery) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(context.getString(R.string.search_query), searchQuery)
                .apply();
    }
}
