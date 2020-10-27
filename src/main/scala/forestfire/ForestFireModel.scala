package forestfire

import model.{Cell, Rule}

/** Forest-fire model */
sealed trait FFCell extends Cell[FFCell]

case object Cinder extends FFCell

case object Tree extends FFCell

case object Fire extends FFCell

case class ForestFireRule(f: Double, p: Double) extends Rule[FFCell] {

  override def transform(
    cell: Cell[FFCell],
    neighbours: scala.collection.mutable.Seq[Cell[FFCell]]
  ): Cell[FFCell] = {
    val random = new scala.util.Random()
    cell match {
      case Fire => Cinder
      case Tree if neighbours.contains(Fire) || random.nextDouble() <= f => Fire
      case Cinder if random.nextDouble() <= p => Tree
      case _ => cell
    }
  }

}