package ui

import model.Cell

import scalafx.Includes._
import scalafx.scene.control.{ColorPicker, ComboBox, Label}
import scalafx.scene.layout.VBox

class CellPicker[T <: Cell[T]](
  private val cellCanvas: CellCanvas[T]
) extends VBox {

  private val label = Label("Choose type of cell:")

  private val comboBox = new ComboBox[Cell[T]](cellCanvas.cellToColor.keys.toSeq) {
    value = cellCanvas.currentCell.value

    value.onChange(
      (_, _, newCell) => cellCanvas.currentCell.value = newCell
    )
  }

  private val colorPicker = new ColorPicker(cellCanvas.cellToColor(comboBox.value.value).value) {
    value.onChange(
      (_, _, newColor) => {
        cellCanvas.cellToColor(comboBox.value.value).value = newColor
        cellCanvas.plotCells()
      }
    )
  }

  spacing = 10

  children = Seq(
    label,
    comboBox,
    colorPicker
  )

}