package com.tinbytes.simpleserviceapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class GPXService extends Service implements LocationListener {
  public static final String SERVICE_ACTION = "com.tinbytes.simpleserviceapp.GPXService.SERVICE";
  public static final String EXTRA_UPDATE_RATE = "extra-update-rate";
  private static final int GPS_NOTIFY = 123456;
  private static final String TAG = GPXService.class.getSimpleName();
  private LocationManager locationManager = null;
  private NotificationManager notificationManager = null;

  @Override
  public void onCreate() {
    super.onCreate();
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    doServiceStart(intent);
    return Service.START_REDELIVER_INTENT;
  }

  private void doServiceStart(Intent intent) {
    int updateRate = intent.getIntExtra(EXTRA_UPDATE_RATE, -1);
    if (updateRate == -1) {
      updateRate = 60000;
    }
    Criteria criteria = new Criteria();
    criteria.setAccuracy(Criteria.NO_REQUIREMENT);
    criteria.setPowerRequirement(Criteria.POWER_LOW);
    String bestProvider = locationManager.getBestProvider(criteria, true);
    locationManager.requestLocationUpdates(bestProvider, updateRate, 0, this);
    Intent toLaunch = new Intent(getApplicationContext(), SimpleServiceActivity.class);
    PendingIntent intentBack = PendingIntent.getActivity(getApplicationContext(), 0, toLaunch, 0);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
    builder.setTicker("Builder GPS Tracking");
    builder.setSmallIcon(android.R.drawable.stat_notify_more);
    builder.setWhen(System.currentTimeMillis());
    builder.setContentTitle("Builder GPS Tracking");
    builder.setContentText("Tracking start at " + updateRate + "ms intervals with [" + bestProvider + "] as the provider.");
    builder.setContentIntent(intentBack);
    builder.setAutoCancel(true);
    Notification notify = builder.build();
    notificationManager.notify(GPS_NOTIFY, notify);
  }

  @Override
  public void onDestroy() {
    if (locationManager != null) {
      locationManager.removeUpdates(this);
      locationManager = null;
    }
    Intent toLaunch = new Intent(getApplicationContext(), SimpleServiceActivity.class);
    PendingIntent intentBack = PendingIntent.getActivity(getApplicationContext(), 0, toLaunch, 0);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
    builder.setTicker("Builder GPS Tracking");
    builder.setSmallIcon(android.R.drawable.stat_notify_more);
    builder.setWhen(System.currentTimeMillis());
    builder.setContentTitle("Builder GPS Tracking");
    builder.setContentText("Tracking stopped");
    builder.setContentIntent(intentBack);
    builder.setAutoCancel(true);
    Notification notify = builder.build();
    notificationManager.notify(GPS_NOTIFY, notify);
    super.onDestroy();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onLocationChanged(Location location) {
    String text = "Location: [lon=" + location.getLongitude() +
        ",lat=" + location.getLatitude() + "]";
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    Log.d(TAG, text);
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
}
