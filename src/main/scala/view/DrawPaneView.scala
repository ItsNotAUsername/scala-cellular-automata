package view

import model.Cell

import scalafx.Includes._
import scalafx.beans.property.ObjectProperty
import scalafx.event.ActionEvent
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.control.{ColorPicker, ComboBox, Label}
import scalafx.scene.layout.GridPane
import scalafx.scene.paint.Color

class DrawPaneView[T <: Cell[T]](
  states:     Seq[T],
  chosenCell: ObjectProperty[T],
  toColor:    ObjectProperty[Map[T, Color]]
) extends PaneView {

  override def getPane: Node = {
    val gridPane = new GridPane {
      hgap = 20
      alignment = Pos.Center
    }

    val cellLabel = new Label("Chosen cell") {
      minWidth = 90
      maxWidth = 90
    }
    gridPane.add(cellLabel, 0, 0)

    val colorPicker = new ColorPicker(toColor.value(states.head)) {
      minWidth = 100
      maxWidth = 100

      onAction = (_: ActionEvent) =>
        toColor.value = toColor.value + (chosenCell.value -> value.value)
    }
    gridPane.add(colorPicker, 2, 0)

    val comboBox = new ComboBox[T](states) {
      minWidth = 100
      maxWidth = 100

      value = states.head

      value onChange {
        (_, _, newValue) => {
          chosenCell.value = newValue
          colorPicker.value = toColor.value(newValue)
        }
      }
    }
    gridPane.add(comboBox, 1, 0)

    gridPane
  }

}
