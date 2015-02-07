package com.ldaniels528.ricochet.entity

import java.util.Random

import com.ldaniels528.ricochet.Direction
import com.ldaniels528.ricochet.Direction._

/**
 * Represents a moving entity within the virtual world
 * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
 */
trait MovingEntity extends Entity {
  private val random = new Random()

  /**
   * Returns the opposite of the given direction
   * @param direction the given [[Direction]]
   * @return the opposite direction
   */
  protected def opposite(direction: Direction): Direction = {
    direction match {
      case NE => SW
      case NW => SE
      case SE => NW
      case SW => NE
    }
  }

  /**
   * Returns a random direction (limited to the given sequence of directions)
   * @return a random direction
   */
  protected def randomDirection(directions: Direction*): Direction = {
    directions(random.nextInt(directions.length))
  }

}
