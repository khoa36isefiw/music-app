package hcmute.edu.vn.sample4.model;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import hcmute.edu.vn.sample4.Service.MusicService;

public class MyServiceConnection implements ServiceConnection {
    MusicService musicService;
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        // Service is connected
        MusicService.MyBinder binder = (MusicService.MyBinder) iBinder;
        musicService = binder.getService();
        Log.e("Connected", musicService+"");
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        // Service is disconnected
        musicService = null;
        Log.e("Disconnected", musicService + "");
    }
}