package com.ldaniels528.ricochet

import java.io._
import javax.sound.sampled._

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.concurrent.Future

/**
 * Audio Manager
 * @author lawrence.daniels@gmail.com
 */
object AudioManager {
  private val system = ActorSystem("AudioManager")
  private val audioSampleCache = loadSamples()
  private implicit val ec = system.dispatcher

  // create the audio play-back actors
  val audioPlayers = (1 to 16) map (n => system.actorOf(Props[AudioPlaybackActor], name = s"audioPlayer$n"))
  var ticker = 0

  // allows background audio playback to be turned on/off
  var playbackOn = true

  /**
   * Returns a reference to an actor for the audio play-back pool
   * @return an actor reference
   */
  def audioPlayer: ActorRef = {
    ticker += 1
    audioPlayers(ticker % audioPlayers.length)
  }

  /**
   * Creates a cached audio data object from the underlying sound data found
   * within the given input stream.
   * @param is the given [[InputStream]]
   * @return a [[FxAudioSample]]
   */
  @throws[IOException]
  @throws[UnsupportedAudioFileException]
  private def loadAudioSample(is: InputStream): FxAudioSample = {
    // create a buffered stream
    val bis = new BufferedInputStream(is)

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

    new FxAudioSample(audioFormat, baos.toByteArray)
  }

  /**
   * Plays the audio represented by the given cached audcio data object
   * @param sample the given [[FxAudioSample]]
   */
  private def playSample(sample: FxAudioSample) {
    val format = sample.format
    val data = sample.audioData

    // Open a data line to play our type of sampled audio. Use SourceDataLine
    // for play and TargetDataLine for record.
    val info = new DataLine.Info(classOf[SourceDataLine], format)
    if (!AudioSystem.isLineSupported(info)) {
      System.out.println("AudioPlayer.playAudioStream does not handle this type of audio.")
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

  /**
   * Plays the audio represented by the given cached audcio data object
   * @param sample the given [[FxAudioSample]]
   */
  private def playContinuousSample(sample: FxAudioSample, isAlive: => Boolean) {
    val format = sample.format
    val data = sample.audioData

    // Open a data line to play our type of sampled audio. Use SourceDataLine
    // for play and TargetDataLine for record.
    val info = new DataLine.Info(classOf[SourceDataLine], format)
    if (!AudioSystem.isLineSupported(info)) {
      System.out.println("AudioPlayer.playAudioStream does not handle this type of audio.")
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


  /**
   * Loads all audio-samples
   */
  private def loadSamples(): Map[AudioKey, FxAudioSample] = {
    Map(Seq(BounceClip, BreakClip, GameOverClip, GetReadyClip, LevelChangeClip, OutOfBoundsClip) map { audioKey =>
      (audioKey, loadSample(audioKey.path))
    }: _*)
  }

  private def loadSample(resource: String) = {
    loadAudioSample(getResource(resource))
  }

  /**
   * Returns an input stream representing the data of the given resource path
   * @param path the given resource path
   * @return an [[InputStream]]
   */
  @throws[java.io.FileNotFoundException]
  private def getResource(path: String): InputStream = {
    Option(getClass.getResource(path)) match {
      case Some(url) => url.openStream()
      case None =>
        throw new FileNotFoundException(s"Resource '$path' not found")
    }
  }

  /**
   * Audio-Clip Playback Actor
   * @author lawrence.daniels@gmail.com
   */
  class AudioPlaybackActor() extends Actor {
    def receive = {
      case audioKey: ContinuousAudioKey =>
        audioSampleCache.get(audioKey) foreach { sample =>
          Future {
            playContinuousSample(sample, isAlive = playbackOn)
          }
        }
      case audioKey: AudioKey =>
        audioSampleCache.get(audioKey) foreach { sample =>
          playSample(sample)
        }
      case x => super.unhandled(x)
    }
  }

  /**
   * Encapsulates the audio information necessary for play back
   * @author lawrence.daniels@gmail.com
   */
  case class FxAudioSample(format: AudioFormat, audioData: Array[Byte])

  /**
   * Audio-Clip messages
   */
  class AudioKey(val path: String)

  class ContinuousAudioKey(path: String) extends AudioKey(path)

  case object BounceClip extends AudioKey("/audio/bounce.wav")

  case object BreakClip extends AudioKey("/audio/break.wav")

  case object GameOverClip extends AudioKey("/audio/gameOver.wav")

  case object GetReadyClip extends AudioKey("/audio/getReady.wav")

  case object LevelChangeClip extends AudioKey("/audio/levelChange.wav")

  case object OutOfBoundsClip extends AudioKey("/audio/outOfBounds.wav")

}
