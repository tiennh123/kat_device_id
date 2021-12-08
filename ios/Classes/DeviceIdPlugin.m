#import "DeviceIdPlugin.h"
#if __has_include(<device_id/device_id-Swift.h>)
#import <device_id/device_id-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "device_id-Swift.h"
#endif

@implementation DeviceIdPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftDeviceIdPlugin registerWithRegistrar:registrar];
}
@end
