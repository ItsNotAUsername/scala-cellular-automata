package wireworld

import model.{Cell, Rule}

import scala.collection.mutable

sealed trait WWCell extends Cell[WWCell]

case object Empty extends WWCell

case object ElectronHead extends WWCell

case object ElectronTail extends WWCell

case object Conductor extends WWCell

object WireWorldRule extends Rule[WWCell] {

  override def transform(
    cell: Cell[WWCell],
    neighbours: mutable.Seq[Cell[WWCell]]
  ): Cell[WWCell] = cell match {
    case Empty => Empty
    case ElectronHead => ElectronTail
    case ElectronTail => Conductor
    case Conductor =>
      val headCount = neighbours.count(_ == ElectronHead)
      if (headCount == 1 || headCount == 2)
        ElectronHead
      else
        Conductor
  }

}