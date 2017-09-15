package com.battlelancer.seriesguide.jobs.episodes;

import android.content.Context;
import android.text.format.DateUtils;
import com.battlelancer.seriesguide.appwidget.ListWidgetProvider;
import com.battlelancer.seriesguide.provider.SeriesGuideContract;
import com.battlelancer.seriesguide.util.EpisodeTools;
import com.uwetrottmann.seriesguide.backend.episodes.model.Episode;

public class ShowWatchedJob extends ShowBaseJob {

    private final long currentTime;

    public ShowWatchedJob(int showTvdbId, int flagValue, long currentTime) {
        super(showTvdbId, flagValue, JobAction.SHOW_WATCHED);
        this.currentTime = currentTime;
    }

    @Override
    public String getDatabaseSelection() {
        if (EpisodeTools.isUnwatched(getFlagValue())) {
            // set unwatched
            // include watched or skipped episodes
            return SeriesGuideContract.Episodes.SELECTION_WATCHED_OR_SKIPPED
                    + " AND " + SeriesGuideContract.Episodes.SELECTION_NO_SPECIALS;
        } else {
            // set watched or skipped
            // do NOT mark watched episodes again to avoid trakt adding a new watch
            // only mark episodes that have been released until within the hour
            return SeriesGuideContract.Episodes.FIRSTAIREDMS + "<=" + (currentTime
                    + DateUtils.HOUR_IN_MILLIS)
                    + " AND " + SeriesGuideContract.Episodes.SELECTION_HAS_RELEASE_DATE
                    + " AND " + SeriesGuideContract.Episodes.SELECTION_UNWATCHED_OR_SKIPPED
                    + " AND " + SeriesGuideContract.Episodes.SELECTION_NO_SPECIALS;
        }
    }

    @Override
    protected String getDatabaseColumnToUpdate() {
        return SeriesGuideContract.Episodes.WATCHED;
    }

    @Override
    public boolean applyLocalChanges(Context context, boolean requiresNetworkJob) {
        if (!super.applyLocalChanges(context, requiresNetworkJob)) {
            return false;
        }

        int lastWatchedEpisodeTvdbId = EpisodeTools.isUnwatched(getFlagValue())
                ? 0 /* just reset */
                : -1 /* we don't care */;

        // set a new last watched episode
        // set last watched time to now if marking as watched or skipped
        updateLastWatched(context, lastWatchedEpisodeTvdbId,
                !EpisodeTools.isUnwatched(getFlagValue()));

        ListWidgetProvider.notifyAllAppWidgetsViewDataChanged(context);

        return true;
    }
}
