package com.ldaniels528.ricochet

import com.ldaniels528.audioplayer.AudioPlayer
import com.ldaniels528.ricochet.AudioSamples._
import com.ldaniels528.audioplayer.AudioPlayer.{DiscreteAudioSample, AudioSample}

/**
 * Ricochet Audio Player Singleton
 * @author lawrence.daniels@gmail.com
 */
object RicochetAudioPlayer extends AudioPlayer(AudioClips, parallelism = 16)

/**
 * Audio Samples Singleton
 * @author lawrence.daniels@gmail.com
 */
object AudioSamples {

  val AudioClips = Seq(BounceClip, BreakClip, GameOverClip, GetReadyClip, LevelChangeClip, OutOfBoundsClip)

  case object BounceClip extends DiscreteAudioSample("/audio/bounce.wav")

  case object BreakClip extends DiscreteAudioSample("/audio/break.wav")

  case object GameOverClip extends DiscreteAudioSample("/audio/gameOver.wav")

  case object GetReadyClip extends DiscreteAudioSample("/audio/getReady.wav")

  case object LevelChangeClip extends DiscreteAudioSample("/audio/levelChange.wav")

  case object OutOfBoundsClip extends DiscreteAudioSample("/audio/outOfBounds.wav")

}
