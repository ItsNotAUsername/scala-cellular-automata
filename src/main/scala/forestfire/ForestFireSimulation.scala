package forestfire

import model.{Cell, Neighbourhood, World}
import ui.CellCanvas

import scalafx.Includes._
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.application.JFXApp
import scalafx.beans.property.ObjectProperty
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, GridPane}
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import scalafx.scene.{Group, Scene}
import scalafx.util.Duration

object ForestFireSimulation extends JFXApp {

  private val rows = 80
  private val cols = 80
  private val cellSize = 8

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

  private def generateWorld = World.random[FFCell](rows, cols, Seq(Cinder, Tree))

  private val map: Map[Cell[FFCell], Color] = Map(
    Cinder -> Color.SaddleBrown,
    Tree   -> Color.ForestGreen,
    Fire   -> Color.Orange
  )

  stage = new JFXApp.PrimaryStage {

    title.value = "Forest-fire simulation"
    resizable = false

    private val cellCanvas = new CellCanvas[FFCell](generateWorld, cellSize, map)

    scene = new Scene {

      root = new BorderPane {

        left = new GridPane {

          padding = Insets(20)
          hgap = 15
          vgap = 10

          private var currentGen = 0
          private val generation = new Text("Generation: 0")
          add(generation, 0, 0)

          private val timeline = new Timeline {
            cycleCount = Timeline.Indefinite
            keyFrames = KeyFrame(Duration(50), onFinished = _ => {
              cellCanvas.update(ruleProp.value, nbProp.value)
              currentGen += 1
              generation.text = "Generation: " + currentGen.toString
            })
          }

          private val playButton = new ToggleButton("Play") {
            minWidth = 100
            handleEvent(ActionEvent.Action) {
              _: ActionEvent =>
                if (selected.value) {
                  timeline.play()
                } else {
                  timeline.pause()
                }
            }
          }
          add(playButton, 0, 1)

          private val resetButton = new Button("Reset") {
            minWidth = 100
            handleEvent(ActionEvent.Action) {
              _: ActionEvent => {
                if (playButton.selected.value) {
                  playButton.fire()
                }
                cellCanvas.clear()
                cellCanvas.world = generateWorld
                cellCanvas.plotCells()
                currentGen = 0
                generation.text = "Generation 0"
              }
            }
          }
          add(resetButton, 0, 2)

          private val fText = new Text("Probability f that tree ignites: 0.0001")
          add(fText, 0, 3)

          private val fField = new TextField {
            handleEvent(ActionEvent.Action) {
              _: ActionEvent => {
                val prob = text.value.toDoubleOption
                if (prob.isDefined && prob.get >= 0 && prob.get <= 1) {
                  val current = ruleProp.value
                  ruleProp.value = current.copy(f = prob.get)
                  fText.text = "Probability f that tree ignites: " + prob.get.toString
                }
              }
            }
          }
          add(fField, 0, 4)

          private val pText = new Text("Probability p that new tree grows: 0.01")
          add(pText, 0, 5)

          private val pField = new TextField {
            handleEvent(ActionEvent.Action) {
              _: ActionEvent => {
                val prob = text.value.toDoubleOption
                if (prob.isDefined && prob.get >= 0 && prob.get <= 1) {
                  val current = ruleProp.value
                  ruleProp.value = current.copy(p = prob.get)
                  pText.text = "Probability p that new tree grows: " + prob.get.toString
                }
              }
            }
          }
          add(pField, 0, 6)

          add(new Text("Choose neighbourhood:"), 0, 7)

          private val group = new ToggleGroup

          private val moore = new RadioButton("Moore neighborhood") {
            selected = true
            toggleGroup = group

            handleEvent(ActionEvent.Action) {
              _: ActionEvent => nbProp.value = Neighbourhood.MooreNeighbourhood
            }
          }
          add(moore, 0, 8)

          private val vonNeumann = new RadioButton("Von Neumann neighborhood") {
            toggleGroup = group

            handleEvent(ActionEvent.Action) {
              _: ActionEvent => nbProp.value = Neighbourhood.VonNeumannNeighbourhood
            }
          }
          add(vonNeumann, 0, 9)

        }

        center = new Group {
          children = cellCanvas
        }
      }
    }
  }
}
