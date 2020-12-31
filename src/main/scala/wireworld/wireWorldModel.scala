package wireworld

import controller.AppController
import model.{Cell, Rule}

import scalafx.scene.paint.Color

/** WireWorld cellular automaton */
sealed trait WWCell extends Cell[WWCell]

case object Empty extends WWCell
case object ElectronHead extends WWCell
case object ElectronTail extends WWCell
case object Conductor extends WWCell

object WireWorldRule extends Rule[WWCell] {

  override def transform(cell: WWCell, neighbours: Seq[WWCell]): WWCell = cell match {
    case Empty        => Empty
    case ElectronHead => ElectronTail
    case ElectronTail => Conductor
    case Conductor    =>
      val headCount = neighbours.count(_ == ElectronHead)
      if (headCount == 1 || headCount == 2)
        ElectronHead
      else
        Conductor
  }

}

object WireWorldController extends AppController[WWCell](
  Seq(Empty, ElectronHead, ElectronTail, Conductor),
  Map(
    Empty        -> Color.White,
    ElectronHead -> Color.Blue,
    ElectronTail -> Color.Red,
    Conductor    -> Color.Yellow
  ),
  WireWorldRule
)