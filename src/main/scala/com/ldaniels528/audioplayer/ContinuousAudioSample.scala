package com.ldaniels528.audioplayer

import java.io.{File, FileInputStream}

/**
 * Represents a continuous audio sample
 * @author lawrence.daniels@gmail.com
 */
class ContinuousAudioSample(file: File, isAlive: => Boolean) extends AudioSample {
  private val sample = loadAudioSample(new FileInputStream(file))

  /**
   * Plays the audio represented by the given audio sample
   */
  override def play() {
    import sample.{audioData, format}

    // play back the audio clip
    getSourceDataLine(format) foreach { dataLine =>
      dataLine.open(format)

      try {
        while (isAlive) {
          // allows the line to move data in and out to a port
          dataLine.start()
          dataLine.write(audioData, 0, audioData.length)

          // block until the buffer is drained
          dataLine.drain()
        }

      } finally {
        dataLine.close()
      }
    }
    ()
  }

}
