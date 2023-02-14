import { NativeEventEmitter, NativeModules } from 'react-native';
const { RNLiveAudioStream } = NativeModules;
const emitter = new NativeEventEmitter(RNLiveAudioStream);

const AudioRecord = {
  init: RNLiveAudioStream.init,
  start: RNLiveAudioStream.start,
  stop: RNLiveAudioStream.stop
};

const eventsMap = {
  data: 'data'
};

AudioRecord.on = (event, callback) => {
  const nativeEvent = eventsMap[event];

  if (!nativeEvent) {
    throw new Error('Invalid event');
  }

  emitter.removeAllListeners(nativeEvent);

  return emitter.addListener(nativeEvent, callback);
};

export default AudioRecord;
