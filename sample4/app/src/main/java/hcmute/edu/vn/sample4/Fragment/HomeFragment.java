package hcmute.edu.vn.sample4.Fragment;


import static android.content.Context.BIND_AUTO_CREATE;
import static hcmute.edu.vn.sample4.Service.ApplicationClass.ACTION_NEXT;
import static hcmute.edu.vn.sample4.Service.ApplicationClass.ACTION_PLAY;
import static hcmute.edu.vn.sample4.Service.ApplicationClass.ACTION_PREV;
import static hcmute.edu.vn.sample4.Service.ApplicationClass.CHANNEL_ID_2;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import android.os.IBinder;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.ArrayList;

import hcmute.edu.vn.sample4.Adapter.HomeAdapter;
import hcmute.edu.vn.sample4.Adapter.RecyclerAdapter;
import hcmute.edu.vn.sample4.MainActivity;
import hcmute.edu.vn.sample4.R;
import hcmute.edu.vn.sample4.Service.MusicService;
import hcmute.edu.vn.sample4.model.UploadSongActivity;
import hcmute.edu.vn.sample4.model.song;


public class HomeFragment extends Fragment implements ServiceConnection {

    View view;

    private boolean checkPermission = false;
    UploadSongActivity uploadSongActivity;
    Uri uri;
    public static ArrayList<song> songList;
    ViewPager viewPager;
    String songName, songUrl, songArtist, imgeUrl;
    MusicService musicService;
    MediaSessionCompat mediaSession;
    int position = 0;
    RecyclerAdapter recyclerAdapter;
    RecyclerView recyclerView;
    public static DatabaseReference mDataBase ;
    static ArrayList<song> tempAudioList = new ArrayList<>();

    private MainActivity hm;
    HomeAdapter homeAdapter;

    // dùng để xét event cho các button
    ImageButton btnPlay, btnNext, btnPrevious, btnLoopSong, btnShuffleSong;
    TextView txtSongName, txtArtist, txtTimeStart, txtTimeRunning;
    SeekBar mSeekBar;

    MediaSessionCompat mediaSessionCompat;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        hm = (MainActivity) getActivity();








        recyclerView = view.findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Member")
                .limitToLast(50);
        FirebaseRecyclerOptions<song> options =
                new FirebaseRecyclerOptions.Builder<song>()
                        .setQuery(query, song.class)
                        .build();


        homeAdapter = new HomeAdapter(getContext(),options,getAllAudioOnline());

        recyclerView.setAdapter(homeAdapter);
        Log.e("file from firebase: ","ok");



        Intent intent1 = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent1, this, BIND_AUTO_CREATE);

        Activity activity = getActivity();
        if (activity instanceof UploadSongActivity) {
            uploadSongActivity = (UploadSongActivity) activity;
        } else {
            // Handle the case where the activity is not an instance of UploadSongActivity
            Log.e("Bug Hơi nhiều đó: ","from Khoa");
        }


        //mediaSession = new MediaSessionCompat(this, "PlayerAudio");
        //showNotification(R.drawable.nutplay);
        return view;

    }
    public ArrayList<song> getAllAudioOnline() {
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


    @Override
    public void onStart() {
        super.onStart();
        homeAdapter.startListening();


    }

    @Override
    public void onStop() {
        super.onStop();
        homeAdapter.stopListening();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        recyclerAdapter.startListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        recyclerAdapter.stopListening();
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_navigation, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.uploadItem){
            if (checkPermission){
                Intent intent = new Intent(getActivity(), UploadSongActivity.class);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validatePermission(Context context) {
        Dexter.withContext(context)
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

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
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
                Toast.makeText(getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getContext(), "Song Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unbindService(this);
    }


//    // for service - every time we call binder service on any of intent
    @Override
    public void onServiceConnected(ComponentName name, IBinder iBinder) {
        MusicService.MyBinder binder = (MusicService.MyBinder) iBinder;
        musicService = binder.getService();
        Log.e("Connected", musicService+"");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
        Log.e("Disconnected", musicService + "");

    }

    // chuyen qua Player
//    public void showNotification(int playPauseBTN) {
//        Intent intent = new Intent(this, HomeFragment.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this,
//                0, intent, 0);
//
//        // previous button
//        Intent prevIntent = new Intent(this, NotificationRecevier.class).setAction(ACTION_PREV);
//        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this,
//                0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // previous button
//        Intent playIntent = new Intent(this, NotificationRecevier.class).setAction(ACTION_PLAY);
//        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this,
//                0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // previous button
//        Intent nextIntent = new Intent(this, NotificationRecevier.class).setAction(ACTION_NEXT);
//        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this,
//                0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        if (songList != null && position >= 0 && position < songList.size()) {
//            String imageUrl = songList.get(position).getImageurl();
//            int resId = getResources().getIdentifier(imageUrl, "drawable", getPackageName());
//            Bitmap picture = BitmapFactory.decodeResource(getResources(), resId);
//            // Build the notification using the image and other information
//
//            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
//                    .setSmallIcon(resId) // Use the resource ID here
//                    .setLargeIcon(picture)
//                    .setContentTitle(songList.get(position).getSongName())
//                    .setContentText(songList.get(position).getSongArtist())
//                    .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", prevPendingIntent)
//                    .addAction(playPauseBTN, "Play", playPendingIntent)
//                    .addAction(R.drawable.iconnext, "Next", nextPendingIntent)
//                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                            .setMediaSession(mediaSession.getSessionToken()))
//                    .setPriority(NotificationCompat.PRIORITY_LOW)
//                    .setContentIntent(contentIntent)
//                    .setOnlyAlertOnce(true)
//                    .build();
//
//            NotificationManager notificationManager = (NotificationManager)
//                    getSystemService(NOTIFICATION_SERVICE);
//
//            notificationManager.notify(0, notification);
//        }
//
//
//    }


}