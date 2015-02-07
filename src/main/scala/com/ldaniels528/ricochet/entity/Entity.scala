package com.ldaniels528.ricochet.entity

import java.awt.{Graphics2D, Rectangle}

import com.ldaniels528.ricochet.VirtualWorld

/**
 * Represents an entity within the virtual world
 * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
 */
trait Entity {
  private var alive = true

  /**
   * Returns the entity's X-axis position
   * @return the entity's X-axis position
   */
  def x: Double

  /**
   * Returns the entity's Y-axis position
   * @return the entity's Y-axis position
   */
  def y: Double

  /**
   * Returns the entities current bounds within the virtual world
   * @return the bounds within the virtual world
   */
  def bounds: Rectangle

  /**
   * Causes the entity to die
   */
  def die(): Unit = alive = false

  /**
   * Indicates whether the entity is still alive
   * @return true, if the entity is alive
   */
  def isAlive: Boolean = alive

  /**
   * Called when a collision occurs with another entity
   * @param entity the other entity
   * @param maxX the X-axis value of the eastern wall
   * @param maxY the Y-axis value of the southern wall
   */
  def handleCollision(world: VirtualWorld, entity: Entity, maxX: Int, maxY: Int)

  /**
   * Renders the entity
   * @param g the given graphics context
   */
  def render(g: Graphics2D)

  /**
   * Updates the entity; allowing it to move about
   * @param tick the given delta time factor
   * @param maxX the X-axis value of the eastern wall
   * @param maxY the Y-axis value of the southern wall
   */
  def update(tick: Double, maxX: Int, maxY: Int)

}
