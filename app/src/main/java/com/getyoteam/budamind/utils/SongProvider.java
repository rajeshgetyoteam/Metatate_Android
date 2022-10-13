package com.getyoteam.budamind.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//import androidx.annotation.Nullable;
import com.getyoteam.budamind.Model.MomentListModel;

import java.util.ArrayList;
import java.util.List;

public class SongProvider {
    private static final int TITLE = 0;
    private static final int TRACK = 1;
    private static final int YEAR = 2;
    private static final int DURATION = 3;
    private static final int PATH = 4;
    private static final int ALBUM = 5;
    private static final int ARTIST_ID = 6;
    private static final int ARTIST = 7;

    private static final String[] BASE_PROJECTION = new String[]{
            MediaStore.Audio.AudioColumns.TITLE,// 0
            MediaStore.Audio.AudioColumns.TRACK,// 1
            MediaStore.Audio.AudioColumns.YEAR,// 2
            MediaStore.Audio.AudioColumns.DURATION,// 3
            MediaStore.Audio.AudioColumns.DATA,// 4
            MediaStore.Audio.AudioColumns.ALBUM,// 5
            MediaStore.Audio.AudioColumns.ARTIST_ID,// 6
            MediaStore.Audio.AudioColumns.ARTIST,// 7
    };

    private static List<MomentListModel> mAllDeviceSongs = new ArrayList<>();

    public static List<MomentListModel> getAllDeviceSongs(Context context) {
        Cursor cursor= makeSongCursor(context);
        return getSongs(cursor);
    }



    @NonNull
    static List<MomentListModel> getSongs(@Nullable final Cursor cursor) {
        final List<MomentListModel> songs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                final MomentListModel song = getSongFromCursorImpl(cursor);
//                if (song.duration >= 30000) {
//                    songs.add(song);
//                    mAllDeviceSongs.add(song);
//                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return songs;
    }



    @NonNull
    private static MomentListModel getSongFromCursorImpl(@NonNull Cursor cursor) {
        final String title = cursor.getString(TITLE);
        final int trackNumber = cursor.getInt(TRACK);
        final int year = cursor.getInt(YEAR);
        final int duration = cursor.getInt(DURATION);
        final String uri = cursor.getString(PATH);
        final String albumName = cursor.getString(ALBUM);
        final int artistId = cursor.getInt(ARTIST_ID);
        final String artistName = cursor.getString(ARTIST);

        return new MomentListModel();

//        return new MomentListModel(title, trackNumber, year, duration, uri, albumName, artistId, artistName);
    }

    @Nullable
    static Cursor makeSongCursor(@NonNull final Context context) {
        try {
            return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    BASE_PROJECTION, null, null, null);
        } catch (SecurityException e) {
            return null;
        }
    }
}
