package view

import model.{Cell, Neighbourhood, Rule}

import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.control.{ComboBox, Label}
import scalafx.scene.layout.GridPane

class RulePaneView[T <: Cell[T]](
  rule:          ObjectProperty[Rule[T]],
  neighbourhood: ObjectProperty[Neighbourhood]
) extends PaneView {

  override def getPane: Node = {
    val gridPane = new GridPane {
      hgap = 20
      alignment = Pos.Center
    }

    val nbLabel = new Label("Neighbourhood")
    gridPane.add(nbLabel, 0, 1)

    val nbComboBox = new ComboBox[Neighbourhood](Seq(Neighbourhood.Moore, Neighbourhood.VonNeumann)) {
      value = Neighbourhood.Moore
      neighbourhood <== value
    }
    gridPane.add(nbComboBox, 1, 1)

    gridPane
  }

}
