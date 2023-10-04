import { NativeModules, NativeEventEmitter } from 'react-native';
const { RNLiveAudioStream } = NativeModules;
const EventEmitter = new NativeEventEmitter(RNLiveAudioStream);

const AudioRecord = {};

AudioRecord.init = options => RNLiveAudioStream.init(options);
AudioRecord.loadPlayer = () => {};
AudioRecord.unloadPlayer = () => {};
AudioRecord.loadRecorder = () => {};
AudioRecord.unloadRecorder = () => {};
AudioRecord.start = () => RNLiveAudioStream.start();
AudioRecord.stop = () => RNLiveAudioStream.stop();
AudioRecord.startPlay = () => {};
AudioRecord.addPlay = () => {};
AudioRecord.stopPlay = () => {};

const eventsMap = {
  data: 'data'
};

AudioRecord.on = (event, callback) => {
  const nativeEvent = eventsMap[event];
  if (!nativeEvent) {
    throw new Error('Invalid event');
  }
  EventEmitter.removeAllListeners(nativeEvent);
  return EventEmitter.addListener(nativeEvent, callback);
};

export default AudioRecord;
