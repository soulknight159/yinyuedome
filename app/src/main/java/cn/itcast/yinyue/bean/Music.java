package cn.itcast.yinyue.bean;

import java.io.Serializable;
import java.util.Objects;

public class Music implements Serializable {
    Integer id;
    String fileName;
    String title;
    int duration;
    String singer;
    String album;
    String type;
    String size;
    String fileUrl;

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Music{" +
                "album='" + album + '\'' +
                ", id=" + id +
                ", fileName='" + fileName + '\'' +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", singer='" + singer + '\'' +
                ", type='" + type + '\'' +
                ", size='" + size + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Music music = (Music) o;
        return duration == music.duration && Objects.equals(id, music.id) && Objects.equals(fileName, music.fileName) && Objects.equals(title, music.title) && Objects.equals(singer, music.singer) && Objects.equals(album, music.album) && Objects.equals(type, music.type) && Objects.equals(size, music.size) && Objects.equals(fileUrl, music.fileUrl);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(fileName);
        result = 31 * result + Objects.hashCode(title);
        result = 31 * result + duration;
        result = 31 * result + Objects.hashCode(singer);
        result = 31 * result + Objects.hashCode(album);
        result = 31 * result + Objects.hashCode(type);
        result = 31 * result + Objects.hashCode(size);
        result = 31 * result + Objects.hashCode(fileUrl);
        return result;
    }
}
