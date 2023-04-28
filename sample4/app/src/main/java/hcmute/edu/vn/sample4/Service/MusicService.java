package hcmute.edu.vn.sample4.Service;



import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import hcmute.edu.vn.sample4.Interface.ActionPlaying;
import hcmute.edu.vn.sample4.model.song;

public class MusicService extends Service {
    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_PREV = "PREVIOUS";


    public ArrayList<song> musicFiles = new ArrayList<>();

    public IBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    private Uri uri;
    public int position = -1;

    ActionPlaying actionPlaying;
    MediaSessionCompat mediaSessionCompat;

    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String ARTIST_NAME = "ARTIST_NAME";
    public static final String SONG_NAME = "SONG_NAME";



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "Method");
        return mBinder;
    }


    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Start Command", "Method");

        String actionName = intent.getStringExtra("myActionName");
        if (actionName !=null) {

            switch (actionName) {
                case ACTION_PLAY:
                    if (actionPlaying != null) {
                        actionPlaying.playClicked();
                    }

                    break;

                case ACTION_NEXT:
                    if(actionPlaying != null) {
                        actionPlaying.nextClicked();
                    }

                    break;

                case ACTION_PREV:
                    if(actionPlaying != null) {
                        actionPlaying.previousClicked();
                    }

                    break;


            }
        }

        //return super.onStartCommand(intent, flags, startId);

        // once the service will be stopped it will be restarted it itselft
        return START_STICKY;
    }

    public void setCallBack(ActionPlaying actionPlaying) {
        this.actionPlaying = actionPlaying;
    }





}
