package com.ldaniels528.audioplayer

import java.io._
import javax.sound.sampled._

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.ldaniels528.audioplayer.AudioPlayer._
import org.slf4j.LoggerFactory

/**
 * Audio Player
 * @author lawrence.daniels@gmail.com
 */
class AudioPlayer(audioSamples: Seq[AudioSample], parallelism: Int = 8) {
  private val system = ActorSystem("AudioPlayer")
  private implicit val ec = system.dispatcher

  // create the audio play-back actors
  val audioPlayers = (1 to parallelism) map (n => system.actorOf(Props[AudioPlaybackActor], name = s"player$n"))
  private var ticker = 0

  /**
   * Returns a reference to an actor for the audio play-back pool
   * @return an actor reference
   */
  def audioPlayer: ActorRef = {
    ticker += 1
    audioPlayers(ticker % audioPlayers.length)
  }

}

/**
 * Audio Player Singleton
 * @author lawrence.daniels@gmail.com
 */
object AudioPlayer {
  private lazy val logger = LoggerFactory.getLogger(getClass)

  /**
   * Audio-Clip Playback Actor
   * @author lawrence.daniels@gmail.com
   */
  class AudioPlaybackActor() extends Actor {
    override def receive = {
      case sample: AudioSample => sample.play()
      case sample => super.unhandled(sample)
    }
  }

  /**
   * Encapsulates the audio information necessary for play back
   * @author lawrence.daniels@gmail.com
   */
  case class AudioSampleData(format: AudioFormat, audioData: Array[Byte])

  /**
   * Represents an audio sample
   * @author lawrence.daniels@gmail.com
   */
  trait AudioSample {

    /**
     * Plays the audio represented by the given cached audio data object
     */
    def play()

    /**
     * Returns an input stream representing the data of the given resource path
     * @param path the given resource path
     * @return an [[InputStream]]
     */
    @throws[java.io.FileNotFoundException]
    protected def getResource(path: String): InputStream = {
      Option(getClass.getResource(path)) match {
        case Some(url) => url.openStream()
        case None =>
          throw new FileNotFoundException(s"Resource '$path' not found")
      }
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
      val baos = new ByteArrayOutputStream(bufferSize)

      // copy contents to the memory stream
      var count = 0
      do {
        count = audioInputStream.read(buffer, 0, buffer.length)
        if (count > 0) {
          baos.write(buffer, 0, count)
        }
      } while (count != -1)

      new AudioSampleData(audioFormat, baos.toByteArray)
    }

  }

  /**
   * Represents an audio sample
   * @author lawrence.daniels@gmail.com
   */
  class DiscreteAudioSample(path: String) extends AudioSample {
    val sample = loadAudioSample(getResource(path))

    /**
     * Plays the audio represented by the given cached audio data object
     */
    def play() {
      val format = sample.format
      val data = sample.audioData

      // Open a data line to play our type of sampled audio. Use SourceDataLine
      // for play and TargetDataLine for record.
      val info = new DataLine.Info(classOf[SourceDataLine], format)
      if (!AudioSystem.isLineSupported(info)) {
        logger.error(s"${getClass.getSimpleName} does not handle this type of audio.")
        return
      }

      // Create a SourceDataLine for play back (throws LineUnavailableException).
      val dataLine = AudioSystem.getLine(info).asInstanceOf[SourceDataLine]

      // The line acquires system resources (throws LineAvailableException).
      dataLine.open(format)

      // Allows the line to move data in and out to a port.
      dataLine.start()

      dataLine.write(data, 0, data.length)

      // Continues data line I/O until its buffer is drained.
      dataLine.drain()

      // Closes the data line, freeing any resources such as the audio device.
      dataLine.close()
    }

  }

  /**
   * Represents a continuous audio sample
   * @author lawrence.daniels@gmail.com
   */
  class ContinuousAudioSample(path: String, isAlive: => Boolean) extends AudioSample {
    val sample = loadAudioSample(getResource(path))

    /**
     * Plays the audio represented by the given cached audio data object
     */
    override def play() {
      val format = sample.format
      val data = sample.audioData

      // Open a data line to play our type of sampled audio. Use SourceDataLine
      // for play and TargetDataLine for record.
      val info = new DataLine.Info(classOf[SourceDataLine], format)
      if (!AudioSystem.isLineSupported(info)) {
        logger.error(s"${getClass.getSimpleName} does not handle this type of audio.")
        return
      }

      // Create a SourceDataLine for play back (throws LineUnavailableException).
      val dataLine = AudioSystem.getLine(info).asInstanceOf[SourceDataLine]

      // The line acquires system resources (throws LineAvailableException).
      dataLine.open(format)

      while (isAlive) {
        // Allows the line to move data in and out to a port.
        dataLine.start()

        dataLine.write(data, 0, data.length)

        // Continues data line I/O until its buffer is drained.
        dataLine.drain()
      }

      // Closes the data line, freeing any resources such as the audio device.
      dataLine.close()
    }

  }

}