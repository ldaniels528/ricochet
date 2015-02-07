package com.ldaniels528.ricochet.entity

import java.awt.{Color, Graphics2D, Rectangle}

import com.ldaniels528.ricochet.VirtualWorld

/**
 * Represents a generic brick
 * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
 */
abstract class GenericBrick(width: Int, height: Int, color: Color) extends Entity {

  override def bounds = new Rectangle(x.toInt, y.toInt, width, height)

  override def handleCollision(world: VirtualWorld, entity: Entity, maxX: Int, maxY: Int) = die()

  override def render(g: Graphics2D) {
    g.setColor(color)
    g.fill3DRect(x.toInt, y.toInt, width, height, true)
  }

}
