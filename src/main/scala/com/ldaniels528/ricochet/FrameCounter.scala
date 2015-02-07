package com.ldaniels528.ricochet

import java.awt.{Color, Graphics2D}

import com.ldaniels528.ricochet.Fonts._

/**
 * Frame Counter
 * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
 */
class FrameCounter() {
  private var lastFrameCheck = System.currentTimeMillis()
  private var frames = 0
  private var fps = 0.0d

  /**
   * Computes the frames rendered per second
   */
  def render(g: Graphics2D) {
    val delta = (System.currentTimeMillis() - lastFrameCheck).toDouble / 1000d
    if (delta >= 1) {
      fps = frames.toDouble / delta
      lastFrameCheck = System.currentTimeMillis()
      frames = 0
    }

    // compute the color for the text
    val color = fps match {
      case n if n < 30 => Color.RED
      case n if n < 60 => Color.YELLOW
      case _ => Color.WHITE
    }

    g.setColor(color)
    g.setFont(COURIER_BOLD_16)
    g.drawString(f"$fps%.1f", 0, 740)
    frames += 1
  }

}
