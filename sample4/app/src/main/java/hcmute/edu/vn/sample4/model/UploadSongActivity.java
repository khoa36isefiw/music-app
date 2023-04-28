package hcmute.edu.vn.sample4.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

//import hcmute.edu.vn.sample4.Manifest;
import hcmute.edu.vn.sample4.R;

public class UploadSongActivity extends AppCompatActivity {

    boolean checkPermission = false;
    Uri uriSong, image;
    byte[] bytes;
    String fileName, songUrl, imageUrl;
    String songLength;
    private StorageReference storageReference;
    ProgressDialog progressDialog;
    EditText selectSongNameEditText;
    EditText artistName;
    ImageView selectImage;
    Button uploadButton;
    ImageButton selectSong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_song);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Upload Song");
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        selectSongNameEditText = findViewById(R.id.selectSong);
        selectImage = findViewById(R.id.selectImage);
        uploadButton = findViewById(R.id.uploadSongButton);
        artistName = findViewById(R.id.artistNameEditText);
        selectSong = findViewById(R.id.selectSongButton);

        selectSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickSong();
            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });


    }

    // SELECT THE SONG TO UPLOAD FROM MOBILE STORAGE
    private void pickSong() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent,1);
    }

    private void  pickImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,2);
    }

    // AFTER SELECTING THE SONG FROM MOBILE STORAGE
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 1 && resultCode == RESULT_OK) {
                uriSong = data.getData();
//                Log.i("uri", songName.toString());
                fileName = getFileName(uriSong);
                selectSongNameEditText.setText(fileName);
                songLength = getSongDuration(uriSong);
                Log.i("duration", songLength);
            }
            if (requestCode == 2 && resultCode == RESULT_OK){
//                Log.i("image",data.toString());
                image = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image);
                    selectImage.setImageBitmap(bitmap);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                    bytes = byteArrayOutputStream.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void upload(View view){
        if (uriSong == null){
            Toast.makeText(this, "Please select a song", Toast.LENGTH_SHORT).show();
        }
        else if (selectSongNameEditText.getText().toString().equals("")){
            Toast.makeText(this, "Song name cannot be empty!", Toast.LENGTH_SHORT).show();
        }
        else if(artistName.getText().toString().equals("")){
            Toast.makeText(this, "Please add Artist, album name", Toast.LENGTH_SHORT).show();
        }
        else if (image == null){
            Toast.makeText(this, "Please select a Thumbnail", Toast.LENGTH_SHORT).show();
        }
        else {
            fileName = selectSongNameEditText.getText().toString();
            String artist = artistName.getText().toString();
            uploadImageToServer(bytes,fileName);
            uploadFileToServer(uriSong,fileName,artist,songLength);
        }
    }

    // trong firebase tao ra 1 folder la Thumbnails chua hinh anh cua bai nhac duoc upload len
    public void uploadImageToServer(byte[] image, String fileName) {
        UploadTask uploadTask = storageReference.child("Thumbnails").child(fileName).putBytes(image);
        progressDialog.show();
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                while (!task.isComplete());
                Uri urlsong = task.getResult();
                imageUrl = urlsong.toString();
//                Log.i("image url", imageUrl);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("image url", "failed");
            }
        });
    }

    // METHOD TO HANDEL SONG UPLOAD TO THE STORAGE SERVER

    // chỉnh code lại chỗ này cho đồng nahats với firebase rồi mới load về được
    /*
    * Firebase mac dinh:
    * imageurl
    * songartist
    *  songname
    * songurl
    * */

    // firebase upload tu may
    /*
    * imageurl
    * songArtist
    * songName
    * songUrl
    * songduration
    * */


    //public void uploadFileToServer(Uri uri, final String songName, final String artist, final String duration){
    public void uploadFileToServer(Uri uri, final String songName, final String artist, final String duration){
        StorageReference filePath = storageReference.child("Audios").child(songName);
        progressDialog.show();
        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Log.i("success", "upload");
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlSong = uriTask.getResult();
                songUrl = urlSong.toString();
//                Log.i("success url ", songUrl);
                uploadDetailsToDatabase(imageUrl, artist,fileName,songUrl,duration);
//                progressDialog.dismiss();
            }

        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                int currentProgress = (int) progress;
                progressDialog.setMessage("Uploading: " + currentProgress + "%");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Log.i("success", "upload");
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Upload Failed! Please Try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // UPLOAD SONG NAME AND URL TO REALTIME DATABASE
    public void uploadDetailsToDatabase(String imageUrl, String songName,String artistName,  String songUrl,  String songDuration){

        // check lai trong class song.java
        song songs = new song(imageUrl, artistName,songName,songUrl,songDuration);
        FirebaseDatabase.getInstance().getReference("Member")
                .push().setValue(songs).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i("database", "upload success");
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Song Uploaded to Database", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    // METHOD TO GET THE SONG NAME
    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public String getSongDuration(Uri song){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(getApplicationContext(),song);
        String durationString = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long time = Long.parseLong(durationString);
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(time);
        int totalSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(time);
        int seconds = totalSeconds-(minutes*60);
        if (String.valueOf(seconds).length() == 1){
            return minutes + ":0" + seconds;
        }else {
            return minutes + ":" + seconds;
        }
    }


    private boolean validatePermision (){

        Dexter.withActivity(UploadSongActivity.this)
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



}
