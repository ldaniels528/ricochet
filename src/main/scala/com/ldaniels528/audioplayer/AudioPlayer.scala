package com.ldaniels528.audioplayer

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.ldaniels528.audioplayer.AudioPlayer._

import scala.util.Try

/**
 * Audio Player
 * @author lawrence.daniels@gmail.com
 */
class AudioPlayer(audioSamples: Seq[AudioSample], parallelism: Int = 8) {
  private val system = ActorSystem("AudioPlayerSystem")

  // create the audio play-back actors
  private val audioPlayers = (1 to parallelism) map (n => system.actorOf(Props[AudioPlaybackActor], name = s"AudioPlayer$n"))
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

  /**
   * Audio-Clip Playback Actor
   * @author lawrence.daniels@gmail.com
   */
  class AudioPlaybackActor() extends Actor {
    override def receive = {
      case sample: AudioSample => Try(sample.play()); ()
      case sample => super.unhandled(sample)
    }
  }

}