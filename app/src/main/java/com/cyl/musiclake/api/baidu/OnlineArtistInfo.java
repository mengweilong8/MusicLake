package com.cyl.musiclake.api.baidu;

/**
 * Created by yonglong on 2016/11/30.
 */
public class OnlineArtistInfo {
    // 星座
    private String constellation;
    // 体重
    private float weight;
    // 身高
    private float stature;
    // 国籍
    private String country;
    // 歌手链接
    private String url;
    // 歌手简介
    private String intro;
    // 头像
    private String avatar_big;
    // 姓名
    private String name;
    // 生日
    private String birth;

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getStature() {
        return stature;
    }

    public void setStature(float stature) {
        this.stature = stature;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getAvatar_big() {
        return avatar_big;
    }

    public void setAvatar_big(String avatar_big) {
        this.avatar_big = avatar_big;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    @Override
    public String toString() {
        return "OnlineArtistInfo{" +
                "constellation='" + constellation + '\'' +
                ", weight=" + weight +
                ", stature=" + stature +
                ", country='" + country + '\'' +
                ", url='" + url + '\'' +
                ", intro='" + intro + '\'' +
                ", avatar_big='" + avatar_big + '\'' +
                ", name='" + name + '\'' +
                ", birth='" + birth + '\'' +
                '}';
    }
}
