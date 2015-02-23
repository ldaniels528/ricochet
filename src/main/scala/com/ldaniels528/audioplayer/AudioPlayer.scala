package com.ldaniels528.audioplayer

import akka.actor.{Actor, ActorSystem, Props}
import akka.routing.RoundRobinPool
import com.ldaniels528.audioplayer.AudioPlayer._

import scala.util.Try

/**
 * Audio Player
 * @author lawrence.daniels@gmail.com
 */
class AudioPlayer(audioSamples: Seq[AudioSample], parallelism: Int = 8) {
  private val system = ActorSystem("AudioPlayerSystem")

  // create the audio play-back actors
  val audioPlayer = system.actorOf(Props[AudioPlaybackActor].
    withRouter(RoundRobinPool(nrOfInstances = parallelism)), name = "audioPlayerActor")

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