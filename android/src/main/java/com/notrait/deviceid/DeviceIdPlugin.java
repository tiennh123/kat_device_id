package com.notrait.deviceid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * DeviceIdPlugin
 */
public class DeviceIdPlugin implements MethodCallHandler {
    private final Activity activity;

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {

        final MethodChannel channel = new MethodChannel(registrar.messenger(), "device_id");
        channel.setMethodCallHandler(new DeviceIdPlugin(registrar.activity()));
    }

    private DeviceIdPlugin(Activity activity) {
        this.activity = activity;
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "getID":
                result.success(Secure.getString(activity.getContentResolver(), Secure.ANDROID_ID));
                break;
            case "getIMEI": {
                TelephonyManager manager = (TelephonyManager) activity.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String imei = manager.getImei();
                    if (imei == null) {
                        result.error("1", "Error getting IMEI", "");
                    }
                    result.success(imei);
                } else {
                    result.error("1", "IMEI is not available for API versions lower than 26", "");
                }
                break;
            }
            case "getMEID": {
                TelephonyManager manager = (TelephonyManager) activity.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String imei = manager.getMeid();
                    if (imei == null) {
                        result.error("1", "Error getting MEID", "");
                    }
                    result.success(imei);
                } else {
                    result.error("1", "MEID is not available for API versions lower than 26", "");
                }
                break;
            }
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
                    int mgmtInfoBase[6];
                    char *msgBuffer = NULL;
                    size_t length;
                    unsigned char macAddress[6];
                    struct if_msghdr *interfaceMsgStruct;
                    struct sockaddr_dl *socketStruct;
                    NSString *errorFlag = NULL;
 
                    // Setup the management Information Base (mib)
                    mgmtInfoBase[0] = CTL_NET;        // Request network subsystem
                    mgmtInfoBase[1] = AF_ROUTE;       // Routing table info
                    mgmtInfoBase[2] = 0;
                    mgmtInfoBase[3] = AF_LINK;        // Request link layer information
                    mgmtInfoBase[4] = NET_RT_IFLIST;  // Request all configured interfaces
                
                    // With all configured interfaces requested, get handle index
                    if ((mgmtInfoBase[5] = if_nametoindex("en0")) == 0)
                        errorFlag = @"if_nametoindex failure";
                    else
                    {
                        // Get the size of the data available (store in len)
                        if (sysctl(mgmtInfoBase, 6, NULL, &length, NULL, 0) < 0)
                            errorFlag = @"sysctl mgmtInfoBase failure";
                        else
                        {
                            // Alloc memory based on above call
                            if ((msgBuffer = malloc(length)) == NULL)
                                errorFlag = @"buffer allocation failure";
                            else
                            {
                                // Get system information, store in buffer
                                if (sysctl(mgmtInfoBase, 6, msgBuffer, &length, NULL, 0) < 0)
                                    errorFlag = @"sysctl msgBuffer failure";
                            }
                        }
                    }
                
                    // Befor going any further...
                    if (errorFlag != NULL)
                    {
                        NSLog(@"Error: %@", errorFlag);
                        return errorFlag;
                    }
                
                    // Map msgbuffer to interface message structure
                    interfaceMsgStruct = (struct if_msghdr *) msgBuffer;
                
                    // Map to link-level socket structure
                    socketStruct = (struct sockaddr_dl *) (interfaceMsgStruct + 1);
                
                    // Copy link layer address data in socket structure to an array
                    memcpy(&macAddress, socketStruct->sdl_data + socketStruct->sdl_nlen, 6);
                
                    // Read from char array into a string object, into traditional Mac address format
                    NSString *macAddressString = [NSString stringWithFormat:@"%02x:%02x:%02x:%02x:%02x:%02x",
                                                macAddress[0], macAddress[1], macAddress[2],
                                                macAddress[3], macAddress[4], macAddress[5]];
                    NSLog(@"Mac Address: %@", macAddressString);
                
                    // Release the buffer memory
                    free(msgBuffer);
                
                    return macAddressString;

                } catch (Exception ex) { 
                    return "02:00:00:00:00:00";
                }
            }
            default:
                result.notImplemented();
                break;
        }
    }
}
