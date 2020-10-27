package lifelike

import model.{Cell, Neighbourhood, World}
import ui.CellCanvas

import scalafx.Includes._
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.application.JFXApp
import scalafx.beans.property.ObjectProperty
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, RadioButton, ToggleButton, ToggleGroup}
import scalafx.scene.layout.{BorderPane, GridPane}
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import scalafx.scene.{Group, Scene}
import scalafx.util.Duration

object LifeLikeSimulation extends JFXApp {

  private val rows = 80
  private val cols = 80
  private val cellSize = 8

  private val ruleProp = new ObjectProperty[LifeLikeRule](
    this,
    "rule",
    LifeLikeRule.GameOfLife
  )

  private val nbProp = new ObjectProperty[Neighbourhood](
    this,
    "neighbourhood",
    Neighbourhood.MooreNeighbourhood
  )

  private def generateWorld: World[LLCell] = World.random(rows, cols, Seq(Dead, Alive))

  private val map: Map[Cell[LLCell], Color] = Map(
    Dead -> Color.White,
    Alive -> Color.Green
  )

  stage = new JFXApp.PrimaryStage {

    title.value = "Cellular Automaton"
    resizable = false

    private val cellCanvas = new CellCanvas[LLCell](generateWorld, cellSize, map)

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
            keyFrames = KeyFrame(Duration(100), onFinished = _ => {
              cellCanvas.update(ruleProp.value, nbProp.value)
              currentGen += 1
              generation.text = "Generation: " + currentGen.toString
            })
          }

          private val playButton = new ToggleButton("Run") {
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

          add(new Text("Choose mode:"), 0, 3)

          private val modeGroup = new ToggleGroup

          private val gameOfLife = new RadioButton("Game of life") {
            selected = true
            toggleGroup = modeGroup

            handleEvent(ActionEvent.Action) {
              _: ActionEvent => ruleProp.value = LifeLikeRule.GameOfLife
            }
          }
          add(gameOfLife, 0, 4)

          private val dayAndNight = new RadioButton("Day and night") {
            toggleGroup = modeGroup

            handleEvent(ActionEvent.Action) {
              _: ActionEvent => ruleProp.value = LifeLikeRule.DayAndNight
            }
          }
          add(dayAndNight, 0, 5)

          private val lifeWithoutDeath = new RadioButton("Life without death") {
            toggleGroup = modeGroup

            handleEvent(ActionEvent.Action) {
              _: ActionEvent => ruleProp.value = LifeLikeRule.LifeWithoutDeath
            }
          }
          add(lifeWithoutDeath, 0, 6)

          private val highLife = new RadioButton("Highlife") {
            toggleGroup = modeGroup

            handleEvent(ActionEvent.Action) {
              _: ActionEvent => ruleProp.value = LifeLikeRule.HighLife
            }
          }
          add(highLife, 0, 7)

          private val seeds = new RadioButton("Seeds") {
            toggleGroup = modeGroup

            handleEvent(ActionEvent.Action) {
              _: ActionEvent => ruleProp.value = LifeLikeRule.Seeds
            }
          }
          add(seeds, 0, 8)

          add(new Text("Choose neighbourhood:"), 0, 9)

          private val nbGroup = new ToggleGroup

          private val moore = new RadioButton("Moore neighborhood") {
            selected = true
            toggleGroup = nbGroup

            handleEvent(ActionEvent.Action) {
              _: ActionEvent => nbProp.value = Neighbourhood.MooreNeighbourhood
            }
          }
          add(moore, 0, 10)

          private val vonNeumann = new RadioButton("Von Neumann neighborhood") {
            toggleGroup = nbGroup

            handleEvent(ActionEvent.Action) {
              _: ActionEvent => nbProp.value = Neighbourhood.VonNeumannNeighbourhood
            }
          }
          add(vonNeumann, 0, 11)

        }

        center = new Group {
          children = cellCanvas
        }
      }
    }
  }
}