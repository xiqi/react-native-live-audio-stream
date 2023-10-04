
# theInfiTualEr/react-native-live-audio-stream

This package is a modified version of [react-native-live-audio-stream](https://github.com/xiqi/react-native-live-audio-stream) which that itself is a modification of [react-native-audio-record](https://github.com/goodatlas/react-native-audio-record) package. This package adds **play**, **unload** and **generating audio header** functionality to the `react-native-live-audio-stream` package, but **ONLY FOR ANDROID.**

[![npm](https://img.shields.io/npm/v/react-native-live-audio-stream)](https://www.npmjs.com/package/react-native-live-audio-stream)

## Install
```
npm install theInfiTualEr/react-native-live-audio-stream
cd ios
pod install
```

## Add Microphone Permissions

### iOS
Add these lines to ```ios/[YOU_APP_NAME]/info.plist```
```xml
<key>NSMicrophoneUsageDescription</key>
<string>We need your permission to use the microphone.</string>
```

### Android
Add the following line to ```android/app/src/main/AndroidManifest.xml```
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

## Usage
```javascript
import LiveAudioStream from 'react-native-live-audio-stream';
import { PermissionsAndroid } from "react-native";


const options = {
  sampleRate: 32000,  // default is 44100 but 32000 is adequate for accurate voice recognition
  channels: 1,        // 1 or 2, default 1
  bitsPerSample: 16,  // 8 or 16, default 16
  audioSource: 6,     // android only (see below)
  bufferSize: 4096    // default is 2048
  hasAudioHeader: true // default is false, but you probably need it
};

await PermissionsAndroid.request(
  PermissionsAndroid.PERMISSIONS.RECORD_AUDIO
);

LiveAudioStream.init(options);
LiveAudioStream.on('data', data => {
  // base64-encoded audio data chunks

  // below line plays the received audio data
  // NOTE: this DOES NOT WORK on iOS
  LiveAudioStream.addPlay(data);
});
  ...
// NOTE: `loadPlayer` is not necessary on iOS
LiveAudioStream.loadPlayer();
// NOTE: `startPlay` is not necessary on iOS
LiveAudioStream.startPlay();
LiveAudioStream.loadRecorder();
LiveAudioStream.start();
  ...
LiveAudioStream.stop();
LiveAudioStream.unloadRecorder();
// NOTE: `stopPlay` is not necessary on iOS
LiveAudioStream.stopPlay();
// NOTE: `unloadPlayer` is not necessary on iOS
LiveAudioStream.unloadPlayer();
  ...
```

`audioSource` should be one of the constant values from [here](https://developer.android.com/reference/android/media/MediaRecorder.AudioSource). Default value is `6` (`VOICE_RECOGNITION`).

Use 3rd-party modules like [buffer](https://www.npmjs.com/package/buffer) to decode base64 data. Example:
```javascript
// yarn add buffer
import { Buffer } from 'buffer';
  ...
LiveAudioStream.on('data', data => {
  var chunk = Buffer.from(data, 'base64');
});
```

## Credits/References
- [react-native-live-audio-stream](https://github.com/xiqi/react-native-live-audio-stream)
- [react-native-audio-record](https://github.com/goodatlas/react-native-audio-record)
- iOS [Audio Queues](https://developer.apple.com/library/content/documentation/MusicAudio/Conceptual/AudioQueueProgrammingGuide)
- Android [AudioRecord](https://developer.android.com/reference/android/media/AudioRecord.html)
- [cordova-plugin-audioinput](https://github.com/edimuj/cordova-plugin-audioinput)
- [react-native-recording](https://github.com/qiuxiang/react-native-recording)
- [SpeakHere](https://github.com/shaojiankui/SpeakHere)
- [ringdroid](https://github.com/google/ringdroid)

## License 
MIT
