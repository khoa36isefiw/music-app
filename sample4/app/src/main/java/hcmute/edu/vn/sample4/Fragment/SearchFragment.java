package hcmute.edu.vn.sample4.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Locale;

import hcmute.edu.vn.sample4.Adapter.HomeAdapter;
import hcmute.edu.vn.sample4.Adapter.MusicAdapter;
import hcmute.edu.vn.sample4.R;
import hcmute.edu.vn.sample4.model.song;


public class SearchFragment extends Fragment {


    SearchView searchView;
    RecyclerView recyclerView;
    HomeAdapter homeAdapter;
    MusicAdapter musicAdapter;
    ArrayList<song> songlist;
    public static DatabaseReference mDataBase ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView= view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.e("API 24", "hihi");
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Member")
                .limitToLast(50);
        FirebaseRecyclerOptions<song> options =
                new FirebaseRecyclerOptions.Builder<song>()
                        .setQuery(query, song.class)
                        .build();


        musicAdapter = new MusicAdapter(getContext(),getAllAudioOnline());
        recyclerView.setAdapter(musicAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query!=null)
                {
                    musicAdapter = new MusicAdapter(getContext(),getAllAudioOnlineWithFilter(query));
                    recyclerView.setAdapter(musicAdapter);



                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText!=null)
                {
                    musicAdapter = new MusicAdapter(getContext(),getAllAudioOnlineWithFilter(newText));
                    recyclerView.setAdapter(musicAdapter);

                }
                return false;
            }
        });
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
                Log.e("API 24", "làn 1");
            }
        });
        return tempAudioList;

    }

    public ArrayList<song>  getAllAudioOnlineWithFilter(String keyword) {
        ArrayList<song> tempAudioList = new ArrayList<>();
        Log.e("API 24", "WE DID IT1");
        mDataBase = FirebaseDatabase.getInstance().getReference("Member");
        mDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    song musicFiles1 = dataSnapshot.getValue(song.class);
                    if(musicFiles1.getSongName().toLowerCase().contains(keyword.toLowerCase()))
                    {
                        Log.e("file from firebase: ", musicFiles1.getSongName());
                        tempAudioList.add(musicFiles1);
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return tempAudioList;

    }
}