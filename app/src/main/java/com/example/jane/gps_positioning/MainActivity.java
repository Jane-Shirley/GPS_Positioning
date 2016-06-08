package com.example.jane.gps_positioning;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    LocService lService;

    boolean bound = false;

    TextView lat, lon, speed, dist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* lat = (TextView) findViewById(R.id.lat);
        lon = (TextView) findViewById(R.id.lon);
        speed = (TextView) findViewById(R.id.speed);
        dist = (TextView) findViewById(R.id.dist);*/

    }

    //Service connection callback
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocService.MyBinder bind = (LocService.MyBinder) service;
            lService = bind.getService();   //object for the service LocService
            bound = true;

            /*myPosProxy = IMyPositionInterface.Stub.asInterface(service);*/

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    //start button clicked
    public void startLocService(View v) {
        Intent i = new Intent(this, LocService.class);
        startService(i);
        bindService(i,mServiceConnection,Context.BIND_AUTO_CREATE);
    }

    //stop button clicked
    public void stopLocService(View v) {
        if(bound) {
            unbindService(mServiceConnection);
            bound = false;
        }
        stopService(new Intent(this, LocService.class));
    }

    /*//update button clicked
    public void updateValues(View v) {
        try {
            lat.setText(Double.toString(myPosProxy.getLatitude()));
            lon.setText(Double.toString(myPosProxy.getLongitude()));
        }
        catch (Exception e) {
            Log.e("error","",e);
        }
    }*/
}
