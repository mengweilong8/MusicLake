package com.cyl.musiclake.api.baidu;

import com.cyl.musiclake.bean.Music;
import com.cyl.musiclake.common.Constants;
import com.cyl.musiclake.net.ApiManager;
import com.cyl.musiclake.utils.FileUtils;
import com.cyl.musiclake.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by yonglong on 2018/1/21.
 */

public class BaiduApiServiceImpl {
    private static final String TAG = "BaiduApiServiceImpl";
    private static final String Base_Url = "http://musicapi.qianqian.com/";

    public static BaiduApiService getApiService() {
        return ApiManager.getInstance().create(BaiduApiService.class, Base_Url);
    }

//    http://musicapi.qianqian.com/v1/restserver/ting?from=android&version=6.0.7.1&channel=huwei&operator=1&method=baidu.ting.billboard.billCategory&format=json&kflag=2

    public static Observable<BaiduMusicList> getOnlinePlaylist() {
        Map<String, String> params = new HashMap<>();
        params.put(Constants.PARAM_METHOD, Constants.METHOD_CATEGORY);// key
        params.put("operator", "1");
        params.put("kflag", "2");
        params.put("format", "json");
        return getApiService().getOnlinePlaylist(params)
                .flatMap(Observable::fromArray);
    }

    public static Observable<List<Music>> getOnlineSongs(String type, int limit, int mOffset) {
        Map<String, String> params = new HashMap<>();

        params.put(Constants.PARAM_METHOD, Constants.METHOD_GET_MUSIC_LIST);
        params.put(Constants.PARAM_TYPE, type);
        params.put(Constants.PARAM_SIZE, String.valueOf(limit));
        params.put(Constants.PARAM_OFFSET, String.valueOf(mOffset));

        return getApiService().getOnlineSongs(params)
                .flatMap(baiduSongList -> {
                    List<Music> musicList = new ArrayList<>();
                    for (BaiduMusicInfo songInfo : baiduSongList.getSong_list()) {
                        Music music = new Music();
                        music.setType(Music.Type.BAIDU);
                        music.setOnline(true);
                        music.setId(songInfo.getSong_id());
                        music.setAlbum(songInfo.getAlbum_title());
                        music.setAlbumId(String.valueOf(songInfo.getAlbum_id()));
                        music.setArtist(songInfo.getArtist_name());
                        music.setArtistId(songInfo.getTing_uid());
                        music.setTitle(songInfo.getTitle());
                        music.setLrcPath(songInfo.getLrclink());
                        music.setCoverSmall(songInfo.getPic_small());
                        music.setCoverUri(songInfo.getPic_big());
                        music.setCoverBig(songInfo.getPic_radio());
                        musicList.add(music);
                    }
                    return Observable.create((ObservableOnSubscribe<List<Music>>) e -> {
                        try {
                            e.onNext(musicList);
                            e.onComplete();
                        } catch (Exception ep) {
                            e.onError(ep);
                        }
                    });
                });
    }

    //{"errorCode":22232,"data":{"xcode":"","songList":""}}
    public static Observable<Music> getTingSongInfo(String mid) {
        Map<String, String> params = new HashMap<>();
        String Url = "http://music.baidu.com/data/music/links?songIds=" + mid;
        return getApiService().getTingSongInfo(Url, params)
                .flatMap(baiduSongInfo -> {
                    Music music = new Music();
                    BaiduSongInfo.DataBean.SongListBean songInfo = baiduSongInfo.getData().getSongList().get(0);
                    music.setType(Music.Type.BAIDU);
                    music.setOnline(true);
                    music.setId(songInfo.getSongId());
                    music.setAlbum(songInfo.getAlbumName());
                    music.setAlbumId(String.valueOf(songInfo.getAlbumId()));
                    music.setArtistId(songInfo.getArtistId());
                    music.setArtist(songInfo.getArtistName());
                    music.setTitle(songInfo.getSongName());
                    music.setUri(songInfo.getSongLink());
                    music.setFileSize(songInfo.getSize());
                    music.setLrcPath(songInfo.getLrcLink());
                    music.setCoverSmall(songInfo.getSongPicSmall());
                    music.setCoverUri(songInfo.getSongPicBig());
                    music.setCoverBig(songInfo.getSongPicRadio());

                    return Observable.create((ObservableOnSubscribe<Music>) e -> {
                        if (music.getUri() != null) {
                            e.onNext(music);
                            e.onComplete();
                        } else {
                            e.onError(new Throwable());
                        }
                    });
                });
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public static Observable<String> getBaiduLyric(Music music) {
        //本地歌词路径
        String mLyricPath = FileUtils.getLrcDir() + music.getTitle() + "-" + music.getArtist() + ".lrc";
        //网络歌词
        String mLyricUrl = music.getLrcPath();
        if (FileUtils.exists(mLyricPath)) {
            return Observable.create(emitter -> {
                try {
                    String lyric = FileUtils.readFile(mLyricPath);
                    emitter.onNext(lyric);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            });
        }
        return getApiService().getBaiduLyric(mLyricUrl)
                .flatMap(baiDuLyricInfo -> {
                    String lyric = baiDuLyricInfo.string();
                    //保存文件
                    boolean save = FileUtils.writeText(mLyricPath, lyric);
                    LogUtil.e("保存网络歌词：" + save);
                    return Observable.fromArray(lyric);
                });
    }

}
