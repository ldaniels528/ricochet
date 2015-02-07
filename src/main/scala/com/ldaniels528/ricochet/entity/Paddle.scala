package com.ldaniels528.ricochet.entity

import java.awt.{Color, Graphics2D, Rectangle}

import com.ldaniels528.ricochet.AudioManager._
import com.ldaniels528.ricochet.VirtualWorld
import com.ldaniels528.ricochet.entity.Paddle._

/**
 * Represents a paddle entity; capable of moving horizontally through the virtual world
 * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
 */
case class Paddle(var x: Double, var y: Double, color: Color) extends MovingEntity {
  var targetX: Option[Int] = None
  var score = 0

  override def bounds: Rectangle = new Rectangle(x.toInt - Width / 2, y.toInt, Width, Height)

  /**
   * Updates the entity; allowing it to move about
   * @param dt the given delta time factor
   * @param maxX the given eastern wall
   * @param maxY the given southern wall
   */
  override def update(dt: Double, maxX: Int, maxY: Int) = {
    targetX.foreach { pos =>
      x = Math.min(Math.max(pos, 0), maxX)
      targetX = None
    }
  }

  override def handleCollision(world: VirtualWorld, entity: Entity, maxX: Int, maxY: Int) = {
    audioPlayer ! BounceClip
    score += 20
  }

  override def render(g: Graphics2D) {
    g.setColor(color)
    g.fill3DRect(x.toInt - Width / 2, y.toInt, Width, Height, true)
  }

}

/**
 * Paddle Entity Singleton
 * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
 */
object Paddle {
  val Width = 100
  val Height = 10

}
