package lifelike

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

object LifeLikeSimulation extends JFXApp {

  private val rows = 40
  private val cols = 40
  private val cellSize = 15

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

  private val map = Map[Cell[LLCell], ObjectProperty[Color]](
    Dead  -> ObjectProperty(Color.White),
    Alive -> ObjectProperty(Color.Green)
  )

  stage = new JFXApp.PrimaryStage {
    title.value = "Cellular Automaton"
    resizable = false

    private val cellCanvas = new CellCanvas[LLCell](
      rows,
      cols,
      cellSize,
      map,
      Dead
    )

    scene = new Scene {
      root = new BorderPane {
        left = new VBox {
          private val buttons = new Buttons[LLCell](
            cellCanvas,
            ruleProp,
            nbProp
          )

          private val label = Label("Choose mode:")

          private val modeGroup = new ToggleGroup

          private val gameOfLife = new RadioButton("Game of life") {
            selected = true
            toggleGroup = modeGroup

            handleEvent(ActionEvent.Action) {
              _: ActionEvent => ruleProp.value = LifeLikeRule.GameOfLife
            }
          }

          private val dayAndNight = new RadioButton("Day and night") {
            toggleGroup = modeGroup

            handleEvent(ActionEvent.Action) {
              _: ActionEvent => ruleProp.value = LifeLikeRule.DayAndNight
            }
          }

          private val lifeWithoutDeath = new RadioButton("Life without death") {
            toggleGroup = modeGroup

            handleEvent(ActionEvent.Action) {
              _: ActionEvent => ruleProp.value = LifeLikeRule.LifeWithoutDeath
            }
          }

          private val highLife = new RadioButton("Highlife") {
            toggleGroup = modeGroup

            handleEvent(ActionEvent.Action) {
              _: ActionEvent => ruleProp.value = LifeLikeRule.HighLife
            }
          }

          private val seeds = new RadioButton("Seeds") {
            toggleGroup = modeGroup

            handleEvent(ActionEvent.Action) {
              _: ActionEvent => ruleProp.value = LifeLikeRule.Seeds
            }
          }

          private val nbPicker = new NeighbourhoodPicker(nbProp)

          private val cellPicker = new CellPicker[LLCell](cellCanvas)

          padding = Insets(15)
          spacing = 15

          children = Seq(
            buttons,
            label,
            gameOfLife,
            dayAndNight,
            lifeWithoutDeath,
            highLife,
            seeds,
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