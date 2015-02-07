package com.ldaniels528.ricochet

import java.awt.Graphics2D

import com.ldaniels528.ricochet.entity._

import scala.collection.mutable.ListBuffer

/**
 * Represents the virtual world
 * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
 */
class VirtualWorld() {
  private val entities = ListBuffer[Entity]()
  private var lifeCycleObservers = List[(EntityLifeCycleState, List[Entity]) => Unit]()
  private var age = 0d

  /**
   * Adds an entity to the virtual world
   * @param entity a given [[Entity]]
   */
  def +=(entity: Entity) {
    entities += entity
    ()
  }

  def addListener(listener: (EntityLifeCycleState, List[Entity]) => Unit) {
    lifeCycleObservers = listener :: lifeCycleObservers
  }

  /**
   * Returns the count of remaining balls
   * @return the count of remaining balls
   */
  def ballsRemaining: Int = {
    entities.foldLeft[Int](0)((total, entity) => total + (if (entity.isInstanceOf[Ball]) 1 else 0))
  }

  /**
   * Returns the count of remaining bricks
   * @return the count of remaining bricks
   */
  def bricksRemaining: Int = {
    entities.foldLeft[Int](0)((total, entity) =>
      total + (if (entity.isInstanceOf[GenericBrick]) 1 else 0))
  }

  /**
   * Allow all entities to update themselves; and perhaps move about ...
   * @param t the given delta time factor
   * @param maxX the maximum X-value (eastern wall)
   * @param maxY the maximum Y-value (southern wall)
   */
  def update(t: Double, maxX: Int, maxY: Int) {
    age += t
    val dt = if (t < 0.2) 0.2 else t
    entities.foreach(_.update(dt, maxX, maxY))
    detectCollisions(maxX, maxY)
  }

  /**
   * Renders all entities
   * @param g the given off-screen context
   */
  def render(g: Graphics2D) = entities.foreach(_.render(g))

  /**
   * Performs collision detection for all moving entities
   * @param maxX the maximum X-value (eastern wall)
   * @param maxY the maximum Y-value (southern wall)
   */
  private def detectCollisions(maxX: Int, maxY: Int) {
    var deadEntities: List[Entity] = Nil

    entities.filter(_.isInstanceOf[MovingEntity]).foreach { me =>
      entities.foreach { ee =>
        if (me != ee && me.bounds.intersects(ee.bounds)) {
          me.handleCollision(this, ee, maxX, maxY)
          ee.handleCollision(this, me, maxX, maxY)
          if (!ee.isAlive) deadEntities = ee :: deadEntities
        }
      }
      if (!me.isAlive) deadEntities = me :: deadEntities
    }

    // remove dead entities
    if (deadEntities.nonEmpty) {
      entities --= deadEntities
      lifeCycleObservers.foreach(_(EntityLifeCycleState.DEATH, deadEntities))
    }
  }

}
