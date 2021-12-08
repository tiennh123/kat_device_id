import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:device_id/device_id.dart';

void main() {
  const MethodChannel channel = MethodChannel('device_id');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await DeviceId.getGsf, '42');
    expect(await DeviceId.getMacAddress, '42');
    expect(await DeviceId.getCarrierName, '42');
  });
}
