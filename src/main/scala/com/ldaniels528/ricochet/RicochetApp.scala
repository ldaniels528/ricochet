package com.ldaniels528.ricochet

import java.awt.event.{MouseEvent, MouseMotionListener}
import java.awt.{Cursor, Color, Dimension, Graphics2D}
import javax.swing.{JFrame, JPanel}

/**
 * Ricochet Application
 * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
 */
class RicochetApp() extends JFrame("Ricochet") {
  super.setContentPane(new ViewPanel())
  super.pack()
  super.setVisible(true)
  super.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
  super.addMouseMotionListener(new MouseMotionListener {
    override def mouseMoved(e: MouseEvent) {
      val pt = e.getLocationOnScreen
      GameManager.player_? foreach { player =>
        player.targetX = Some(pt.x)
      }
    }

    override def mouseDragged(e: MouseEvent) = ()
  })

  // graphics-related values
  private val contentPane = getContentPane
  private val buffer = createImage(contentPane.getWidth, contentPane.getHeight)
  private val offScreen = buffer.getGraphics.asInstanceOf[Graphics2D]
  private val theScreen = contentPane.getGraphics.asInstanceOf[Graphics2D]

  // game-related values
  private val frameCounter = new FrameCounter()
  private var alive = true
  private var hz: Double = 0

  /**
   * Called to end the execution of the main loop
   */
  def die(): Unit = alive = false

  /**
   * Main loop
   */
  def run() {
    while (alive) {
      // capture the start time of the frame
      val startTime = System.currentTimeMillis()

      // clear the off-screen buffer
      offScreen.setColor(Color.BLACK)
      offScreen.fillRect(0, 0, contentPane.getWidth, contentPane.getHeight)

      // update the game manager
      GameManager.update(offScreen, hz, contentPane.getWidth, contentPane.getHeight)

      // compute the delta time factor and frames/second
      frameCounter.render(offScreen)

      // update the display
      theScreen.drawImage(buffer, 0, 0, this)

      // capture the cycles/second (in Hz)
      hz = (System.currentTimeMillis() - startTime).toDouble / 1000d
    }
  }

  /**
   * Game View Panel
   * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
   */
  class ViewPanel() extends JPanel(false) {
    super.setPreferredSize(new Dimension(1024, 768))
  }

}

/**
 * Radius Application Singleton
 * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
 */
object RicochetApp {

  /**
   * Application entry-point
   * @param args the given command line arguments
   */
  def main(args: Array[String]) = new RicochetApp().run()

}
