package ru.kulikovman.flickrviewer;

import android.content.Context;
import android.preference.PreferenceManager;

public class PreferencesHelper {

    public static String loadSearchQuery(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.search_query), null);
    }

    public static int loadPage(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(context.getString(R.string.search_query), 1);
    }

    public static void saveSearchQuery(Context context, String searchQuery) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(context.getString(R.string.search_query), searchQuery)
                .apply();
    }

    public static void savePage(Context context, int page) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(context.getString(R.string.page), page)
                .apply();
    }
}
