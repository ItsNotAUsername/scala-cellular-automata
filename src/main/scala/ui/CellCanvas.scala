package ui

import model.{Cell, Neighbourhood, Rule, World}

import scala.collection.mutable.{Seq => MutSeq}
import scalafx.Includes._
import scalafx.beans.property.{BooleanProperty, ObjectProperty}
import scalafx.scene.canvas.Canvas
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color

class CellCanvas[T <: Cell[T]](
  val rows: Int,
  val cols: Int,
  val cellSize: Int,
  val cellToColor: Map[Cell[T], ObjectProperty[Color]],
  val defaultCell: Cell[T]
) extends Canvas {

  private val world: World[T] = new World(MutSeq.fill(rows, cols)(defaultCell))

  private val graphics = graphicsContext2D

  width = cellSize * rows
  height = cellSize * cols

  plotCells()

  val isEditable: BooleanProperty = BooleanProperty(true)

  val currentCell: ObjectProperty[Cell[T]] = ObjectProperty(defaultCell)

  def update(rule: Rule[T], nb: Neighbourhood): Unit = {
    world.update(rule, nb)
    plotCells()
  }

  private def plotCell(i: Int, j: Int): Unit = {
    graphics.fill = cellToColor(world.current(i)(j)).value
    graphics.fillRect(i * cellSize, j * cellSize, cellSize, cellSize)
  }

  def plotCells(): Unit = {
    for {
      i <- world.current.indices
      j <- world.current.head.indices
    } plotCell(i, j)
  }

  def clear(): Unit = {
    for {
      i <- world.current.indices
      j <- world.current.head.indices
    } {
      world.current(i)(j) = defaultCell
      plotCell(i, j)
    }
  }

  def random(): Unit = {
    val states = cellToColor.keys.toSeq
    for {
      i <- world.current.indices
      j <- world.current.head.indices
    } {
      world.current(i)(j) = World.randomCell(states)
      plotCell(i, j)
    }
  }

  handleEvent(MouseEvent.MouseReleased) {
    event: MouseEvent => {
      if (isEditable.value) {
        val x = ((event.x - (event.x % cellSize)) / cellSize).toInt
        val y = ((event.y - (event.y % cellSize)) / cellSize).toInt
        if (world.current.indices.contains(x) && world.current.head.indices.contains(y)) {
          world.current(x)(y) = currentCell.value
          plotCell(x, y)
        }
      }
    }
  }

  handleEvent(MouseEvent.MouseDragged) {
    event: MouseEvent => {
      if (isEditable.value) {
        val x = ((event.x - (event.x % cellSize)) / cellSize).toInt
        val y = ((event.y - (event.y % cellSize)) / cellSize).toInt
        if (world.current.indices.contains(x) && world.current.head.indices.contains(y)) {
          world.current(x)(y) = currentCell.value
          plotCell(x, y)
        }
      }
    }
  }

}
