declare module "react-native-live-audio-stream" {
  export interface IAudioRecord {
    init: (options: Options) => void
    /**
     * make sure to call `init` before this
     */
    start: () => void
    stop: () => void
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
  }

  const AudioRecord: IAudioRecord

  export default AudioRecord;
}
