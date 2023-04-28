package hcmute.edu.vn.sample4.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;


import hcmute.edu.vn.sample4.PlayerActivity;
import hcmute.edu.vn.sample4.R;
import hcmute.edu.vn.sample4.model.song;

//adapter để set up dữ liệu lên từng list_item
public class RecyclerAdapter extends FirebaseRecyclerAdapter<song,RecyclerAdapter.myViewHolder> implements Filterable {

    private Context mContext;
    static ArrayList<song> songList ;   // xét lên Adapter
    ArrayList<song> songListOld ;   // biến trung gian

    ArrayList<song> songItem ;

    public void setSongItem(ArrayList<song> songItem) {
        this.songItem = songItem;
        notifyDataSetChanged();
    }


    public RecyclerAdapter(Context mContext, @NonNull FirebaseRecyclerOptions<song> options, ArrayList<song> mmsonglist) {

        super(options);
        this.mContext = mContext;
        this.songList = mmsonglist;
        this.songListOld = songList;        // để duyệt qua vòng for tìm key word

    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull song model) {
        Glide.with(holder.img_look_thumbnail.getContext())
                .load(model.getImageurl())
                .placeholder(R.drawable.ic_music2)
                .error(com.google.android.gms.base.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.img_look_thumbnail);
        holder.songName.setText(model.getSongName());
        holder.artistName.setText(model.getSongArtist());
        //holder.songDuration.setText(model.getSongduration());

        int index = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity( new Intent(mContext, PlayerActivity.class)
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




    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // key words search in searchView
                String strSearch = constraint.toString();

                //loc du lieu phu hop voi du lieu nay
                if (strSearch.isEmpty()) {      // chưa điền text or không có tên bài hát
                    songList = songListOld;

                } else { // tìm ra những list songs có chứa kí tự trong search
                    ArrayList<song> list = new ArrayList<>();
                    //check bai nhac nao co ky tu ma ta search
                        for (song songss : songListOld) {

                            // kiểm tra thằng nào có ký tự giống trong ô search --> bỏ vào list
                            if (songss.getSongName().toLowerCase().contains(strSearch.toLowerCase())) {
                                list.add(songss);
                            }
                        }
                    songList = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = songList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                songList = (ArrayList<song>) results.values;

                notifyDataSetChanged();
            }
        };
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
            //songDuration = itemView.findViewById(R.id.textViewtimetotal);
        }
    }



}
