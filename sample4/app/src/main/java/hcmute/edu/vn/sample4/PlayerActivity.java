package hcmute.edu.vn.sample4;

import static hcmute.edu.vn.sample4.Service.ApplicationClass.CHANNEL_ID_2;
import static hcmute.edu.vn.sample4.Service.NotificationRecevier.ACTION_NEXT;
import static hcmute.edu.vn.sample4.Service.NotificationRecevier.ACTION_PLAY;
import static hcmute.edu.vn.sample4.Service.NotificationRecevier.ACTION_PREV;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import hcmute.edu.vn.sample4.Adapter.DiskAdapter;
import hcmute.edu.vn.sample4.Fragment.DiskFragment;
import hcmute.edu.vn.sample4.Interface.ActionPlaying;
import hcmute.edu.vn.sample4.Service.MusicService;
import hcmute.edu.vn.sample4.Service.NotificationRecevier;
import hcmute.edu.vn.sample4.model.song;

public class PlayerActivity extends AppCompatActivity implements ServiceConnection, ActionPlaying {

    ImageButton btnPlay,btnNext,btnPrevious, btnShuffleSong, btnLoopSong;
    TextView txtSongName, txtArtist, txtTimeStart, txtTimeRunning;
    SeekBar mseekBar;
    Context context;
    MusicService musicService;
    MediaSessionCompat mediaSessionCompat;

    private DiskFragment discFragment;
    ViewPager viewPagerDisc;
    private DiskAdapter diskAdapter;
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;

    Handler handler3;
    Runnable runnable;
    ArrayList<song> songlist;
    ImageView imageView;
    private SQLiteDatabase db;
    private androidx.appcompat.widget.Toolbar toolbarplaynhac;

    private int dem = 0, position = 0, duration = 0, timeValue = 0, durationToService = 0;
    private boolean repeat = false, checkrandom = false, isplaying;
    private song Song;
    private static ArrayList<song> mangbaihat = new ArrayList<>();

    // declare a boolean variable to store the button state
    boolean isLooping = false;

    boolean isShuffling = false;

    private String taikhoan;

    private CircleImageView circleImageView;
    public  static  final String EXTRA_NAME = "songname";
    static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mediaSessionCompat = new MediaSessionCompat(this, "MySessionTag");
        btnPrevious = (ImageButton) findViewById(R.id.imageButtonpreview);
        btnNext = (ImageButton) findViewById(R.id.imageButtonnext);
        btnPlay = (ImageButton) findViewById(R.id.imageButtonplaypause);
        btnLoopSong = (ImageButton) findViewById(R.id.imageButtonlap);
        btnShuffleSong = (ImageButton) findViewById(R.id.imageButtontron);

        txtSongName = (TextView) findViewById(R.id.textViewtenbaihatplaynhac);
        txtArtist = (TextView) findViewById(R.id.textViewtencasiplaynhac);
        txtTimeStart = findViewById(R.id.textViewtimetotal);
        txtTimeRunning = findViewById(R.id.textViewruntime);

        mseekBar = findViewById(R.id.seekBartime);
        circleImageView = findViewById(R.id.viewCirlerdianhac);


        if (mediaPlayer != null)
         {
             mediaPlayer.start();
             mediaPlayer.release();
         }
         Intent intent = getIntent();
         Bundle bundle = intent.getExtras();

         songlist = (ArrayList)bundle.getParcelableArrayList("songs");
         String sName = intent.getStringExtra("songname");
         position = bundle.getInt("pos",0);

        circleImageView.setAnimation(loadRotation());
        initPlayer(position);
        //mseekBar.setRotation(360);

        handler3 = new Handler();

        mseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mediaPlayer.seekTo(progress);
                    mseekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




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
                if (position < songlist.size() - 1) {
                    position++;
                } else {
                    position = 0;

                }
                initPlayer(position);

                if (mediaPlayer.isPlaying())
                    // mới chỉnh chỗ này 11:00 25/4/2023
                    showNotification(R.drawable.nutpause);
                else
                    showNotification(R.drawable.nutplay);
            }
        });

        // button for prevous Song
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position <= 0) {
                    position = songlist.size() - 1;
                } else {
                    position--;
                }

                initPlayer(position);

                if (mediaPlayer.isPlaying())
                    showNotification(R.drawable.nutpause);
                else
                    showNotification(R.drawable.nutplay);
            }
        });

        // loop song
