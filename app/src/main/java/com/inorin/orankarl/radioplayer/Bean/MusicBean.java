package com.inorin.orankarl.radioplayer.Bean;

import java.io.Serializable;

public class MusicBean implements Serializable {
    private String songName, albumPicBig, albumPicSmall, downUrl, singerName;
    private int seconds, songid;

    public MusicBean(){}

    public MusicBean(String songName, int seconds, int songid, String albumPicBig, String albumPicSmall, String downUrl, String singerName) {
        this.songName = songName;
        this.seconds = seconds;
        this.songid = songid;
        this.albumPicBig = albumPicBig;
        this.albumPicSmall = albumPicSmall;
        this.downUrl = downUrl;
        this.singerName = singerName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getAlbumPicBig() {
        return albumPicBig;
    }

    public void setAlbumPicBig(String albumPicBig) {
        this.albumPicBig = albumPicBig;
    }

    public String getAlbumPicSmall() {
        return albumPicSmall;
    }

    public void setAlbumPicSmall(String albumPicSmall) {
        this.albumPicSmall = albumPicSmall;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getSongid() {
        return songid;
    }

    public void setSongid(int songid) {
        this.songid = songid;
    }
}
