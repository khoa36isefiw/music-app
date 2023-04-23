package hcmute.edu.vn.sample1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import com.bumptech.glide.Glide;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.edu.vn.sample1.Adapter.DiskAdapter;
import hcmute.edu.vn.sample1.Fragment.DiskFragment;
import hcmute.edu.vn.sample1.model.song;

public class PlayerActivity extends AppCompatActivity {

    ImageButton btnPlay,btnNext,btnPrevious;
    TextView txtSongName,txtSongStart,txtSongEnd,txtArtist;
    SeekBar seekBar;
    private DiskFragment discFragment;
    ViewPager viewPagerDisc;
    private DiskAdapter diskAdapter;
    private Handler handler = new Handler();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        btnPrevious = (ImageButton)findViewById(R.id.imageButtonpreview);
        btnNext =(ImageButton)findViewById(R.id.imageButtonnext);
        btnPlay = (ImageButton)findViewById(R.id.imageButtonplaypause);

        txtSongName =(TextView) findViewById(R.id.textViewtenbaihatplaynhac);
        txtArtist = (TextView) findViewById(R.id.textViewtencasiplaynhac);
        seekBar = findViewById(R.id.seekBartime);
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
         txtSongName.setSelected(true);
         Uri uri = Uri.parse(songList.get(position).getSongURL());

         String songName = songList.get(position).getSongName();
        Log.e("file from firebase: ", uri.toString());
            String songArtist = songList.get(position).getSongArtist();
         txtSongName.setText(songName);
         txtArtist.setText(songArtist);
         btnPlay.setBackgroundResource(R.drawable.nutplay);


        showCurrentArtwork();

        circleImageView.setAnimation(loadRotation());

        // Play song
         mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
         mediaPlayer.start();



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
    }
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
}