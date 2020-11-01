package wireworld

import model.{Cell, Neighbourhood}
import ui.{Buttons, CellCanvas, CellPicker, NeighbourhoodPicker}

import scalafx.application.JFXApp
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Insets
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.{Group, Scene}

object WireWorldSimulation extends JFXApp {

  private val rows = 40
  private val cols = 40
  private val cellSize = 15

  private val ruleProp = new ObjectProperty[WireWorldRule.type](
    this,
    "rule",
    WireWorldRule
  )

  private val nbProp = new ObjectProperty[Neighbourhood](
    this,
    "neighbourhood",
    Neighbourhood.MooreNeighbourhood
  )

  private val map = Map[Cell[WWCell], ObjectProperty[Color]](
    Empty        -> ObjectProperty(Color.White),
    ElectronHead -> ObjectProperty(Color.DeepSkyBlue),
    ElectronTail -> ObjectProperty(Color.DarkGrey),
    Conductor    -> ObjectProperty(Color.DarkOrange)
  )

  stage = new JFXApp.PrimaryStage {
    title.value = "Wire World"
    resizable = false

    private val cellCanvas = new CellCanvas[WWCell](
      rows,
      cols,
      cellSize,
      map,
      Empty
    )

    scene = new Scene {
      root = new BorderPane {
        left = new VBox {
          private val buttons = new Buttons[WWCell](
            cellCanvas,
            ruleProp,
            nbProp
          )

          private val nbPicker = new NeighbourhoodPicker(nbProp)

          private val cellPicker = new CellPicker[WWCell](cellCanvas)

          padding = Insets(15)
          spacing = 15

          children = Seq(
            buttons,
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
