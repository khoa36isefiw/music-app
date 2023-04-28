package hcmute.edu.vn.sample4;
import static hcmute.edu.vn.sample4.Service.ApplicationClass.ACTION_NEXT;
import static hcmute.edu.vn.sample4.Service.ApplicationClass.ACTION_PLAY;
import static hcmute.edu.vn.sample4.Service.ApplicationClass.ACTION_PREV;
import static hcmute.edu.vn.sample4.Service.ApplicationClass.CHANNEL_ID_2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.Manifest;       /*
            FOREGROUND_SERVICE
            READ_EXTERNAL_STORAGE
            POST_NOTIFICATIONS
            WRITE_EXTERNAL_STORAGE
*/
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hcmute.edu.vn.sample4.Adapter.RecyclerAdapter;
import hcmute.edu.vn.sample4.Adapter.ViewPaperAdapter;
import hcmute.edu.vn.sample4.Service.MusicService;
import hcmute.edu.vn.sample4.Service.NotificationRecevier;
import hcmute.edu.vn.sample4.model.MyServiceConnection;
import hcmute.edu.vn.sample4.model.UploadSongActivity;
import hcmute.edu.vn.sample4.model.song;
import kotlin.contracts.ReturnsNotNull;

public class MainActivity extends AppCompatActivity {

    private boolean checkPermission = false;
    Uri uri;

    private ViewPager mViewPaper;
    public static ArrayList<song> songList;
    RecyclerView rvcSongs;

    public static DatabaseReference mDataBase;
    RecyclerAdapter recyclerAdapter;
    private BottomNavigationView mbottomNavigationView;

    String songName, songUrl, songArtist, imgeUrl;
    MusicService musicService;
    MediaSessionCompat mediaSession;
    int position = 0;

    //public static DatabaseReference mDataBase ;
    public static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reflect();

        ViewPaperAdapter adapter = new ViewPaperAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPaper.setAdapter(adapter);


        MyServiceConnection serviceConnection = new MyServiceConnection();

        Intent intentLuanKhung = new Intent(this, MusicService.class);
        // Bind to the service
        bindService(intentLuanKhung, serviceConnection, BIND_AUTO_CREATE);

        // Unbind from the service
        unbindService(serviceConnection);




        //hien icon trang hien hanh
        mViewPaper.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        mbottomNavigationView.getMenu().findItem(R.id.menu_tab_1).setChecked(true);
                        break;
                    case 1:
                        mbottomNavigationView.getMenu().findItem(R.id.menu_tab_2).setChecked(true);
                        break;
                    case 2:
                        mbottomNavigationView.getMenu().findItem(R.id.menu_tab_3).setChecked(true);
                        break;


                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mbottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_tab_1:
                        mViewPaper.setCurrentItem(0);
                        break;
                    case R.id.menu_tab_2:
                        mViewPaper.setCurrentItem(1);
                        break;
                    case R.id.menu_tab_3:
                        mViewPaper.setCurrentItem(2);
                        break;
                    case R.id.uploadItem:
                        Intent intent = new Intent(getApplicationContext(), UploadSongActivity.class);
                        startActivity(intent);

                }
                return true;
            }
        });

        validatePermision();

        mediaSession = new MediaSessionCompat(this, "PlayerAudio");
        //showNotification(R.drawable.nutplay);

    }

    private ArrayList<song> getAllAudioOnline() {
        ArrayList<song> tempAudioList = new ArrayList<>();
        Log.e("API 24", "WE DID IT");
        mDataBase = FirebaseDatabase.getInstance().getReference("Member");
        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    song musicFiles1 = dataSnapshot.getValue(song.class);
                    //Log.e("file from firebase: ", musicFiles1.getSongName());
                    tempAudioList.add(musicFiles1);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return tempAudioList;
    }


    //Anh xa
    public void reflect(){
        mViewPaper = findViewById(R.id.view_paper);
        mbottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (recyclerAdapter != null) {
            //recyclerAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mViewPaper.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.uploadItem){
            if (checkPermission){
                Intent intent = new Intent(this, UploadSongActivity.class);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validatePermision(){

        Dexter.withActivity(MainActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        checkPermission = true;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        checkPermission = false;
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        return checkPermission;
    }

    // SELECT THE SONG TO UPLOAD FROM MOBILE STORAGE
    private void pickSong() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent,1);
    }

    // upload music at local to firebase
    private void uploadSongToFirebaseStorage() {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("Member").child(uri.getLastPathSegment());

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlSong = uriTask.getResult();
                String urlSongString = urlSong.toString();  // Convert Uri to String

                uploadDetailsToDatabase();
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progres = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                int currentProgress = (int)progres;
                progressDialog.setMessage("Uploaded: "+currentProgress+"%");
            }
        });
    }
    private void uploadDetailsToDatabase() {

        // public song(String imageurl, String songName, String songArtist, String songURL) {
        song songObj = new song(imgeUrl, songName, songArtist, songUrl);

        FirebaseDatabase.getInstance().getReference("Songs")
                .push().setValue(songObj).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Song Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Intent intent = new Intent(this, MusicService.class);
//        //indService(intent, (ServiceConnection) MainActivity.this, BIND_AUTO_CREATE);
//        MyServiceConnection serviceConnection = new MyServiceConnection();
//        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        unbindService((ServiceConnection)this);
//    }

//    // for service - every time we call binder service on any of intent
//    @Override
//    public void onServiceConnected(ComponentName name, IBinder iBinder) {
//        MusicService.MyBinder binder = (MusicService.MyBinder) iBinder;
//        musicService = binder.getService();
//        Log.e("Connected", musicService+"");
//    }
//
//    @Override
//    public void onServiceDisconnected(ComponentName name) {
//        musicService = null;
//        Log.e("Disconnected", musicService + "");
//
//    }


}