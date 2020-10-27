package ui

import model.{Cell, Rule, Neighbourhood, World}
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color

class CellCanvas[T <: Cell[T]](
  var world: World[T],
  val cellSize: Int,
  val map: Map[Cell[T], Color]
) extends Canvas {

  private val graphics = graphicsContext2D

  width = cellSize * world.rows
  height = cellSize * world.cols

  plotCells()

  def update(rule: Rule[T], nb: Neighbourhood): Unit = {
    clear()
    world.update(rule, nb)
    plotCells()
  }

  def plotCells(): Unit = {
    for {
      i <- world.current.indices
      j <- world.current.head.indices
    } {
      graphics.fill = map(world.current(i)(j))
      graphics.fillRect(i * cellSize, j * cellSize, cellSize, cellSize)
    }
  }

  def clear(): Unit = {
    graphics.clearRect(0, 0, width.value, height.value)
  }

}
