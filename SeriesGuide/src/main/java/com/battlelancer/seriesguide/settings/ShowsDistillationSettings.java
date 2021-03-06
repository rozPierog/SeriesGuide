package com.battlelancer.seriesguide.settings;

import android.content.Context;
import android.preference.PreferenceManager;

import static com.battlelancer.seriesguide.provider.SeriesGuideContract.Shows;

/**
 * Provides settings used to filter and sort displayed shows in {@link
 * com.battlelancer.seriesguide.ui.ShowsFragment}.
 */
public class ShowsDistillationSettings {

    public static String KEY_SORT_ORDER = "com.battlelancer.seriesguide.sort.order";
    public static String KEY_SORT_FAVORITES_FIRST
            = "com.battlelancer.seriesguide.sort.favoritesfirst";
    public static String KEY_FILTER_FAVORITES = "com.battlelancer.seriesguide.filter.favorites";
    public static String KEY_FILTER_UNWATCHED = "com.battlelancer.seriesguide.filter.unwatched";
    public static String KEY_FILTER_UPCOMING = "com.battlelancer.seriesguide.filter.upcoming";
    public static String KEY_FILTER_HIDDEN = "com.battlelancer.seriesguide.filter.hidden";

    /**
     * Builds an appropriate SQL sort statement for sorting shows.
     */
    public static String getSortQuery(int sortOrderId, boolean isSortFavoritesFirst,
            boolean isSortIgnoreArticles) {
        StringBuilder query = new StringBuilder();

        if (isSortFavoritesFirst) {
            query.append(ShowsSortQuery.FAVORITES_FIRST);
        }

        if (sortOrderId == ShowsSortOrder.OLDEST_EPISODE_ID) {
            query.append(ShowsSortQuery.OLDEST_EPISODE);
        } else if (sortOrderId == ShowsSortOrder.LATEST_EPISODE_ID) {
            query.append(ShowsSortQuery.LATEST_EPISODE);
        } else if (sortOrderId == ShowsSortOrder.LAST_WATCHED_ID) {
            query.append(ShowsSortQuery.LAST_WATCHED);
        } else if (sortOrderId == ShowsSortOrder.LEAST_REMAINING_EPISODES_ID) {
            query.append(ShowsSortQuery.REMAINING_EPISODES);
        }
        // always sort by title at last
        query.append(isSortIgnoreArticles ?
                Shows.SORT_TITLE_NOARTICLE : Shows.SORT_TITLE);

        return query.toString();
    }

    /**
     * Returns the id as of {@link com.battlelancer.seriesguide.settings.ShowsDistillationSettings.ShowsSortOrder}
     * of the current show sort order.
     */
    public static int getSortOrderId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_SORT_ORDER, 0);
    }

    public static boolean isSortFavoritesFirst(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_SORT_FAVORITES_FIRST, true);
    }

    public static boolean isFilteringFavorites(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_FILTER_FAVORITES, false);
    }

    public static boolean isFilteringUnwatched(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_FILTER_UNWATCHED, false);
    }

    public static boolean isFilteringUpcoming(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_FILTER_UPCOMING, false);
    }

    public static boolean isFilteringHidden(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_FILTER_HIDDEN, false);
    }

    private interface ShowsSortQuery {
        // by oldest next episode, then continued first
        String OLDEST_EPISODE = Shows.NEXTAIRDATEMS + " ASC,"
                + Shows.SORT_STATUS + ",";
        // by latest next episode, then continued first
        String LATEST_EPISODE = Shows.SORT_LATEST_EPISODE + ",";
        // by latest watched first
        String LAST_WATCHED = Shows.LASTWATCHED_MS + " DESC,";
        // by least episodes remaining to watch, then continued first
        String REMAINING_EPISODES = Shows.UNWATCHED_COUNT + " ASC,"
                + Shows.SORT_STATUS + ",";
        // add as prefix to sort favorites first
        String FAVORITES_FIRST = Shows.FAVORITE + " DESC,";
    }

    /**
     * Used by {@link com.battlelancer.seriesguide.ui.ShowsFragment} loader to sort the list of
     * shows.
     */
    public interface ShowsSortOrder {
        int TITLE_ID = 0;
        // @deprecated Only supporting alphabetical sort order going forward.
        // int TITLE_REVERSE_ID = 1;
        int OLDEST_EPISODE_ID = 2;
        int LATEST_EPISODE_ID = 3;
        int LAST_WATCHED_ID = 4;
        int LEAST_REMAINING_EPISODES_ID = 5;
    }
}
