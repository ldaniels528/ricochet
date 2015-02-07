package com.ldaniels528.ricochet.entity

import java.awt.Color

import com.ldaniels528.ricochet.VirtualWorld

/**
 * Represents a ball-producing brick
 * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
 */
case class HatchingBrick(x: Double, var y: Double, width: Int, height: Int)
  extends GenericBrick(width, height, Color.GRAY) {

  override def handleCollision(world: VirtualWorld, entity: Entity, maxX: Int, maxY: Int) = {
    world += Ball(x, y, Color.CYAN, speed = 15)
    super.handleCollision(world, entity, maxX, maxY)
  }

  override def update(tick: Double, maxX: Int, maxY: Int) = {
    y += tick / 50d
  }

}
