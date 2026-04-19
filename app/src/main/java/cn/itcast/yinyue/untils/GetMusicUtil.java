package cn.itcast.yinyue.untils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.yinyue.bean.Music;

public class GetMusicUtil {
    public static List<Music> getMusic(Context context){
        List<Music> musicList = new  ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Music song = new Music();
                song.setFileName(cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                song.setTitle(cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                song.setDuration(cursor.getInt(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                song.setSinger(cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                song.setAlbum(cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));

                long sizeByte = cursor.getLong(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                );
                if (sizeByte > 0) {
                    float sizeMB = sizeByte / 1024f / 1024f;
                    song.setSize(String.format("%.2f M", sizeMB));
                } else {
                    song.setSize("未知");
                }
                String fileUrl = cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                );
                if (fileUrl != null && !fileUrl.isEmpty()) {
                    song.setFileUrl(fileUrl);
                }
                musicList.add(song);
            } while (cursor.moveToNext());

            cursor.close();
        }
        return musicList;
    }
}
