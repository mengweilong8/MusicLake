package com.cyl.musiclake.service;

import android.os.RemoteException;

import com.cyl.musiclake.IMusicService;
import com.cyl.musiclake.bean.Music;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Author   : D22434
 * version  : 2018/2/7
 * function :
 */

public class IMusicServiceStub extends IMusicService.Stub {
    private final WeakReference<MusicPlayerService> mService;

    public IMusicServiceStub(final MusicPlayerService service) {
        mService = new WeakReference<>(service);
    }

    @Override
    public void playOnline(Music music) throws RemoteException {
        mService.get().play(music);
    }

    @Override
    public void play(int id) throws RemoteException {
        mService.get().playMusic(id);
    }

    @Override
    public void playPause() throws RemoteException {
        mService.get().playPause();
    }

    @Override
    public void pause() throws RemoteException {
        mService.get().pause();
    }

    @Override
    public void stop() throws RemoteException {
        mService.get().stop(true);
    }

    @Override
    public void prev() throws RemoteException {
        mService.get().prev();
    }

    @Override
    public void next() throws RemoteException {
        mService.get().next();
    }

    @Override
    public void refresh() throws RemoteException {
        mService.get().refresh();
    }

    @Override
    public void update(Music music) throws RemoteException {
        mService.get().updateFavorite(music);
    }

    @Override
    public void setLoopMode(int loopmode) throws RemoteException {
    }

    @Override
    public void seekTo(int ms) throws RemoteException {
        mService.get().seekTo(ms);
    }

    @Override
    public String getSongName() throws RemoteException {
        return mService.get().getTitle();
    }

    @Override
    public String getSongArtist() throws RemoteException {
        return mService.get().getArtistName();
    }

    @Override
    public Music getPlayingMusic() throws RemoteException {
        return mService.get().getPlayingMusic();
    }

    @Override
    public void setPlayList(List<Music> playlist) throws RemoteException {
        mService.get().setPlayQueue(playlist);
    }

    @Override
    public List<Music> getPlayList() throws RemoteException {
        return mService.get().getPlayQueue();
    }

    @Override
    public void removeFromQueue(int position) throws RemoteException {
        mService.get().removeFromQueue(position);
    }

    @Override
    public void clearQueue() throws RemoteException {
        mService.get().clearQueue();
    }

    @Override
    public void showDesktopLyric(boolean show) throws RemoteException {
        mService.get().showDesktopLyric(show);
    }

    @Override
    public int position() throws RemoteException {
        return mService.get().getPlayPosition();
    }

    @Override
    public int getDuration() throws RemoteException {
        return (int) mService.get().getDuration();
    }

    @Override
    public int getCurrentPosition() throws RemoteException {
        return (int) mService.get().getCurrentPosition();
    }


    @Override
    public boolean isPlaying() throws RemoteException {
        return mService.get().isPlaying();
    }

    @Override
    public boolean isPause() throws RemoteException {
        return !mService.get().isPlaying();
    }
}