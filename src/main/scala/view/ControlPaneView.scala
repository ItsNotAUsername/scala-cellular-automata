package view

import scalafx.Includes._
import scalafx.beans.property.IntegerProperty
import scalafx.event.ActionEvent
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Node
import scalafx.scene.control.{Button, Label, ToggleButton}
import scalafx.scene.layout.GridPane
import util.GUIUtils

class ControlPaneView(
  onPlay:      => Unit,
  onClear:     => Unit,
  onRandom:    => Unit,
  generation:  IntegerProperty,
  speed:       IntegerProperty,
  speedLimits: (Int, Int)
) extends PaneView {

  override def getPane: Node = {
    val gridPane = new GridPane {
      margin = Insets(20)
      hgap = 25
      vgap = 20
      alignment = Pos.Center
    }

    val generationLabel = Label("Generation: 0")
    gridPane.add(generationLabel, 0, 0, 3, 1)

    generation onChange {
      (_, _, newValue) => generationLabel.text = "Generation: " + newValue.toString
    }

    val bind = GUIUtils.createBinding("Speed", speed, speedLimits)
    gridPane.add(bind, 0, 1, 3, 1)

    val playButton = new ToggleButton("Play") {
      minWidth = 125
      maxWidth = 125

      onAction = (_: ActionEvent) => onPlay
    }
    gridPane.add(playButton, 0, 2)

    val clearButton = new Button("Clear") {
      minWidth = 125
      maxWidth = 125

      onAction = (_: ActionEvent) => {
        if (playButton.selected.value) {
          playButton.fire()
        }
        onClear
      }
    }
    gridPane.add(clearButton, 1, 2)

    val randomButton = new Button("Random") {
      minWidth = 125
      maxWidth = 125

      onAction = (_: ActionEvent) => {
        if (playButton.selected.value) {
          playButton.fire()
        }
        onRandom
      }
    }
    gridPane.add(randomButton, 2, 2)

    gridPane
  }

}
