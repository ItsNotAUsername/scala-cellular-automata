package model

import scala.annotation.tailrec

class Board[T <: Cell[T]] private(val cells: Vector[Vector[T]]) {

  val rows: Int = cells.length

  val cols: Int = cells.head.length

  def apply(x: Int, y: Int): T = cellByIndex(x, y)

  def next(rule: Rule[T], nb: Neighbourhood): Board[T] = {
    val newCells = Vector.tabulate(rows, cols)(
      (y, x) => {
        val neighbours = nb.neighbours(x, y).map {
          case (i, j) => cellByIndex(i, j)
        }
        rule.transform(cells(y)(x), neighbours)
      }
    )
    new Board[T](newCells)
  }

  def copy(x: Int, y: Int, newCell: T): Board[T] = {
    val (xb, yb) = bounds(x, y)
    val newCells = cells.updated(yb, cells(yb).updated(xb, newCell))
    new Board[T](newCells)
  }

  private def cellByIndex(x: Int, y: Int): T = {
    val (xb, yb) = bounds(x, y)
    cells(yb)(xb)
  }

  @tailrec
  private def bounds(x: Int, y: Int): (Int, Int) =
    if (x >= cols)
      bounds(x - cols, y)
    else if (x < 0)
      bounds(x + cols, y)
    else if (y >= rows)
      bounds(x, y - rows)
    else if (y < 0)
      bounds(x, y + rows)
    else
      (x, y)

}

object Board {

  def fill[T <: Cell[T]](rows: Int, cols: Int, cell: T): Board[T] = {
    val cells = Vector.fill(rows, cols)(cell)
    new Board[T](cells)
  }

  def random[T <: Cell[T]](rows: Int, cols: Int, states: Seq[T]): Board[T] = {
    val random = scala.util.Random
    val cells = Vector.fill(rows, cols)(states(random.between(0, states.size)))
    new Board(cells)
  }

}