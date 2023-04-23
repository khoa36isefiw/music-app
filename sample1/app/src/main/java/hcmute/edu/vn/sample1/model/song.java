package hcmute.edu.vn.sample1.model;

import java.io.Serializable;

public class song implements Serializable {
    private String imageurl;
    private String songartist;
    private String songname;
    private String songurl;
    private String songduration;

    public song() {
    }



    public song(String imageurl, String songName, String songArtist, String songURL) {
        this.imageurl = imageurl;
        this.songname = songName;
        this.songartist = songArtist;
        this.songurl = songURL;
    }


    // get time of songs
    public song(String imageurl, String songName, String songArtist, String songURL, String songDuration) {
        this.imageurl = imageurl;
        this.songname = songName;
        this.songartist = songArtist;
        this.songurl = songURL;
        this.songduration = songDuration;
    }


    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getSongName() {
        return songname;
    }

    public void setSongName(String songName) {
        this.songname = songName;
    }

    public String getSongArtist() {
        return songartist;
    }

    public void setSongArtist(String songArtist) {
        this.songartist = songArtist;
    }

    public String getSongURL() {
        return songurl;
    }

    public void setSongURL(String songURL) {
        this.songurl = songURL;
    }

    public String getSongduration() {
        return songduration;
    }

    public void setSongduration(String songduration) {
        this.songduration = songduration;
    }
}
