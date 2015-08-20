package com.tinbytes.simpleserviceapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class GPXServiceRemote extends Service {
  private LocationManager locationManager;

  @Override
  public void onCreate() {
    super.onCreate();
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
  }

  @Override
  public void onDestroy() {
    locationManager = null;
    super.onDestroy();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    // we only have one, so no need to check the intent
    return new IRemoteInterface.Stub() {
      public Location getLastLocation() {
        if (locationManager != null) {
          return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
        return null;
      }
    };
  }
}
