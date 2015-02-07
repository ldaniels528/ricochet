package com.ldaniels528.ricochet.entity

import java.awt.Color

/**
 * Represents a semi-stationary brick
 * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
 */
case class Brick(x: Double, var y: Double, width: Int, height: Int, color: Color)
  extends GenericBrick(width, height, color) {

  override def update(tick: Double, maxX: Int, maxY: Int) = {
    y += tick / 50d
  }

}