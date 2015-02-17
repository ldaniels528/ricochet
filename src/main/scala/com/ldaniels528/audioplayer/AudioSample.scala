package com.ldaniels528.audioplayer

import java.io._
import javax.sound.sampled._

import com.ldaniels528.audioplayer.AudioSample.AudioSampleData
import org.slf4j.LoggerFactory

/**
 * Represents an audio sample
 * @author lawrence.daniels@gmail.com
 */
trait AudioSample {
  private lazy val logger = LoggerFactory.getLogger(getClass)

  /**
   * Plays the audio represented by the given audio sample
   */
  def play()

  /**
   * Returns a play back capable source data line for the given audio format
   * @param format the given [[AudioFormat]]
   * @return an option of a [[SourceDataLine]]
   */
  @throws[LineUnavailableException]
  protected def getSourceDataLine(format: AudioFormat): Option[SourceDataLine] = {
    // open a data line to play our type of sampled audio. Use SourceDataLine
    // for play and TargetDataLine for record.
    val info = new DataLine.Info(classOf[SourceDataLine], format)
    if (!AudioSystem.isLineSupported(info)) {
      logger.warn(s"Incompatible audio format - $format")
      None
    }
    else Option(AudioSystem.getLine(info)) map (_.asInstanceOf[SourceDataLine])
  }

  /**
   * Creates a cached audio data object from the underlying sound data found
   * within the given input stream.
   * @param in the given [[InputStream]]
   * @return a [[AudioSampleData]]
   */
  @throws[IOException]
  @throws[UnsupportedAudioFileException]
  protected def loadAudioSample(in: InputStream): AudioSampleData = {
    // create a buffered stream
    val bis = new BufferedInputStream(in)

    // create an audio input stream
    val audioInputStream = AudioSystem.getAudioInputStream(bis)

    // get the audio format
    val audioFormat = audioInputStream.getFormat

    // Create a buffer for moving data from the audio stream to the line.
    val bufferSize = (audioFormat.getSampleRate * audioFormat.getFrameSize).toInt
    val buffer = new Array[Byte](bufferSize)

    // create memory stream
    val out = new ByteArrayOutputStream(bufferSize)

    // copy contents to the memory stream
    var count = 0
    do {
      count = audioInputStream.read(buffer, 0, buffer.length)
      if (count > 0) {
        out.write(buffer, 0, count)
      }
    } while (count != -1)

    new AudioSampleData(audioFormat, out.toByteArray)
  }

}

/**
 * Audio Sample Singleton
 * @author lawrence.daniels@gmail.com
 */
object AudioSample {

  /**
   * Encapsulates the audio information necessary for play back
   * @author lawrence.daniels@gmail.com
   */
  case class AudioSampleData(format: AudioFormat, audioData: Array[Byte])

}