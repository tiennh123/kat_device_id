
import 'dart:async';

import 'package:flutter/services.dart';

class DeviceId {
  static const MethodChannel _channel = MethodChannel('device_id');

  static Future<String?> get getGsf async {
    final String? meid = await _channel.invokeMethod('getGsf');
    return meid;
  }

  static Future<String?> get getMacAddress async {
    final String? meid = await _channel.invokeMethod('getMacAddress');
    return meid;
  }

  static Future<String?> get getCarrierName async {
    final String? carrierName = await _channel.invokeMethod('getCarrierName');
    return carrierName;
  }
}
