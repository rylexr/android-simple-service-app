package com.tinbytes.simpleserviceapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class GPXIntentService extends IntentService {
  private static final String TAG = GPXIntentService.class.getName();

  private Handler handler;
  private LocationManager locationManager;

  public GPXIntentService() {
    super("GPXIntentService");
  }

  @Override
  public void onCreate() {
    super.onCreate();
    handler = new Handler();
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
  }

  @Override
  public void onDestroy() {
    locationManager = null;
    super.onDestroy();
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (locationManager != null) {
      handler.post(new Runnable() {
        @Override
        public void run() {
          Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
          String text = location != null ? "Location: [lon=" + location.getLongitude() +
              ",lat=" + location.getLatitude() + "]" : "Unknown Location";
          Toast.makeText(GPXIntentService.this, text, Toast.LENGTH_SHORT).show();
          Log.d(TAG, text);
        }
      });
    }
  }
}
