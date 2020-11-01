package forestfire

import model.{Cell, Neighbourhood}
import ui.{Buttons, CellCanvas, CellPicker, NeighbourhoodPicker}

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.beans.property.ObjectProperty
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.{Group, Scene}

object ForestFireSimulation extends JFXApp {

  private val rows = 40
  private val cols = 40
  private val cellSize = 15

  private val ruleProp = new ObjectProperty[ForestFireRule](
    this,
    "rule",
    ForestFireRule(f = 0.0001, p = 0.01)
  )

  private val nbProp = new ObjectProperty[Neighbourhood](
    this,
    "neighbourhood",
    Neighbourhood.MooreNeighbourhood
  )

  private val map = Map[Cell[FFCell], ObjectProperty[Color]](
    Cinder -> ObjectProperty(Color.Black),
    Tree   -> ObjectProperty(Color.ForestGreen),
    Fire   -> ObjectProperty(Color.Red)
  )

  stage = new JFXApp.PrimaryStage {
    title.value = "Forest-fire simulation"
    resizable = false

    private val cellCanvas = new CellCanvas[FFCell](
      rows,
      cols,
      cellSize,
      map,
      Cinder
    )

    scene = new Scene {
      root = new BorderPane {
        left = new VBox {
          private val buttons = new Buttons[FFCell](
            cellCanvas,
            ruleProp,
            nbProp
          )

          private val fLabel = Label("Probability f that tree ignites: 0.0001")

          private val fField = new TextField {
            handleEvent(ActionEvent.Action) {
              _: ActionEvent => {
                val prob = text.value.toDoubleOption
                if (prob.isDefined && prob.get >= 0 && prob.get <= 1) {
                  val current = ruleProp.value
                  ruleProp.value = current.copy(f = prob.get)
                  fLabel.text = "Probability f that tree ignites: " + prob.get.toString
                }
              }
            }
          }

          private val pLabel = Label("Probability p that new tree grows: 0.01")

          private val pField = new TextField {
            handleEvent(ActionEvent.Action) {
              _: ActionEvent => {
                val prob = text.value.toDoubleOption
                if (prob.isDefined && prob.get >= 0 && prob.get <= 1) {
                  val current = ruleProp.value
                  ruleProp.value = current.copy(p = prob.get)
                  pLabel.text = "Probability p that new tree grows: " + prob.get.toString
                }
              }
            }
          }

          private val nbPicker = new NeighbourhoodPicker(nbProp)

          private val cellPicker = new CellPicker[FFCell](cellCanvas)

          padding = Insets(15)
          spacing = 15

          children = Seq(
            buttons,
            fLabel,
            fField,
            pLabel,
            pField,
            nbPicker,
            cellPicker
          )
        }

        center = new Group {
          children = cellCanvas
        }
      }
    }
  }
}
