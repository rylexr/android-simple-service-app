package com.tinbytes.simpleserviceapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class SimpleServiceActivity extends AppCompatActivity implements ServiceConnection {
  private static final String TAG = SimpleServiceActivity.class.getSimpleName();
  private static final String PACKAGE_NAME = "com.tinbytes.simpleserviceapp";

  private IRemoteInterface remoteInterface;
  private boolean bound;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_simple_service);
    findViewById(R.id.bStartLocationService).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        doStartLocationService();
      }
    });
    findViewById(R.id.bStopLocationService).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        doStopLocationService();
      }
    });
    findViewById(R.id.bBind).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        doBindService();
      }
    });
    findViewById(R.id.bGetLocation).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        doGetLocation();
      }
    });
    findViewById(R.id.bUnbind).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        doUnbindService();
      }
    });
    findViewById(R.id.bShowLocation).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        doShowLocation();
      }
    });
  }

  private void doShowLocation() {
    Intent i = new Intent(this, GPXIntentService.class);
    i.setPackage(PACKAGE_NAME); // Workaround for explicit intents in Lollipop
    startService(i);
  }

  private void doStartLocationService() {
    Intent i = new Intent(GPXService.SERVICE_ACTION);
    i.setPackage(PACKAGE_NAME); // Workaround for explicit intents in Lollipop
    i.putExtra(GPXService.EXTRA_UPDATE_RATE, 5000);
    startService(i);
  }

  private void doStopLocationService() {
    Intent i = new Intent(GPXService.SERVICE_ACTION);
    i.setPackage(PACKAGE_NAME); // Workaround for explicit intents in Lollipop
    stopService(i);
  }

  private void doGetLocation() {
    if (remoteInterface == null) {
      Toast.makeText(this, "Bind the remote service before calling 'Get Location'", Toast.LENGTH_SHORT).show();
      return;
    }
    try {
      Location l = remoteInterface.getLastLocation();
      String text = l != null ? "Location: [lon=" + l.getLongitude() +
          ",lat=" + l.getLatitude() + "]" : "Unknown Location";
      Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
      Log.d(TAG, text);
    } catch (RemoteException e) {
      Toast.makeText(this, "Location cannot be acquired", Toast.LENGTH_SHORT).show();
      Log.e(TAG, "Error acquiring location", e);
    }
  }

  private void doBindService() {
    Intent i = new Intent(IRemoteInterface.class.getName());
    i.setPackage(PACKAGE_NAME); // Workaround for explicit intents in Lollipop
    bound = bindService(i, this, Context.BIND_AUTO_CREATE);
  }

  public void onServiceConnected(ComponentName name, IBinder service) {
    remoteInterface = IRemoteInterface.Stub.asInterface(service);
    Toast.makeText(this, "Service connected", Toast.LENGTH_SHORT).show();
  }

  public void onServiceDisconnected(ComponentName name) {
    remoteInterface = null;
    Toast.makeText(this, "Service disconnected", Toast.LENGTH_SHORT).show();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    doUnbindService();
  }

  private void doUnbindService() {
    if (bound) {
      unbindService(this);
      bound = false;
    }
  }
}
