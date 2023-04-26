package hcmute.edu.vn.sample4.Fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.edu.vn.sample4.R;

public class DiskFragment extends Fragment {

    View view;
    CircleImageView circleImageView;
    ObjectAnimator objectAnimator;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_disk, container, false);
        circleImageView = view.findViewById(R.id.imageviewcircle);
        objectAnimator = ObjectAnimator.ofFloat(circleImageView, "rotation", 0f, 360f);
        objectAnimator.setDuration(20000);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
        return view;
    }
    public void addImage(byte[] art) {
        Glide.with(this).asBitmap()
                .load(art)
                .into(circleImageView);
    }

    public void setAnim(Context context, Bitmap bitmap) {
        Glide.with(context).load(bitmap).into(circleImageView);
    }

    public void startAnimation(Animation anim) {
        circleImageView.startAnimation(anim);
    }


}