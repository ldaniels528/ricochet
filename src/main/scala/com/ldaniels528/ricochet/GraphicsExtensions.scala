package com.ldaniels528.ricochet

import java.awt.{Color, Graphics2D}

/**
 * Useful Graphics Extensions
 * @author "Lawrence Daniels" <lawrence.daniels@gmail.com>
 */
object GraphicsExtensions {

  /**
   * Graphics2D extensions
   * @param g the given graphics context
   */
  implicit class Graphics2DExtension(val g: Graphics2D) {

    def drawString2D(s: String, x: Int, y: Int, outlineColor: Color, color: Color): Unit = {
      g.setColor(outlineColor)
      g.drawString(s, x, y)
      g.setColor(color)
      g.drawString(s, x + 1, y + 1)
    }

  }

}
