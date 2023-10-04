declare module "react-native-live-audio-stream" {
  export interface IAudioRecord {
    init: (options: Options) => void
    /**
     * make sure to call `init` before this,
     * starts recording.
     */
    start: () => void
    /**
     * stops recording.
     */
    stop: () => void
    /**
     * has to be called before playing audio.
     * NOTE: this DOES NOT WORK on iOS
     */
    loadPlayer: () => void
    /**
     * unloads the player resources, can be called on unmount.
     * NOTE: this DOES NOT WORK on iOS
     */
    unloadPlayer: () => void
    /**
     * has to be called before playing recorder.
     * NOTE: this DOES NOT WORK on iOS
     */
    loadRecorder: () => void
    /**
     * unloads the recorder resources, can be called on unmount.
     * NOTE: this DOES NOT WORK on iOS
     */
    unloadRecorder: () => void
    /**
     * make sure to call `init` before this
     * NOTE: this DOES NOT WORK on iOS
     */
    startPlay: () => void;
    /**
     * NOTE: this DOES NOT WORK on iOS
     * @param audioBufferBase64 same data that you got on `data` event
     */
    addPlay: (audioBufferBase64: string) => void;
    /**
     * NOTE: this DOES NOT WORK on iOS
     */
    stopPlay: () => void;
    /**
     * 
     * @param event
     * @param callback provides data as base64 header-less wave audio
     */
    on: (event: "data", callback: (data: string) => void) => void
  }

  export interface Options {
    sampleRate: number
    /**
     * - `1 | 2`
     */
    channels: number
    /**
     * - `8 | 16`
     */
    bitsPerSample: number
    /**
     * - `6`
     */
    audioSource?: number
    bufferSize?: number
    /**
     * you probably want this to be true if you want to play it elsewhere.
     */
    hasAudioHeader?: boolean
  }

  const AudioRecord: IAudioRecord

  export default AudioRecord;
}
