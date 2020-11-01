package ui

import model.Neighbourhood
import model.Neighbourhood._

import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.{ComboBox, Label}
import scalafx.scene.layout.VBox

class NeighbourhoodPicker(
  private val nbProperty: ObjectProperty[Neighbourhood]
) extends VBox {

  private val label = Label("Choose neighbourhood:")

  private val comboBox = new ComboBox[Neighbourhood](
    Seq(MooreNeighbourhood, VonNeumannNeighbourhood)
  ) {
    value = MooreNeighbourhood

    value.onChange(
      (_, _, newValue) => nbProperty.value = newValue
    )
  }

  spacing = 10

  children = Seq(
    label,
    comboBox
  )

}
