package hcmute.edu.vn.sample4.Service;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationRecevier extends BroadcastReceiver {
    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_PREV = "PREVIOUS";



    //kiểm tra hành động của Intent và khởi động một
    // dịch vụ âm nhạc với hành động tương ứng nếu
    // nhận được một trong các hành động được định nghĩa sẵn.
    @Override
    public void onReceive(Context context, Intent intent) {


        // create an intent
        //một Intent mới được tạo ra với lớp được chỉ định là MusicService
        Intent intent1 = new Intent(context, MusicService.class);


        // hành động có thể được nhận được trong intent.
        if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case ACTION_PLAY:
                        Toast.makeText(context, "Play", Toast.LENGTH_SHORT).show();

                        // a new intent that will start the service
                        intent1.putExtra("myActionName", intent.getAction());
                        context.startService(intent1);
                        break;

                    case ACTION_NEXT:
                        Toast.makeText(context, "Next", Toast.LENGTH_SHORT).show();
                        intent1.putExtra("myActionName", intent.getAction());
                        context.startService(intent1);
                        break;

                    case ACTION_PREV:
                        Toast.makeText(context, "Previous", Toast.LENGTH_SHORT).show();
                        intent1.putExtra("myActionName", intent.getAction());
                        context.startService(intent1);
                        break;


                }
        }

    }
}
