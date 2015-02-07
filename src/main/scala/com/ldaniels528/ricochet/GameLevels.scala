package com.ldaniels528.ricochet

import java.awt.Color

import com.ldaniels528.ricochet.entity.{Ball, Brick, HatchingBrick, Paddle}

import scala.util.Random

/**
 * Game Level Manager
 * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
 */
object GameLevels {
  private val random = new Random()

  /**
   * Sets up the game level
   * @param world the given virtual world
   * @param level the given game level
   * @param maxX the maximum X-value (eastern wall)
   * @param maxY the maximum Y-value (southern wall)
   * @return the player paddle
   */
  def setupLevel(world: VirtualWorld, level: Int, maxX: Int, maxY: Int): Paddle = {
    level match {
      case 1 => setupLevel1(world, maxX, maxY)
      case _ => setupLevel1(world, maxX, maxY)
    }
  }

  /**
   * Sets up the game level
   * @param world the given virtual world
   * @param maxX the maximum X-value (eastern wall)
   * @param maxY the maximum Y-value (southern wall)
   * @return the player paddle
   */
  def setupLevel1(world: VirtualWorld, maxX: Int, maxY: Int): Paddle = {
    // add some balls
    world += Ball(x = 492, y = 690, Color.WHITE, speed = 12)

    // add the player paddle
    val paddle = Paddle(x = 512, y = 700, Color.WHITE)
    world += paddle

    // add multiple rows of colored bricks
    val colors = Seq(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW)
    var specialBricks = 5
    for {
      adj <- Seq(0, 32 * 3)
      yPos <- 0 to 32 by 16
      xPos <- 0 to 31
    } {
      val brick = {
        random.nextInt(15) match {
          case 3 if specialBricks > 0 =>
            specialBricks -= 1
            HatchingBrick(x = 32 * xPos, y = adj + yPos, width = 31, height = 15)
          case _ =>
            Brick(x = 32 * xPos, y = adj + yPos, width = 31, height = 15, colors(xPos % colors.length))
        }
      }

      world += brick
    }

    paddle
  }

}
