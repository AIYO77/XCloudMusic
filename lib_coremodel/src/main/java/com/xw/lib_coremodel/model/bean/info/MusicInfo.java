package com.xw.lib_coremodel.model.bean.info;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;


public class MusicInfo implements Parcelable {

    private static final String KEY_SONG_ID = "songId";
    private static final String KEY_ALBUM_ID = "albumId";
    private static final String KEY_ALBUM_NAME = "albumName";
    private static final String KEY_ALBUM_DATA = "albumPic";
    private static final String KEY_MUSIC_NAME = "musicName";
    private static final String KEY_ARTIST = "artist";
    private static final String KEY_ARTIST_ID = "artistId";
    private static final String KEY_ISLOCAL = "islocal";

    public long songId = -1;
    public long albumId = -1;
    public String albumName;
    public String albumPic;
    public String musicName;
    public String artist;
    public long artistId;
    public boolean islocal;

    public MusicInfo() {
    }

    public static final Creator<MusicInfo> CREATOR = new Creator<MusicInfo>() {

        @Override
        public MusicInfo createFromParcel(Parcel source) {
            MusicInfo music = new MusicInfo();
            Bundle bundle = source.readBundle(getClass().getClassLoader());
            if (bundle != null) {
                music.songId = bundle.getLong(KEY_SONG_ID);
                music.albumId = bundle.getInt(KEY_ALBUM_ID);
                music.albumName = bundle.getString(KEY_ALBUM_NAME);
                music.albumPic = bundle.getString(KEY_ALBUM_DATA);
                music.musicName = bundle.getString(KEY_MUSIC_NAME);
                music.artist = bundle.getString(KEY_ARTIST);
                music.artistId = bundle.getLong(KEY_ARTIST_ID);
                music.islocal = bundle.getBoolean(KEY_ISLOCAL);
            }
            return music;
        }

        @Override
        public MusicInfo[] newArray(int size) {
            return new MusicInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_SONG_ID, songId);
        bundle.getLong(KEY_ALBUM_ID, albumId);
        bundle.putString(KEY_ALBUM_NAME, albumName);
        bundle.putString(KEY_ALBUM_DATA, albumPic);
        bundle.putString(KEY_MUSIC_NAME, musicName);
        bundle.putString(KEY_ARTIST, artist);
        bundle.putLong(KEY_ARTIST_ID, artistId);
        bundle.putBoolean(KEY_ISLOCAL, islocal);
        dest.writeBundle(bundle);
    }
}