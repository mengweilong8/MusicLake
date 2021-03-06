package com.cyl.musiclake;

import android.app.Application;
import android.content.Context;

import com.cyl.musiclake.common.Constants;
import com.cyl.musiclake.service.PlayManager;
import com.cyl.musiclake.utils.SPUtils;
import com.cyl.musiclake.utils.UpdateUtils;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.liulishuo.filedownloader.util.FileDownloadLog;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.tauth.Tencent;

import org.litepal.LitePal;

import java.net.Proxy;

public class MusicApp extends Application {
    private static MusicApp sInstance;
    private PlayManager.ServiceToken mToken;
    public static Context mContext;
    private Tencent mTencent;

    public static synchronized MusicApp getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mContext = this;
        LitePal.initialize(this);
        UpdateUtils.init(this);
        SPUtils.init(this);
        FileDownloadLog.NEED_LOG = true;
        mTencent = Tencent.createInstance(Constants.APP_ID, this);
        initBugly();
        /**
         * 初始化文件下载
         */
        FileDownloader.setupOnApplicationOnCreate(this)
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15_000) // set connection timeout.
                        .readTimeout(15_000) // set read timeout.
                        .proxy(Proxy.NO_PROXY) // set proxy
                ))
                .commit();
    }

    private void initBugly() {
        Bugly.init(getApplicationContext(), Constants.BUG_APP_ID, true);
        Beta.checkUpgrade(false, false);
    }

    public Tencent getTencent() {
        return mTencent;
    }
}
