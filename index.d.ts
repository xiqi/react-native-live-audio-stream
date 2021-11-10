declare module "react-native-live-audio-stream" {
  export interface IAudioRecord {
    init: (options: Options) => void
    start: () => void
    stop: () => Promise<string>
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
