package hcmute.edu.vn.sample1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.edu.vn.sample1.Adapter.DiskAdapter;
import hcmute.edu.vn.sample1.Fragment.DiskFragment;
import hcmute.edu.vn.sample1.model.song;

public class PlayerActivity extends AppCompatActivity {

    int currIndex = 0;
    ImageButton btnPlay,btnNext,btnPrevious, btnLoopSong, btnShuffleSong;
    TextView txtSongName,txtSongStart,txtSongEnd,txtArtist, txtTimeStart, txtTimeRunning;
    SeekBar mSeekBar;
    private DiskFragment discFragment;
    ViewPager viewPagerDisc;
    private DiskAdapter diskAdapter;
    ArrayList<song> songList;
    List<song> mSongList;

    ImageView imageView;
    private SQLiteDatabase db;
    private androidx.appcompat.widget.Toolbar toolbarplaynhac;

    private int dem = 0, position = 0, duration = 0, timeValue = 0, durationToService = 0;
    private boolean repeat = false, checkrandom = false, isplaying;
    private song Song;
    private static ArrayList<song> mangbaihat = new ArrayList<>();

    private String taikhoan;

    private CircleImageView circleImageView;
    public  static  final String EXTRA_NAME = "songname";
    static MediaPlayer mediaPlayer;
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        btnPrevious = (ImageButton)findViewById(R.id.imageButtonpreview);
        btnNext =(ImageButton)findViewById(R.id.imageButtonnext);
        btnPlay = (ImageButton)findViewById(R.id.imageButtonplaypause);
        btnLoopSong = (ImageButton)findViewById(R.id.imageButtonlap);
        btnShuffleSong = (ImageButton)findViewById(R.id.imageButtontron);

        txtSongName =(TextView) findViewById(R.id.textViewtenbaihatplaynhac);
        txtArtist = (TextView) findViewById(R.id.textViewtencasiplaynhac);
        txtTimeStart = findViewById(R.id.textViewtimetotal);
        txtTimeRunning = findViewById(R.id.textViewruntime);

        mSeekBar = findViewById(R.id.seekBartime);
        circleImageView = findViewById(R.id.viewCirlerdianhac);




         if (mediaPlayer != null)
         {


             mediaPlayer.start();
             mediaPlayer.release();
         }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

         songList = (ArrayList)bundle.getParcelableArrayList("songs");
         String sName = intent.getStringExtra("songname");
         position = bundle.getInt("pos",0);

//         txtSongName.setSelected(true);
//         Uri uri = Uri.parse(songList.get(position).getSongURL());
//
//         String songName = songList.get(position).getSongName();
//        Log.e("file from firebase: ", uri.toString());
//            String songArtist = songList.get(position).getSongArtist();
//            String songTotalTime = songList.get(position).getSongduration();
//         txtSongName.setText(songName);
//         txtArtist.setText(songArtist);
//         txtTimeStart.setText(songTotalTime);
//        // set seekbar maximum duration
//
//
//
//         btnPlay.setBackgroundResource(R.drawable.nutplay);
//
//
//            showCurrentArtwork();
//

        circleImageView.setAnimation(loadRotation());
//
//        // Play song
//         mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
//         mediaPlayer.start();

        initPlayer(position);
        mSeekBar.setRotation(180);








