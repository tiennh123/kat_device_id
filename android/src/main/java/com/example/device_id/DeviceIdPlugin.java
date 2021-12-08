package com.example.device_id;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/** DeviceIdPlugin */
public class DeviceIdPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  private MethodChannel channel;
  private Activity activity;
  private TelephonyManager mTelephonyManager;

  private boolean isSimStateReady() {
    return TelephonyManager.SIM_STATE_READY == mTelephonyManager.getSimState();
  }

  private String getCarrierName() {
    String networkOperatorName = mTelephonyManager.getNetworkOperatorName();
    if (networkOperatorName == null) return "";
    return networkOperatorName;
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "device_id");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
    // TODO: your plugin is now attached to an Activity
    this.activity = activityPluginBinding.getActivity();
    mTelephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
  }

  @SuppressLint("HardwareIds")
  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (call.method) {
      case "getGsf": {
        Uri URI = Uri.parse("content://com.google.android.gsf.gservices");
        String ID_KEY = "android_id";
        String params[] = {ID_KEY};
        Cursor c = activity.getContentResolver().query(URI, null, null, params, null);
        if (!c.moveToFirst() || c.getColumnCount() < 2) {
          result.error("1", "Error getting Gsf", "");
        }
        try
        {
          result.success(Long.toHexString(Long.parseLong(c.getString(1))));
        }
        catch (NumberFormatException e)
        {
          result.error("1", "Error getting Gsf", "");
        }
        break;
      }
      case "getMacAddress": {
        try {
          List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
          for (NetworkInterface nif : all) {
            byte[] macBytes = nif.getHardwareAddress();
            if (macBytes == null) {
              result.success("02:00:00:00:00:00");
            }

            StringBuilder res1 = new StringBuilder();
            for (byte b : macBytes) {
              res1.append(String.format("%02X:",b));
            }

            if (res1.length() > 0) {
              res1.deleteCharAt(res1.length() - 1);
            }
            result.success(res1.toString());
          }
        } catch (Exception ex) {
          result.success("02:00:00:00:00:00");
        }
        break;
      }
      case "getCarrierName": {
        if (!isSimStateReady()) {
          result.success("");
          return;
        }
        result.success(getCarrierName());
        break;
      }
      default: {
        result.notImplemented();
      }
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onDetachedFromActivity() {
    // TODO: your plugin is no longer associated with an Activity. Clean up references.
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
    // TODO: your plugin is no longer associated with an Activity. Clean up references.
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    // TODO: your plugin is no longer associated with an Activity. Clean up references.
  }
}
