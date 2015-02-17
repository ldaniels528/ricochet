package com.ldaniels528.ricochet

import java.io.{File, FileNotFoundException, InputStream}

import com.ldaniels528.audioplayer.{AudioPlayer, DiscreteAudioSample}
import com.ldaniels528.ricochet.RicochetAudioSamples._

/**
 * Ricochet Audio Player Singleton
 * @author lawrence.daniels@gmail.com
 */
object RicochetAudioPlayer extends AudioPlayer(AudioClips, parallelism = 16)

/**
 * Ricochet Audio Samples Singleton
 * @author lawrence.daniels@gmail.com
 */
object RicochetAudioSamples {

  val AudioClips = Seq(BounceClip, BreakClip, GameOverClip, GetReadyClip, LevelChangeClip, OutOfBoundsClip)

  case object BounceClip extends DiscreteAudioSample(getResourceAsFile("/audio/bounce.wav"))

  case object BreakClip extends DiscreteAudioSample(getResourceAsFile("/audio/break.wav"))

  case object GameOverClip extends DiscreteAudioSample(getResourceAsFile("/audio/gameOver.wav"))

  case object GetReadyClip extends DiscreteAudioSample(getResourceAsFile("/audio/getReady.wav"))

  case object LevelChangeClip extends DiscreteAudioSample(getResourceAsFile("/audio/levelChange.wav"))

  case object OutOfBoundsClip extends DiscreteAudioSample(getResourceAsFile("/audio/outOfBounds.wav"))

  /**
   * Returns an input stream representing the data of the given resource path
   * @param path the given resource path
   * @return an [[InputStream]]
   */
  @throws[java.io.FileNotFoundException]
  private def getResourceAsFile(path: String): File = {
    Option(getClass.getResource(path)) match {
      case Some(url) => new File(url.getFile)
      case None =>
        throw new FileNotFoundException(s"Resource '$path' not found")
    }
  }

}
