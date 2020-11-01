package ui

import model.{Cell, Neighbourhood, Rule}
import scalafx.Includes._
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.beans.property.ObjectProperty
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Label, Slider, ToggleButton}
import scalafx.scene.layout.VBox
import scalafx.util.Duration

class Buttons[T <: Cell[T]](
  private val cellCanvas: CellCanvas[T],
  private val ruleProp: ObjectProperty[_ <: Rule[T]],
  private val nbProp: ObjectProperty[Neighbourhood]
) extends VBox {

  private var currentGeneration = 0
  private val generationLabel = Label("Generation: 0")

  private val timeline = new Timeline {
    cycleCount = Timeline.Indefinite
    keyFrames = KeyFrame(
      Duration(100),
      onFinished = _ => {
        cellCanvas.update(ruleProp.value, nbProp.value)
        currentGeneration += 1
        generationLabel.text = "Generation: " + currentGeneration.toString
      }
    )
  }

  private val playButton = new ToggleButton("Play") {
    minWidth = 100

    handleEvent(ActionEvent.Action) {
      _: ActionEvent => {
        if (selected.value) {
          timeline.play()
          cellCanvas.isEditable.value = false
        }
        else {
          timeline.pause()
          cellCanvas.isEditable.value = true
        }
      }
    }
  }

  private val clearButton = new Button("Clear") {
    minWidth = 100

    handleEvent(ActionEvent.Action) {
      _: ActionEvent => {
        if (playButton.selected.value) {
          playButton.fire()
        }
        cellCanvas.clear()
        cellCanvas.isEditable.value = true
      }
    }
  }

  private val randomButton = new Button("Random") {
    minWidth = 100

    handleEvent(ActionEvent.Action) {
      _: ActionEvent => {
        if (playButton.selected.value) {
          playButton.fire()
        }
        cellCanvas.random()
        currentGeneration = 0
        generationLabel.text = "Generation 0"
        cellCanvas.isEditable.value = true
      }
    }
  }

  private val speedLabel = Label("Choose speed:")

  private val slider = new Slider(0.1, 2, 1) {
    blockIncrement = 0.1
    majorTickUnit = 0.1
    minorTickCount = 0
    showTickLabels = true
    snapToTicks = true

    value.onChange(
      (_, _, newValue) => timeline.rate = newValue.doubleValue()
    )
  }

  spacing = 10

  children = Seq(
    generationLabel,
    playButton,
    clearButton,
    randomButton,
    speedLabel,
    slider
  )

}
