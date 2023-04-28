package hcmute.edu.vn.sample4.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

import hcmute.edu.vn.sample4.PlayerActivity;
import hcmute.edu.vn.sample4.R;
import hcmute.edu.vn.sample4.Service.MusicService;
import hcmute.edu.vn.sample4.model.song;


public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.myViewHolder> {
    private Context mContext;
    static ArrayList<song> songList;

    public MusicAdapter(Context context, ArrayList<song> songlist) {
        this.mContext = context;
        this.songList = songlist;
    }



    private void ClickStartService(int position) {
        Intent intent = new Intent(mContext, MusicService.class)
                .putExtra("songs",songList)
                .putExtra("pos",position);
        mContext.startService(intent);



    }
    /*private byte[] getAlbumArt(String uri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }*/

/*    @SuppressLint("MissingPermission")
    public void showNotification(int position) {
        MediaSessionCompat sessionCompat = new MediaSessionCompat(mContext, "tag");
        Intent intent;
        intent = new Intent(mContext, HomeFragment.class);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext,
                0, intent, 0);

        // previous button
        Intent prevIntent = new Intent(mContext, NotificationRecevier.class).setAction(ACTION_PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(mContext,
                0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // previous button
        Intent playIntent = new Intent(mContext, NotificationRecevier.class).setAction(ACTION_PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(mContext,
                0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // previous button
        Intent nextIntent = new Intent(mContext, NotificationRecevier.class).setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(mContext,
                0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Resources resources = null;

        if (songList != null && position >= 0 && position < songList.size()) {


            Notification notification = new NotificationCompat.Builder(mContext, CHANNEL_ID_2)
                    .setSmallIcon(R.drawable.ic_launcher_foreground) // Use the resource ID here
                    .setContentTitle(songList.get(position).getSongName())
                    .setContentText(songList.get(position).getSongArtist())
                    .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", prevPendingIntent)
                    .addAction(R.drawable.ic_play, "Play", playPendingIntent)
                    .addAction(R.drawable.iconnext, "Next", nextPendingIntent)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(sessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setContentIntent(contentIntent)
                    .setOnlyAlertOnce(true)
                    .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
            notificationManager.notify(1, notification);
            Log.e("ok chuwa","ok");


        }


    }*/



    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        Glide.with(holder.img_look_thumbnail.getContext())
                .load(songList.get(position).getImageurl())
                .placeholder(R.drawable.ic_music)
                .error(com.google.android.gms.base.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.img_look_thumbnail);
        holder.songName.setText(songList.get(position).getSongName());
        holder.artistName.setText(songList.get(position).getSongArtist());
//        holder.songDuration.setText(model.getSongduration());
        int index = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, PlayerActivity.class)
                        .putExtra("songs", songList)
                        .putExtra("pos", index)
                        .putExtra("songname", songList.get(index).getSongName())

                );

                ClickStartService(index);
            }


        });
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


    @Override
    public int getItemCount() {
        return songList.size();
    }

    private byte[] getAlbumArt(String uri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    //hàm cập nhật lại danh sách bài hát trong songs Fragment sau khi tìm kiếm
    public void updateList(ArrayList<song> musicFilesArrayList){
        songList = new ArrayList<>();
        songList.addAll(musicFilesArrayList);
        //thông báo cập nhật lại recycleview vì danh sách bài hát sau khí search đã thay đổi
        notifyDataSetChanged();
    }
}
