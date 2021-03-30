
# react-native-live-audio-stream
[![npm](https://img.shields.io/npm/v/react-native-live-audio-stream)](https://www.npmjs.com/package/react-native-live-audio-stream)

Get live audio stream data for React Native. Ideal for live voice recognition (transcribing).

This module is modified from [react-native-audio-record](https://github.com/goodatlas/react-native-audio-record). Instead of saving to an audio file, it only emit events with live data. By doing this, it can reduce memory usage and eliminate file operation overheads in the case that an audio file is not necessary (e.g. live transcribing).

Most of the code was written by the respective original authors.

## Install
```
yarn add react-native-live-audio-stream
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

const options = {
  sampleRate: 32000,  // default is 44100 but 32000 is adequate for accurate voice recognition
  channels: 1,        // 1 or 2, default 1
  bitsPerSample: 16,  // 8 or 16, default 16
  audioSource: 6,     // android only (see below)
  bufferSize: 4096    // default is 2048
};

LiveAudioStream.init(options);
LiveAudioStream.on('data', data => {
  // base64-encoded audio data chunks
});
  ...
LiveAudioStream.start();
  ...
LiveAudioStream.stop();
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
- [react-native-audio-record](https://github.com/goodatlas/react-native-audio-record)
- iOS [Audio Queues](https://developer.apple.com/library/content/documentation/MusicAudio/Conceptual/AudioQueueProgrammingGuide)
- Android [AudioRecord](https://developer.android.com/reference/android/media/AudioRecord.html)
- [cordova-plugin-audioinput](https://github.com/edimuj/cordova-plugin-audioinput)
- [react-native-recording](https://github.com/qiuxiang/react-native-recording)
- [SpeakHere](https://github.com/shaojiankui/SpeakHere)
- [ringdroid](https://github.com/google/ringdroid)

## License 
MIT
