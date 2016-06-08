package com.example.jane.gps_positioning;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Jane on 6/6/2016.
 */

public class LocService extends Service {

    private IBinder binder = new MyBinder();
    private File file;
    OutputStreamWriter myWriter;
    LocationManager locationManager;
    Location start, end;

    private static final String header = "<gpx version=\"1.0\">\n<trk>\n<trkseg>";
    private static final String footer = "</trkseg></trk></gpx>";

    //********************onCreate*********************
    @Override
    public void onCreate() {
        Log.i("log", "onCreate");

        //create new file in sdcard

        try{
            file = new File(Environment.getExternalStorageDirectory(),"location.gpx");
//            file = new File(Environment.getExternalStorageDirectory(),"trace.txt");
            file.createNewFile();
            myWriter = new OutputStreamWriter(new FileOutputStream(file));
            myWriter.append(header);
        }
        catch (Exception e) { Log.e("error", "could not create file", e);}

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(i);
        }
    }

    //******************onStart********************
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        Log.i("log","start");

        //register for location data
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 50, locListener);

        return START_NOT_STICKY;
    }

    //listen for changes in the location
    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            writeToFile(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void writeToFile(Location loc) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        String str = "<trkpt lat=\""+loc.getLatitude()+"\" lon=\""+loc.getLongitude()+"\"><time>"+df.format(loc.getTime())+"</time></trkpt>";
        try{
            myWriter.append(str);
        }
        catch (Exception e){Log.e("error","",e);}
    }

    //****************onDestroy****************
    @Override
    public void onDestroy() {
        super.onDestroy();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.removeUpdates(locListener);
        try{
            myWriter.append(footer);
            myWriter.close();
        }
        catch (Exception e) { Log.e("error","",e);}
        Log.i("log","destroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("log", "bind");
        return binder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.i("log","rebind");
    }

    public boolean onUnbind(Intent intent) {
        Log.i("log","unbind");
        return true;
    }

    //return an instance of this class to MainActivity
    public class MyBinder extends Binder {
        LocService getService() {
            return LocService.this;
        }
    }
}


