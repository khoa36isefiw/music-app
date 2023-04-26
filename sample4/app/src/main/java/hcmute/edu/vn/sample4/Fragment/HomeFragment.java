package hcmute.edu.vn.sample4.Fragment;



import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hcmute.edu.vn.sample4.Adapter.HomeAdapter;
import hcmute.edu.vn.sample4.Adapter.HomeViewPager;
import hcmute.edu.vn.sample4.MainActivity;
import hcmute.edu.vn.sample4.R;
import hcmute.edu.vn.sample4.model.song;


public class HomeFragment extends Fragment {

    View view;

    ViewPager viewPager;
    RecyclerView recyclerView;
    public static DatabaseReference mDataBase ;
    static ArrayList<song> tempAudioList = new ArrayList<>();
    private  ArrayList<song> listsong;
    private MainActivity hm;
    HomeAdapter homeAdapter;


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
}