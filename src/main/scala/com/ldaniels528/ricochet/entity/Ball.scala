package com.ldaniels528.ricochet.entity

import java.awt.{Color, Graphics2D, Rectangle}

import com.ldaniels528.ricochet.RicochetAudioSamples._
import com.ldaniels528.ricochet.Direction._
import com.ldaniels528.ricochet.RicochetAudioPlayer._
import com.ldaniels528.ricochet.VirtualWorld
import com.ldaniels528.ricochet.entity.Ball._

/**
 * Represents a ball entity; capable of moving diagonally through the virtual world
 * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
 */
case class Ball(var x: Double, var y: Double, color: Color, var speed: Double) extends MovingEntity {
  private var direction = randomDirection(NE, NW)
  private val diameter = Radius * 2

  override def bounds = new Rectangle(x.toInt - Radius, y.toInt - Radius, diameter, diameter)

  override def die() {
    audioPlayer ! OutOfBoundsClip
    super.die()
  }

  override def handleCollision(world: VirtualWorld, entity: Entity, maxX: Int, maxY: Int) {
    entity match {
      case _: Paddle =>
        audioPlayer ! BounceClip
        direction = randomDirection(NW, NE)
      case _: Brick =>
        audioPlayer ! BreakClip
        direction = opposite(direction)
      case _ =>
    }

    if (speed < MaxSpeed) speed += .1
    update(0.20, maxX, maxY)
  }

  override def render(g: Graphics2D) {
    g.setColor(color)
    g.fillOval(x.toInt - Radius, y.toInt - Radius, diameter, diameter)
  }

  override def update(dt: Double, maxX: Int, maxY: Int) {
    val limitX = maxX - Radius
    val limitY = maxY - Radius
    val delta = dt * speed

    direction match {
      case NE =>
        x = Math.min(x + delta, limitX).toInt
        y = Math.max(y - delta, Radius).toInt
        if (x == limitX) direction = NW
        else if (y == Radius) direction = if (x > maxX / 2) SW else SE
      case SE =>
        x = Math.min(x + delta, limitX).toInt
        y = Math.min(y + delta, limitY).toInt
        if (x == limitX) direction = SW
        else if (y == limitY) die()
      case SW =>
        x = Math.max(x - delta, Radius).toInt
        y = Math.min(y + delta, limitY).toInt
        if (x == Radius) direction = SE
        else if (y == limitY) die()
      case NW =>
        x = Math.max(x - delta, Radius).toInt
        y = Math.max(y - delta, Radius).toInt
        if (x == Radius) direction = NE
        else if (y == Radius) direction = SW
    }
  }

}

/**
 * Ball Entity Singleton
 * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
 */
object Ball {
  val MaxSpeed = 40.0d
  val Radius = 7

}
