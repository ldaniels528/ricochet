package com.ldaniels528.ricochet

import java.awt.{Color, Graphics2D}

import com.ldaniels528.ricochet.AudioManager._
import com.ldaniels528.ricochet.EntityLifeCycleState._
import com.ldaniels528.ricochet.Fonts._
import com.ldaniels528.ricochet.GraphicsExtensions._
import com.ldaniels528.ricochet.entity.{GenericBrick, Paddle}

/**
 * Represents the Game State Manager
 * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
 */
object GameManager {
  private val world = new VirtualWorld()
  private var level = 1
  private var gameState: GameState = GameStartUpState(level)
  var player_? : Option[Paddle] = None

  // listen to entity life-cycle events
  world.addListener { (state, entities) =>
    state match {
      case DEATH =>
        player_?.foreach { player =>
          player.score += entities.map {
            case b: GenericBrick => 10
            case _ => 0
          }.sum
        }

        // check for the "Game Over" event
        if (world.ballsRemaining == 0) {
          audioPlayer ! GameOverClip
          gameState = GameOverState
        }

        // check for the "Next Level" event
        if (world.bricksRemaining == 0) {
          level += 1
          gameState = GameStartUpState(level)
        }
      case _ =>
    }
  }

  /**
   * Allow all entities to update themselves; and perhaps move about ...
   * @param tick the given cycles/second
   * @param maxX the maximum X-value (eastern wall)
   * @param maxY the maximum Y-value (southern wall)
   */
  def update(g: Graphics2D, tick: Double, maxX: Int, maxY: Int) = {
    // render the game state
    gameState.update(g, tick, maxX, maxY)

    // render the score
    g.setColor(Color.WHITE)
    g.setFont(COURIER_BOLD_16)
    g.drawString(s"Score ${player_?.map(_.score).getOrElse(0)}", 850, 740)
  }

  /**
   * Represents the current game state
   * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
   */
  trait GameState {

    /**
     * Renders the current game state
     * @param g the given graphics context
     * @param tick the given tick (a single cycle/seconds)
     * @param maxX the maximum X-value (eastern wall)
     * @param maxY the maximum Y-value (southern wall)
     */
    def update(g: Graphics2D, tick: Double, maxX: Int, maxY: Int)

  }

  /**
   * Represents the "Game Start-up" state
   * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
   */
  case class GameStartUpState(level: Int) extends GameState {
    private var setup = false
    private var time = 0d

    override def update(g: Graphics2D, tick: Double, maxX: Int, maxY: Int): Unit = {
      // load the level
      if (!setup) {
        player_? = Option(GameLevels.setupLevel(world, level, maxX, maxY))
        setup = true
      }

      // render the world entities
      world.render(g)

      // after 3 seconds, start the game
      time += tick
      if (time >= 1) gameState = GameReadyState()
    }
  }

  /**
   * Represents the "Game Ready" state
   * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
   */
  case class GameReadyState() extends GameState {
    private var soundPlayed = false
    private var time = 0d

    override def update(g: Graphics2D, tick: Double, maxX: Int, maxY: Int): Unit = {
      // play the level start clip
      if (!soundPlayed && time >= 0.5) {
        audioPlayer ! GetReadyClip
        soundPlayed = true
      }

      // render the world entities
      world.render(g)

      // render the "Get Ready!" message
      g.setFont(COURIER_BOLD_64)
      g.drawString2D("Get Ready!", 300, 335, outlineColor = Color.WHITE, color = Color.BLUE)

      // after 3 seconds, start the game
      time += tick
      if (time >= 3) gameState = GamePlayState
    }
  }

  /**
   * Represents the "Game Play" state
   * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
   */
  object GamePlayState extends GameState {

    override def update(g: Graphics2D, tick: Double, maxX: Int, maxY: Int): Unit = {
      // update the world
      world.update(tick, maxX, maxY)

      // render the world entities
      world.render(g)
    }
  }

  /**
   * Represents the "Game Over" state
   * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
   */
  object GameOverState extends GameState {

    override def update(g: Graphics2D, tick: Double, maxX: Int, maxY: Int): Unit = {
      // render the world entities
      world.render(g)

      // render the "Game Over!" message
      g.setFont(COURIER_BOLD_64)
      g.drawString2D("Game Over!", 300, 335, outlineColor = Color.YELLOW, color = Color.RED)
    }
  }

}
