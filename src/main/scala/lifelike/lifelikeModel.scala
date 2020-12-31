package lifelike

import controller.AppController
import model.{Cell, Rule}
import scalafx.event.ActionEvent
import scalafx.Includes._
import scalafx.geometry.{Insets, Pos}
import view.RulePaneView
import scalafx.scene.Node
import scalafx.scene.control.{Label, ToggleButton}
import scalafx.scene.layout.GridPane
import scalafx.scene.paint.Color

/** Life-like cellular automaton */
sealed trait LLCell extends Cell[LLCell]
case object Dead extends LLCell
case object Alive extends LLCell

class LifeLikeRule(born: Set[Int], survive: Set[Int]) extends Rule[LLCell] {

  override def transform(cell: LLCell, neighbours: Seq[LLCell]): LLCell = {
    val numOfAlive = neighbours.count(_ == Alive)
    cell match {
      case Dead if born.contains(numOfAlive)      => Alive
      case Alive if !survive.contains(numOfAlive) => Dead
      case _ => cell
    }
  }

  override def toString: String = "B" + born.mkString("") + "/S" + survive.mkString("")

}

object LifeLikeController extends AppController[LLCell](
  Seq(Dead, Alive),
  Map(
    Dead -> Color.White,
    Alive -> Color.Blue
  ),
  new LifeLikeRule(Set(3), Set(2, 3))
) {

  override protected val rulePaneView = new RulePaneView(rule, neighbourhood) {

    override def getPane: Node = {
      val gridPane = new GridPane {
        hgap = 10
        vgap = 15
      }

      val bornLabel = new Label("Born") {
        minWidth = 60
        maxWidth = 60
      }
      gridPane.add(bornLabel, 0, 0)

      val surviveLabel = new Label("Survive") {
        minWidth = 60
        maxWidth = 60
      }
      gridPane.add(surviveLabel, 0, 1)

      val buttons = createButtons()

      buttons.foreach {
        case (born, survive, i) =>
          if (i == 2) {
            survive.selected = true
          } else if (i == 3) {
            born.selected = true
            survive.selected = true
          }

          val onAction = (_: ActionEvent) => {
            val born = buttons.filter(_._1.selected.value).map(_._3).toSet
            val survive = buttons.filter(_._2.selected.value).map(_._3).toSet
            rule.value = new LifeLikeRule(born, survive)
          }
          born.onAction = onAction
          survive.onAction = onAction

          gridPane.addColumn(i + 1, born, survive)
      }

      gridPane.add(super.getPane, 0, 3, 10, 1)

      gridPane
    }

    private def createButtons(): Seq[(ToggleButton, ToggleButton, Int)] = {

      def createButton(i: Int): ToggleButton = new ToggleButton(i.toString) {
        minWidth = 30
        maxWidth = 30
        minHeight = 30
        maxHeight = 30
      }

      for {
        i <- 0 to 8
      } yield (createButton(i), createButton(i), i)
    }

  }

}