         // icon Play Song
         btnPlay.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(mediaPlayer.isPlaying())
                 {
                     btnPlay.setBackgroundResource(R.drawable.nutpause);
                     mediaPlayer.pause();
                 }
                 else
                 {
                     btnPlay.setBackgroundResource(R.drawable.nutplay);
                     mediaPlayer.start();
                 }
             }
         });

         // button for next Song
         btnNext.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (position < songList.size() - 1) {
                     position++;
                 } else {
                     position = 0;

                 }
                 initPlayer(position);
             }
         });

        // button for prevous Song
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position <= 0) {
                    position = songList.size() - 1;
                } else {
                    position--;
                }

                initPlayer(position);
            }
        });

        // loop song
        btnLoopSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        });

        //random songs
        btnShuffleSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Random rand = new Random();
                            position = rand.nextInt((songList.size() - 1) - 0 + 1) + 0;
                            initPlayer(position);
                        }
                    });
                } else {
                    Random rand = new Random();
                    position = rand.nextInt((songList.size() - 1) - 0 + 1) + 0;
                    initPlayer(position);
                }
            }
        });


         // for progressbar
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mediaPlayer.seekTo(progress);
                    mSeekBar.setProgress(position);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        // Khởi tạo và bắt đầu Thread để cập nhật vị trí của SeekBar
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null) {
                    try {
                        if (mediaPlayer.isPlaying()) {
                            Message msg = new Message();
                            msg.what = mediaPlayer.getCurrentPosition();
                            handle2r.sendMessage(msg);
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }



    // function play Song, create to tái sử dụng để set event cho Next, Previous
    private void initPlayer(final int position) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
        }

        txtSongName.setSelected(true);
        //Uri songResourceUri = Uri.parse(songList.get(position).getSongURL());
        Uri songResourceUri = Uri.parse(songList.get(position).getSongURL());

        String songName = songList.get(position).getSongName();

        Log.e("file from firebase: ", songResourceUri.toString());
        String songArtist = songList.get(position).getSongArtist();
        String songTotalTime = songList.get(position).getSongduration();
        txtSongName.setText(songName);
        txtArtist.setText(songArtist);
        txtTimeStart.setText(songTotalTime);
        // set seekbar maximum duration

        mediaPlayer = MediaPlayer.create(getApplicationContext(), songResourceUri); // create and load mediaplayer with song resources
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                String totalTime = createTimeLabel(mediaPlayer.getDuration());
                //songTotalTime.setText(totalTime);
                //mSeekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                btnPlay.setBackgroundResource(R.drawable.nutplay);

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                int curSongPoition = position;
                // code to repeat songs until the
                if (curSongPoition < songList.size() - 1) {
                    curSongPoition++;
                    initPlayer(curSongPoition);
                } else {
                    curSongPoition = 0;
                    initPlayer(curSongPoition);
                }

                btnPlay.setBackgroundResource(R.drawable.nutpause);

            }
        });


        showCurrentArtwork();
        circleImageView.setAnimation(loadRotation());
    }


    // random songs
    private void playRandomSong() {
//        // Get a random song from the songList
//        int randomIndex = new Random().nextInt(songList.size());
//        int songResId = songList.get(randomIndex);
//
//        // Stop and release any previous MediaPlayer instance
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//        }
//
//        // Create a new MediaPlayer instance with the random song
//        mediaPlayer = MediaPlayer.create(this, songResId);
//
//        // Start playing the song
//        mediaPlayer.start();

        Random rand = new Random();
        position = rand.nextInt((songList.size() - 1) - 0 + 1) + 0;
        initPlayer(position);
    }

//
    @SuppressLint("HandlerLeak")
    private Handler handle2r = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            Log.i("handler ", "handler called");
            int current_position = msg.what;
            mSeekBar.setProgress(current_position);
            String cTime = createTimeLabel(current_position);
            txtTimeRunning.setText(cTime);
        }
    };
    // end progress bar


   /* private Animation loadRotation(){
        //RotateAnimation rotateAnimation = new RotateAnimation(0,360)
    }*/
    private void showCurrentArtwork(){
        Uri uri = Uri.parse(songList.get(position).getImageurl());
        Glide.with(getApplicationContext())
                .load(uri)
                .into(circleImageView);

        if(circleImageView == null)
        {
            circleImageView.setImageResource(R.drawable.img);
        }
    }
    private  Animation loadRotation(){
        RotateAnimation rotateAnimation = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(10000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        return rotateAnimation;
    }

    // new function to calculate init time before starting to ending of the song

    public String createTimeLabel(int duration) {
        String timeLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timeLabel += min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }


}