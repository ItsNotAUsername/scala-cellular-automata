package forestfire

import controller.AppController
import model.{Cell, Rule}
import view.RulePaneView

import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.control.{Label, TextField, TextFormatter}
import scalafx.scene.layout.GridPane
import scalafx.scene.paint.Color
import scalafx.util.StringConverter

/** Forest-fire model */
sealed trait FFCell extends Cell[FFCell]

case object Cinder extends FFCell
case object Tree extends FFCell
case object Fire extends FFCell

class ForestFireRule(val f: Double, val p: Double) extends Rule[FFCell] {

  override def transform(cell: FFCell, neighbours: Seq[FFCell]): FFCell = {
    val random = new scala.util.Random()
    cell match {
      case Fire => Cinder
      case Tree if neighbours.contains(Fire) || random.nextDouble() <= f => Fire
      case Cinder if random.nextDouble() <= p => Tree
      case _ => cell
    }
  }

}

object ForestFireController extends AppController[FFCell](
  Seq(Cinder, Tree, Fire),
  Map(
    Cinder -> Color.Black,
    Tree   -> Color.ForestGreen,
    Fire   -> Color.Red
  ),
  new ForestFireRule(f = 0.0001, p = 0.01)
) {

  override protected val rulePaneView = new RulePaneView(rule, neighbourhood) {

    override def getPane: Node = {
      val gridPane = new GridPane {
        hgap = 10
        vgap = 25

        alignment = Pos.Center
      }

      val fLabel = new Label("Probability f that tree ignites")
      gridPane.add(fLabel, 0, 0)

      val fField = new TextField {
        textFormatter = createTextFormatter(0.0001)
      }
      gridPane.add(fField, 1, 0)

      val pLabel = new Label("Probability p that new tree grows")
      gridPane.add(pLabel, 0, 1)

      val pField = new TextField {
        textFormatter = createTextFormatter(0.01)
      }
      gridPane.add(pField, 1, 1)

      val onAction = (_: ActionEvent) => {
        val f = fField.text.value.toDouble
        val p = pField.text.value.toDouble
        rule.value = new ForestFireRule(f, p)
      }
      fField.onAction = onAction
      pField.onAction = onAction

      gridPane.add(super.getPane, 0, 2, 2, 1)

      gridPane
    }

    private def createTextFormatter(initValue: Double): TextFormatter[Double] = {
      val converter = new StringConverter[Double] {

        override def fromString(string: String): Double = string.toDoubleOption match {
          case Some(value) if (value >= 0 && value <= 1) => value
          case _ => 0
        }

        override def toString(t: Double): String = t.toString

      }

      new TextFormatter[Double](converter, initValue)
    }
  }

}