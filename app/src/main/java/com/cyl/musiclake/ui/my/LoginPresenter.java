package com.cyl.musiclake.ui.my;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.cyl.musiclake.common.Constants;
import com.cyl.musiclake.musicApi.MusicApiServiceImpl;
import com.cyl.musiclake.ui.my.user.User;
import com.cyl.musiclake.utils.LogUtil;
import com.cyl.musiclake.utils.ToastUtils;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.tencent.connect.common.Constants.REQUEST_LOGIN;

/**
 * Created by D22434 on 2018/1/3.
 */

public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View mView;
    private Context mContext;
    private UserModel userModel;

    //QQ第三方登录
    private Tencent mTencent;
    private IUiListener loginListener;

    @Override
    public void attachView(LoginContract.View view) {
        mView = view;
        mContext = (Context) view;
        userModel = new UserModel(mContext);
    }


    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void login(Map<String, String> params) {
        mView.showLoading();

    }

    private void getPrivateToken() {
        MusicApiServiceImpl.login(mTencent.getAccessToken(), mTencent.getOpenId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(User user) {
                        if (user != null) {
                            User newUserInfo = userModel.getUserInfo();
                            newUserInfo.setToken(user.getToken());
                            userModel.savaInfo(newUserInfo);
                        }
                        //登录成功
                        mView.hideLoading();
                        //登录成功
                        mView.success(userModel.getUserInfo());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        //登录失败
                        mView.hideLoading();

                        mView.showErrorInfo(throwable.getMessage());
                        //登录成功
                    }

                    @Override
                    public void onComplete() {
                        mView.hideLoading();
                    }
                });
    }


    /**
     * 实现QQ第三方登录
     */
    @Override
    public void loginByQQ(Activity activity) {
        mView.showLoading();
        //QQ第三方登录
        mTencent = Tencent.createInstance(Constants.APP_ID, mContext);
        mTencent.login(activity, "all", loginListener);
        loginListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                mView.hideLoading();
                //登录成功后回调该方法,可以跳转相关的页面
                Toast.makeText(activity, "登录成功", Toast.LENGTH_SHORT).show();
                JSONObject object = (JSONObject) o;
                try {
                    LogUtil.e("mTencent:" + o.toString());
                    String accessToken = object.getString("access_token");
                    String expires = object.getString("expires_in");
                    String openID = object.getString("openid");
                    LogUtil.e("QQ" + accessToken + "--" + expires + "--" + openID);
                    mTencent.setAccessToken(accessToken, expires);
                    mTencent.setOpenId(openID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                mView.hideLoading();
            }

            @Override
            public void onCancel() {
                mView.hideLoading();
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == -1) {
                Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
                Tencent.handleResultData(data, loginListener);
                UserInfo info = new UserInfo(mContext, mTencent.getQQToken());
                info.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object o) {
                        try {
                            JSONObject info = (JSONObject) o;
                            String nickName = info.getString("nickname");//获取用户昵称
                            String iconUrl = info.getString("figureurl_qq_2");//获取用户头像的url
                            String gender = info.getString("gender");//获取用户性别
//                            Map<String, String> params = new HashMap<String, String>();
//                            params.put(Constants.PARAM_METHOD, "qq");
//                            params.put(Constants.USERNAME, nickName);
//                            params.put(Constants.USER_SEX, gender);
//                            params.put(Constants.USER_IMG, iconUrl);
//                            params.put(Constants.USER_ID, mTencent.getOpenId());
                            User userInfo = new User();
                            userInfo.setId(mTencent.getOpenId());
                            userInfo.setAvatar(iconUrl);
                            userInfo.setSex(gender);
                            userInfo.setName(nickName);
                            userInfo.setNick(nickName);
                            //保存用户信息
                            userModel.savaInfo(userInfo);
                            getPrivateToken();
                        } catch (JSONException e) {
                            ToastUtils.show(mContext, "网络异常，请稍后重试！");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(UiError uiError) {
                        mView.hideLoading();
                    }

                    @Override
                    public void onCancel() {
                        mView.hideLoading();
                    }
                });
            }
        }
    }
}
