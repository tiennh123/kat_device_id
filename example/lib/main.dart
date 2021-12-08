import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:device_id/device_id.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _gsf = 'Unknown';
  String _macAddress = 'Unknown';
  String _carrierName = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String gsf = '';
    String macAddress = '';
    String carrierName = '';
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      gsf = await DeviceId.getGsf ?? '';
      macAddress = await DeviceId.getMacAddress ?? '02:00:00:00:00:00';
      carrierName = await DeviceId.getCarrierName ?? '';
    } on PlatformException {
      gsf = 'Failed to get platform version.';
      macAddress = 'Failed to get platform version.';
      carrierName = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _gsf = 'GSF: $gsf';
      _macAddress = 'MAC ADDRESS: $macAddress';
      _carrierName = 'CARRIER NAME: $carrierName';
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Center(
              child: Text(_gsf),
            ),
            Center(
              child: Text(_macAddress),
            ),
            Center(
              child: Text(_carrierName),
            ),
          ],
        ),
      ),
    );
  }
}