//        btnLoopSong.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mediaPlayer.setLooping(true);
//                mediaPlayer.start();
//                btnLoopSong.setVisibility(v.GONE);
//                btnLoopSong.setImageResource(R.drawable.ic_baseline_skip_next_36);
//
//            }
//        });

        // declare a boolean variable to store the button state


// loop song
        btnLoopSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
                if (!isLooping) {
                    // if the button is not in loop state, set it to loop state
                    isLooping = true;
                    btnLoopSong.setImageResource(R.drawable.ic_baseline_repeat_one_24);
                } else {
                    // if the button is in loop state, set it to normal state
                    isLooping = false;
                    btnLoopSong.setImageResource(R.drawable.ic_baseline_repeat_24);
                }
                //btnLoopSong.setVisibility(View.GONE);
            }
        });




// shuffle song
        btnShuffleSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShuffling) {
                    // if the button is not in shuffle state, set it to shuffle state
                    isShuffling = true;
                    btnShuffleSong.setImageResource(R.drawable.ic_baseline_shuffle_24);
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                Random rand = new Random();
                                position = rand.nextInt((songlist.size() - 1) - 0 + 1) + 0;
                                initPlayer(position);
                            }
                        });
                    } else {
                        Random rand = new Random();
                        position = rand.nextInt((songlist.size() - 1) - 0 + 1) + 0;
                        initPlayer(position);
                    }
                } else {
                    // if the button is in shuffle state, set it to normal state
                    isShuffling = false;
                    btnShuffleSong.setImageResource(R.drawable.ic_baseline_shuffle_on_24);
                }
            }
        });

        // for progressbar
        mseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    mseekBar.setProgress(position);

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
        Uri songResourceUri = Uri.parse(songlist.get(position).getSongURL());
        try {
            mediaPlayer.setDataSource(PlayerActivity.this, songResourceUri);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        String songName = songlist.get(position).getSongName();

        Log.e("file from firebase: ", songResourceUri.toString());
        String songArtist = songlist.get(position).getSongArtist();
        String songTotalTime = songlist.get(position).getSongduration();
        txtSongName.setText(songName);
        txtArtist.setText(songArtist);
        txtTimeStart.setText(songTotalTime);


        // set seekbar maximum duration
        // create and load mediaplayer with song resources
        mediaPlayer = MediaPlayer.create(getApplicationContext(), songResourceUri);

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                String totalTime = createTimeLabel(mediaPlayer.getDuration());
                //songTotalTime.setText(totalTime);
                //mSeekBar.setMax(mediaPlayer.getDuration());
                //mediaPlayer.start();
                showNotification(R.drawable.nutpause); // sai xoa
                btnPlay.setBackgroundResource(R.drawable.nutplay);

                mseekBar.setMax(mp.getDuration());
                mediaPlayer.start();
                updateSeekBar();

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                int curSongPoition = position;
                // code to repeat songs until the
                if (curSongPoition < songlist.size() - 1) {
                    curSongPoition++;
                    initPlayer(curSongPoition);
                } else {
                    curSongPoition = 0;
                    initPlayer(curSongPoition);
                }

                showNotification(R.drawable.nutplay); // sai xoa
                btnPlay.setBackgroundResource(R.drawable.nutplay);


            }
        });

        //cập nhật trạng thái đang đệm của MediaPlayer
        // và cập nhật tiến trình của thanh seekbar.
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                // percent là tỷ lệ phần trăm của dữ liệu đã được tải.
                double  ratio = percent/100.0;
                //bufferingLevel được tính toán,
                int bufferingLevel = (int)(mp.getDuration()*ratio);
                //gọi trên đối tượng mseekBar để cập nhật tiến trình của thanh seekbar
                // hiển thị mức độ đệm của bài hát
                mseekBar.setSecondaryProgress(bufferingLevel);
            }
        });


        showCurrentArtwork();
        showNotification(R.drawable.nutpause);
        circleImageView.setAnimation(loadRotation());
    }

    //cập nhật tiến trình của thanh seekbar
    // ==> hiển thị thời gian đã phát của bài hát.
    public void updateSeekBar() {
        int currPosition = mediaPlayer.getCurrentPosition();
        mseekBar.setProgress(currPosition);


        //sử dụng để lặp lại việc cập nhật tiến trình
        // của seekbar theo thời gian thực.
        runnable = new Runnable() {
            //run() để gọi lại phương thức updateSeekBar() sau mỗi giây.
            @Override
            public void run() {
                updateSeekBar();
            }
        };
        handler3.postDelayed(runnable, 1000);
    }


    @SuppressLint("HandlerLeak")
    private Handler handle2r = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            Log.i("handler ", "handler called");
            //giá trị của thuộc tính what của đối tượng này được
            // sử dụng để cập nhật tiến trình của thanh seekbar.
            int current_position = msg.what;
            mseekBar.setProgress(current_position);
            String cTime = createTimeLabel(current_position);
            //tính toán thời gian đã phát của bài hát và đặt giá trị txtTimeRunning
            txtTimeRunning.setText(cTime);
        }
    };
    // end progress bar



   /* private Animation loadRotation(){
        //RotateAnimation rotateAnimation = new RotateAnimation(0,360)
    }*/
    private void showCurrentArtwork(){
        Uri uri = Uri.parse(songlist.get(position).getImageurl());
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




    public String createTimeLabel(int duration) {
        String timeLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timeLabel += min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    // show notification

    //tạo một kết nối giữa Activity hiện
    // tại và một dịch vụ âm nhạc (MusicService)


    /*
    *
    * Tham chiếu đến Intent, cho biết dịch vụ nào sẽ được khởi tạo.
    Tham chiếu đến đối tượng ServiceConnection, đại diện cho kết nối giữa Activity và dịch vụ.
    Ký hiệu BIND_AUTO_CREATE, để yêu cầu hệ thống tự động khởi tạo dịch vụ nếu nó chưa được khởi tạo.
    * */
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
    }


    //hủy bỏ kết nối giữa Activity và dịch vụ âm nhạc (MusicService)

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);    //được gọi để hủy bỏ kết nối giữa Activity và dịch vụ.
    }



    // for service - every time we call binder service on any of intent
    @Override
    public void onServiceConnected(ComponentName name, IBinder iBinder) {
        //name: tên của thành phần dịch vụ đã được kết nối.
        //iBinder: đối tượng IBinder đại diện cho dịch vụ được kết nối.


        MusicService.MyBinder binder = (MusicService.MyBinder) iBinder;
        musicService = binder.getService();
        musicService.setCallBack(PlayerActivity.this);
        Log.e("Connected", musicService + "");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
        Log.e("Disconnected", musicService + "");

    }

    // chuyen qua Player
    public void showNotification(int playPauseBTN) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, intent, 0);

        // previous button
        Intent prevIntent = new Intent(this, NotificationRecevier.class).setAction(ACTION_PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this,
                0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // previous button
        Intent playIntent = new Intent(this, NotificationRecevier.class).setAction(ACTION_PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this,
                0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // previous button
        Intent nextIntent = new Intent(this, NotificationRecevier.class).setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this,
                0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri uri = Uri.parse(songlist.get(position).getImageurl());

        Glide.with(getApplicationContext())

                .asBitmap()
                .load(uri)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID_2);

                        Notification notification = builder.setSmallIcon(R.drawable.ic_musical_note)   // icon mặc định của dành cho thông báo
                                .setLargeIcon(resource)     // hình ảnh nền cho thông báo
                                        .setContentTitle(songlist.get(position).getSongName())
                                .setContentText(songlist.get(position).getSongArtist())
                                .addAction(R.drawable.ic_baseline_skip_previous_36, "Previous", prevPendingIntent)
                                .addAction(R.drawable.nutpause, "Pause", playPendingIntent)
                                .addAction(R.drawable.ic_baseline_skip_next_36, "Next", nextPendingIntent)
                                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setOnlyAlertOnce(true)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setSound(null)
                                .build();
                        //musicService.startForeground(1, notification);
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(0, notification);
                    }
                });



    };













    private byte[] getAlbumArt(String uri)
            throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }


    // implement some functions in Interface ActionPlaying

    private void nextThreadbtn() {
        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextClicked();
                    }
                });
            }
        };
        nextThread.start();
    }




    // triển khai interface --> dungftrong
    // MusicService class thi vì phải viết lại từng dòng code




    public void nextClicked() {
        if (position < songlist.size() - 1) {
            position++;
        } else {
            position = 0;

        }
        initPlayer(position);
    }


    public void previousClicked() {
        if (position <= 0) {
            position =songlist.size() - 1;
        } else {
            position--;
        }

        initPlayer(position);
    }


    public void playClicked() {
        if (mediaPlayer.isPlaying()) {
            btnPlay.setBackgroundResource(R.drawable.nutpause);
            mediaPlayer.pause();
        } else {
            btnPlay.setBackgroundResource(R.drawable.nutplay);
            mediaPlayer.start();
        }
    }

}