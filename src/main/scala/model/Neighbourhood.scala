package model

import scala.collection.mutable.{ Seq => MutSeq }

sealed abstract class Neighbourhood {

  val indices: MutSeq[(Int, Int)]

  def neighbours[T](x: Int, y: Int, grid: MutSeq[MutSeq[T]]): MutSeq[T] = {
    val rows = grid.length
    val cols = grid.head.length
    for {
      (i, j) <- indices
    } yield grid(checkBounds(x + i, rows))(checkBounds(y + j, cols))
  }

  def checkBounds(a: Int, b: Int): Int =
    if (a < 0)
      a + b
    else if (a >= b)
      a - b
    else a

}

object Neighbourhood {

  case object MooreNeighbourhood extends Neighbourhood {

    val indices: MutSeq[(Int, Int)] = MutSeq(
      (-1, -1), (-1,  0), (-1,  1),
      ( 0, -1),           ( 0,  1),
      ( 1, -1), ( 1,  0), ( 1,  1)
    )

  }

  case object VonNeumannNeighbourhood extends Neighbourhood {

    val indices: MutSeq[(Int, Int)] = MutSeq(
      (-1, 0), (1, 0), (0, -1), (0, 1)
    )

  }

}