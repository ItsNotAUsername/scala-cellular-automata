import forestfire.ForestFireController
import lifelike.LifeLikeController
import wireworld.WireWorldController

import scalafx.application.{JFXApp, Platform}
import scalafx.scene.control.ChoiceDialog

object Main extends JFXApp {

  val controllers = Map(
    "Life-like"   -> LifeLikeController,
    "Forest-fire" -> ForestFireController,
    "WireWorld"   -> WireWorldController
  )

  val dialog = new ChoiceDialog[String](
    "Life-like",
    Seq(
      "Life-like",
      "Forest-fire",
      "WireWorld"
    )
  ) {
    title = "Cellular Automata"
    headerText = ""
    contentText = "Choose simulation: "
  }

  dialog.showAndWait() match {
    case Some(value) =>
      val controller = controllers(value)
      stage = new JFXApp.PrimaryStage {
        title = value
        scene = controller.createScene()
      }
    case None => Platform.exit()
  }

}
