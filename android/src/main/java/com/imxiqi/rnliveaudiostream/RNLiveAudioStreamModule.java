package com.imxiqi.rnliveaudiostream;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.AudioManager;
import android.media.MediaRecorder.AudioSource;
import android.util.Base64;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.lang.Math;

public class RNLiveAudioStreamModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter;

    private int sampleRateInHz;
    private int channelConfig;
    private int audioFormat;
    private int audioSource;
    private boolean hasAudioHeader;

    private AudioTrack player;
    private AudioRecord recorder;
    private int bufferSize;
    private boolean isRecording;

    private byte[] wavHeaders;

    public RNLiveAudioStreamModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNLiveAudioStream";
    }

    @ReactMethod
    public void init(ReadableMap options) {
        sampleRateInHz = 44100;
        if (options.hasKey("sampleRate")) {
            sampleRateInHz = options.getInt("sampleRate");
        }

        channelConfig = AudioFormat.CHANNEL_IN_MONO;
        if (options.hasKey("channels") && options.getInt("channels") == 2) {
            channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        }

        audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        if (options.hasKey("bitsPerSample") && options.getInt("bitsPerSample") == 8) {
            audioFormat = AudioFormat.ENCODING_PCM_8BIT;
        }

        audioSource = AudioSource.VOICE_RECOGNITION;
        if (options.hasKey("audioSource")) {
            audioSource = options.getInt("audioSource");
        }

        hasAudioHeader = false;
        if (options.hasKey("hasAudioHeader")) {
            hasAudioHeader = options.getBoolean("hasAudioHeader");
        }

        isRecording = false;
        eventEmitter = reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);

        bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        if (options.hasKey("bufferSize")) {
            bufferSize = Math.max(bufferSize, options.getInt("bufferSize"));
        }

        // TODO recordingBufferSize or bufferSize ?
        // int recordingBufferSize = bufferSize * 3;
        long totalAudioLen = bufferSize;
        long totalDataLen = totalAudioLen + 36;
        wavHeaders = genWavHeader(totalAudioLen, totalDataLen);
    }

    @ReactMethod
    public void start() {
        isRecording = true;
        recorder.startRecording();

        Thread recordingThread = new Thread(new Runnable() {
            public void run() {
                try {
                    int bytesRead;
                    int count = 0;
                    String base64Data;
                    byte[] buffer = new byte[bufferSize];

                    while (isRecording) {
                        bytesRead = recorder.read(buffer, 0, buffer.length);

                        // skip first 2 buffers to eliminate "click sound"
                        if (bytesRead > 0 && ++count > 2) {
                            if (!hasAudioHeader) {
                                base64Data = Base64.encodeToString(buffer, Base64.NO_WRAP);
                                eventEmitter.emit("data", base64Data);
                                continue;
                            }

                            byte[] combined = new byte[wavHeaders.length + buffer.length];
                            System.arraycopy(wavHeaders, 0, combined, 0, wavHeaders.length);
                            System.arraycopy(buffer, 0, combined, wavHeaders.length, buffer.length);

                            base64Data = Base64.encodeToString(combined, Base64.NO_WRAP);

                            eventEmitter.emit("data", base64Data);
                        }
                    }
                    recorder.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        recordingThread.start();
    }

    @ReactMethod
    public void stop(Promise promise) {
        isRecording = false;
    }

    @ReactMethod
    public void startPlay() {
        player.play();
    }

    @ReactMethod
    public void addPlay(String audioBufferBase64) {
        // player.flush();
        byte[] audioBuffer = Base64.decode(audioBufferBase64, Base64.NO_WRAP);
        player.write(audioBuffer, 0, audioBuffer.length);
    }

    @ReactMethod
    public void stopPlay() {
        player.stop();
    }

    @ReactMethod
    public void loadPlayer() {
        // more info:
        // https://stackoverflow.com/questions/9413998/live-audio-recording-and-playing-in-android-and-thread-callback-handling
        // TODO recordingBufferSize or bufferSize ?
        // int recordingBufferSize = bufferSize * 3;
        player = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRateInHz, channelConfig, audioFormat,
                bufferSize, AudioTrack.MODE_STREAM);
        player.setPlaybackRate(sampleRateInHz);
    }

    @ReactMethod
    public void unloadPlayer() {
        player.release();
    }

    @ReactMethod
    public void loadRecorder() {
        // int recordingBufferSize = bufferSize * 3;
        recorder = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSize);
    }

    @ReactMethod
    public void unloadRecorder() {
        recorder.release();
    }

    @ReactMethod
    public void addListener(String eventName) {
        // Keep: Required for RN built in Event Emitter Calls.
    }

    @ReactMethod
    public void removeListeners(Integer count) {
        // Keep: Required for RN built in Event Emitter Calls.
    }

    private byte[] genWavHeader(long totalAudioLen, long totalDataLen) {
        long sampleRate = sampleRateInHz;
        int channels = channelConfig == AudioFormat.CHANNEL_IN_MONO ? 1 : 2;
        int bitsPerSample = audioFormat == AudioFormat.ENCODING_PCM_8BIT ? 8 : 16;
        long byteRate = sampleRate * channels * bitsPerSample / 8;
        int blockAlign = channels * bitsPerSample / 8;

        byte[] header = new byte[44];

        header[0] = 'R'; // RIFF chunk
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff); // how big is the rest of this file
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W'; // WAVE chunk
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1 for PCM
        header[21] = 0;
        header[22] = (byte) channels; // mono or stereo
        header[23] = 0;
        header[24] = (byte) (sampleRate & 0xff); // samples per second
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff); // bytes per second
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) blockAlign; // bytes in one sample, for all channels
        header[33] = 0;
        header[34] = (byte) bitsPerSample; // bits in a sample
        header[35] = 0;
        header[36] = 'd'; // beginning of the data chunk
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff); // how big is this data chunk
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        return header;
    }
}
