package com.cyl.musiclake.ui.music.local.contract;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;

import com.cyl.musiclake.base.BasePresenter;
import com.cyl.musiclake.base.BaseView;


public interface PlayControlsContract {

    interface View extends BaseView {

        void setAlbumArt(Bitmap albumArt);

        void setAlbumArt(Drawable albumArt);

        void setTitle(String title);

        void setArtist(String artist);

        void setOtherInfo(String source);

        void setPalette(Palette palette);

        void showLyric(String lyric, boolean isFilePath);


        void setPlayPauseButton(boolean isPlaying);

        boolean getPlayPauseStatus();

        void updateProgress(int progress);

        void updateFavorite(boolean love);

        void setProgressMax(int max);

        void setErrorInfo(String message);

        void updatePanelLayout(boolean scroll);
    }

    interface Presenter extends BasePresenter<View> {

        void onPlayPauseClick();

        void onPreviousClick();

        void loadLyric();

        void onNextClick();

        void updateNowPlayingCard();

        void updatePlayStatus();

    }
}
