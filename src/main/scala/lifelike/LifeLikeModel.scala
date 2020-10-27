package lifelike

import model.{Cell, Rule}

/** Life-like cellular automaton */
sealed trait LLCell extends Cell[LLCell]

case object Dead extends LLCell

case object Alive extends LLCell


class LifeLikeRule(born: Seq[Int], survive: Seq[Int]) extends Rule[LLCell] {

  override def transform(
    cell: Cell[LLCell],
    neighbours: scala.collection.mutable.Seq[Cell[LLCell]]
  ): Cell[LLCell] = {
    val numOfAlive = neighbours.count(_ == Alive)
    cell match {
      case Dead if born.contains(numOfAlive) => Alive
      case Alive if !survive.contains(numOfAlive) => Dead
      case _ => cell
    }
  }

  override def toString: String = "B" + born.mkString("") + "/S" + survive.mkString("")

}

object LifeLikeRule {

  case object GameOfLife extends LifeLikeRule(Seq(3), Seq(2, 3))

  case object DayAndNight extends LifeLikeRule(Seq(3, 6, 7, 8), Seq(3, 4, 6, 7, 8))

  case object LifeWithoutDeath extends LifeLikeRule(Seq(3), Seq.from(0 to 8))

  case object HighLife extends LifeLikeRule(Seq(3, 6), Seq(2, 3))

}