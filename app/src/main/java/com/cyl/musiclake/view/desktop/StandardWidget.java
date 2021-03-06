package com.cyl.musiclake.view.desktop;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.cyl.musiclake.MusicApp;
import com.cyl.musiclake.R;
import com.cyl.musiclake.common.NavigateUtil;
import com.cyl.musiclake.service.MusicPlayerService;
import com.cyl.musiclake.service.PlayManager;
import com.cyl.musiclake.utils.CoverLoader;


/**
 * Created by nv95 on 08.07.16.
 */

public class StandardWidget extends BaseWidget {

    @Override
    int getLayoutRes() {
        return R.layout.widget_standard;
    }

    @Override
    void onViewsUpdate(Context context, RemoteViews remoteViews, ComponentName serviceName, Bundle extras) {
        remoteViews.setOnClickPendingIntent(R.id.iv_next, PendingIntent.getService(
                context,
                REQUEST_NEXT,
                new Intent(context, MusicPlayerService.class)
                        .setAction(MusicPlayerService.ACTION_NEXT)
                        .setComponent(serviceName),
                0
        ));
        remoteViews.setOnClickPendingIntent(R.id.iv_prev, PendingIntent.getService(
                context,
                REQUEST_PREV,
                new Intent(context, MusicPlayerService.class)
                        .setAction(MusicPlayerService.ACTION_PREV)
                        .setComponent(serviceName),
                0
        ));
        remoteViews.setOnClickPendingIntent(R.id.iv_playpause, PendingIntent.getService(
                context,
                REQUEST_PLAYPAUSE,
                new Intent(context, MusicPlayerService.class)
                        .setAction(MusicPlayerService.ACTION_TOGGLE_PAUSE)
                        .setComponent(serviceName),
                0
        ));

        if (extras != null) {
            String t = extras.getString("track");
            String artist = extras.getString("artist");
            if (t != null) {
                remoteViews.setTextViewText(R.id.tv_title, t + "-" + artist);
            }
            remoteViews.setImageViewResource(R.id.iv_playpause,
                    extras.getBoolean("playing") ? R.drawable.ic_pause : R.drawable.ic_play);
            if (PlayManager.getPlayingMusic() != null)
                CoverLoader.loadImageViewByMusic(MusicApp.getAppContext(), PlayManager.getPlayingMusic(), artwork -> {
                    if (artwork != null) {
                        remoteViews.setImageViewBitmap(R.id.iv_cover, artwork);
                    } else {
                        remoteViews.setImageViewResource(R.id.iv_cover, R.drawable.default_cover);
                    }
                });
        } else {
            remoteViews.setTextViewText(R.id.tv_title, PlayManager.getSongName());
            remoteViews.setImageViewResource(R.id.iv_cover, R.drawable.default_cover);
        }
        remoteViews.setOnClickPendingIntent(R.id.iv_cover, PendingIntent.getActivity(
                context,
                0,
                NavigateUtil.getNowPlayingIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT
        ));

        remoteViews.setOnClickPendingIntent(R.id.iv_lyric, PendingIntent.getService(
                context,
                0,
                NavigateUtil.getLyricIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT
        ));
    }
}
