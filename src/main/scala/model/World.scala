package model

import scala.collection.mutable.{Seq => MutSeq}

class World[T <: Cell[T]](private var cells: MutSeq[MutSeq[Cell[T]]]) {

  val rows: Int = cells.length
  val cols: Int = cells.head.length

  private var swapSeq: MutSeq[MutSeq[Cell[T]]] = MutSeq.tabulate(rows, cols) {
    (i, j) => cells(i)(j)
  }

  private def swap(): Unit = {
    val a = cells
    cells = swapSeq
    swapSeq = a
  }

  def update(rule: Rule[T], nb: Neighbourhood): Unit = {
    for {
      i <- swapSeq.indices
      j <- swapSeq.head.indices
    } swapSeq(i)(j) = rule.transform(
      cells(i)(j),
      nb.neighbours(i, j, cells)
    )
    swap()
  }

  def current: MutSeq[MutSeq[Cell[T]]] = cells

}

object World {

  def random[T <: Cell[T]](
    rows: Int,
    cols: Int,
    states: Seq[Cell[T]]
  ): World[T] = {
    val random = scala.util.Random
    val cells = MutSeq.fill(rows, cols)(states(random.between(0, states.length)))
    new World[T](cells)
  }

  def randomCell[T <: Cell[T]](
    states: Seq[Cell[T]]
  ): Cell[T] = {
    states(scala.util.Random.between(0, states.length))
  }

}