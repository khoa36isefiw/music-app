package hcmute.edu.vn.sample4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hcmute.edu.vn.sample4.Adapter.ViewPaperAdapter;
import hcmute.edu.vn.sample4.model.song;

public class MainActivity extends AppCompatActivity {


    private ViewPager mViewPaper;
    public static ArrayList<song> songList;
    private BottomNavigationView mbottomNavigationView;
    //public static DatabaseReference mDataBase ;
    public static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reflect();

        ViewPaperAdapter adapter = new ViewPaperAdapter(getSupportFragmentManager(),FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPaper.setAdapter(adapter);


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

                }
                return true;
            }
        });

    }
/*   @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Log.e("API 29", "WE DID IT");
                    //musicFiles = getAllAudioForHigherAPI(this);

                   songList = getAllAudioOnline();
                } else {

                    songList = getAllAudioOnline();
                }
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }*/
  /*  public void getAllAudioOnline() {
        ArrayList<song> tempAudioList = new ArrayList<>();
        Log.e("API 24", "WE DID IT");
        mDataBase = FirebaseDatabase.getInstance().getReference("Member");
        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    song musicFiles1 = dataSnapshot.getValue(song.class);
                    //Log.e("file from firebase: ", musicFiles1.getSongName());

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }*/
    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }




   /* public static ArrayList<song> getAllAudioOnline() {
        ArrayList<song> tempAudioList = new ArrayList<>();

        mDataBase = FirebaseDatabase.getInstance().getReference("Member");
        mDataBase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    song musicFiles = dataSnapshot.getValue(song.class);
                    //Log.e("file from firebase: ", musicFiles1.getSongName());
                    songList.add(musicFiles);
                }

                Log.e("Temp list size  : ", String.valueOf(songList.size()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        return tempAudioList;

    }*/
    //Anh xa
    public void reflect(){
        mViewPaper = findViewById(R.id.view_paper);
        mbottomNavigationView = findViewById(R.id.bottom_navigation);
    }
}