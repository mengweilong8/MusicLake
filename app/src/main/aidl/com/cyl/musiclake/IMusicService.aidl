// IMusicService.aidl
package com.cyl.musiclake;

// Declare any non-default types here with import statements
import com.cyl.musiclake.bean.Music;

interface IMusicService {
    void playOnline(in Music music);
    void play(int id);
    void playPause();
    void pause();
    void stop();
    void prev();
    void next();
    void refresh();
    void update(in Music music);
    void setLoopMode(int loopmode);
    void seekTo(int ms);
    int position();
    int getDuration();
    int getCurrentPosition();
    boolean isPlaying();
    boolean isPause();
    String getSongName();
    String getSongArtist();
    Music getPlayingMusic();
    void setPlayList(in List<Music> playlist);
    List<Music> getPlayList();
    void removeFromQueue(int position);
    void clearQueue();
    void showDesktopLyric(boolean show);
}
