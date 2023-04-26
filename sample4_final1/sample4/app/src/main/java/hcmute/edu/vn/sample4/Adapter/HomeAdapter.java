package hcmute.edu.vn.sample4.Adapter;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hcmute.edu.vn.sample4.PlayerActivity;
import hcmute.edu.vn.sample4.R;
import hcmute.edu.vn.sample4.model.song;


//adapter để set up dữ liệu lên từng list_item
public class HomeAdapter extends FirebaseRecyclerAdapter<song,HomeAdapter.myViewHolder> {
    private Context mContext;
    static ArrayList<song> songList ;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public HomeAdapter(Context context,@NonNull FirebaseRecyclerOptions<song> options,ArrayList<song> songlist) {
        super(options);
        this.mContext = context;
        this.songList = songlist;
    }



    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull song model) {
        Glide.with(holder.img_look_thumbnail.getContext())
                .load(model.getImageurl())
                .placeholder(R.drawable.ic_music)
                .error(com.google.android.gms.base.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.img_look_thumbnail);
        holder.songName.setText(model.getSongName());
        holder.artistName.setText(model.getSongArtist());
//        holder.songDuration.setText(model.getSongduration());
        int index = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity( new Intent(mContext,PlayerActivity.class)
                        .putExtra("songs",songList)
                        .putExtra("pos",index)
                        .putExtra("songname",model.getSongName())
                );
            }
        });
    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new myViewHolder(view);
    }






    // hàm để set giá trị cho từng item trong list
    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView songName, artistName, songDuration;
        ImageView img_look_thumbnail;
        CardView cardView;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            songName = itemView.findViewById(R.id.txtSong); // txtSong <=> Song Name
            img_look_thumbnail = itemView.findViewById(R.id.songImg);
            cardView = itemView.findViewById(R.id.card_view_id);
            artistName = itemView.findViewById(R.id.txtArtist);
            songDuration = itemView.findViewById(R.id.textViewtimetotal);
        }
    }




}
