package controller

import model.{Board, Cell, Neighbourhood, Rule}
import util.GUIUtils
import view.{ControlPaneView, DrawPaneView, RulePaneView, SizePaneView}

import scalafx.Includes._
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.beans.property.{BooleanProperty, IntegerProperty, ObjectProperty}
import scalafx.event.ActionEvent
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.{ScrollPane, TitledPane}
import scalafx.scene.effect.{BlurType, DropShadow}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, Priority, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.{Node, Scene}
import scalafx.util.Duration

class AppController[T <: Cell[T]](
  states:            Seq[T],
  initToColor:       Map[T, Color],
  initRule:          Rule[T]
) {

  /** Board and cell size */
  private val rows     = IntegerProperty(80)
  private val cols     = IntegerProperty(120)
  private val cellSize = IntegerProperty(8)

  /** Canvas for drawing cells */
  private val cellCanvas = new Canvas {
    width  = cols.value * cellSize.value
    height = rows.value * cellSize.value

    effect = new DropShadow {
      blurType = BlurType.Gaussian
      radius   = 25
      color    = Color.Black
    }
  }
  private val gc = cellCanvas.graphicsContext2D

  /** Simulation properties */
  private val generation = IntegerProperty(0)
  private val speed = IntegerProperty(20)
  private val running = BooleanProperty(false)

  /** Drawing properties */
  private val chosenCell = ObjectProperty(states.head)
  private val toColor = ObjectProperty(initToColor)

  /** Cellular automaton's properties */
  protected val rule: ObjectProperty[Rule[T]] = ObjectProperty(initRule)
  protected val neighbourhood: ObjectProperty[Neighbourhood] = ObjectProperty(Neighbourhood.Moore)

  /** Board */
  private var board = Board.fill(rows.value, cols.value, states.head)

  /** Timeline */
  private val timeline = new Timeline {
    cycleCount = Timeline.Indefinite
    keyFrames = KeyFrame(
      time = Duration(1000 / speed.value),
      onFinished = (_: ActionEvent) => updateBoard()
    )
  }

  /** PaneViews */
  private val controlPaneView = new ControlPaneView(
    onPlay(),
    onClear(),
    onRandom(),
    generation,
    speed,
    (1, 40)
  )
  private val sizePaneView = new SizePaneView(
    Seq(
      ("Rows",      rows,     (1, 500)),
      ("Columns",   cols,     (1, 500)),
      ("Cell size", cellSize, (1, 20) )
    )
  )
  private val colorPaneView = new DrawPaneView[T](states, chosenCell, toColor)
  protected val rulePaneView = new RulePaneView[T](rule, neighbourhood)

  def createScene(): Scene = {
    val controlPane = controlPaneView.getPane
    val colorPane = createPane("Draw", colorPaneView.getPane)
    val rulePane = createPane("Rule", rulePaneView.getPane, disableOnRunning = true)
    val sizePane = createPane("Grid", sizePaneView.getPane, disableOnRunning = true)

    val dashboard = new VBox {
      alignment = Pos.TopCenter
      children = Seq(controlPane, colorPane, rulePane, sizePane)
    }

    val sceneContent = new HBox {
      padding = Insets(25)
      spacing = 25
      children = Seq(dashboard, cellCanvas)
    }

    initEventHandlers()
    drawBoard()

    new Scene {
      stylesheets += getClass.getResource("/css/styles.css").toExternalForm

      root = new ScrollPane {
        vgrow = Priority.Always
        hgrow = Priority.Always
        fitToHeight = true
        fitToWidth = true
        content = sceneContent
      }
    }
  }

  private def createPane(title: String, node: Node, disableOnRunning: Boolean = false): TitledPane =
    new TitledPane {
      text = title
      content = node
      if (disableOnRunning) {
        node.disable <== running
      }
    }

  /** Event handlers */
  private def initEventHandlers(): Unit = {
    rows onChange {
      (_, _, newValue) => {
        cellCanvas.height = cellSize.value * newValue.doubleValue()
        updateBoardSize()
      }
    }
    cols onChange {
      (_, _, newValue) => {
        cellCanvas.width = cellSize.value * newValue.doubleValue()
        updateBoardSize()
      }
    }
    cellSize onChange {
      (_, _, newValue) => {
        cellCanvas.width  = cols.value * newValue.doubleValue()
        cellCanvas.height = rows.value * newValue.doubleValue()
        drawBoard()
      }
    }

    speed onChange {
      (_, _, newValue) => timeline.rate = newValue.doubleValue() / 20
    }

    toColor onChange {
      drawBoard()
    }

    // Mouse events
    val eventHandler = (event: MouseEvent) => if (!running.value) {
      val x = event.x.toInt / cellSize.value
      val y = event.y.toInt / cellSize.value
      if (x < cols.value && x >= 0 && y < rows.value && y >= 0) {
        board = board.copy(x, y, chosenCell.value)
        drawBoard()
      }
    }
    cellCanvas.onMouseClicked = eventHandler
    cellCanvas.onMouseDragged = eventHandler
  }

  /** Callbacks */
  private def onPlay(): Unit = {
    if (running.value) {
      running.value = false
      timeline.pause()
    } else {
      running.value = true
      timeline.play()
    }
  }

  private def onClear(): Unit = {
    if (running.value) {
      running.value = false
    }
    board = Board.fill(rows.value, cols.value, states.head)
    generation.value = 0
    drawBoard()
  }

  private def onRandom(): Unit = {
    if (running.value) {
      running.value = false
    }
    board = Board.random(rows.value, cols.value, states)
    generation.value = 0
    drawBoard()
  }

  private def updateBoard(): Unit = {
    board = board.next(rule.value, neighbourhood.value)
    generation.value = generation.value + 1
    drawBoard()
  }

  private def updateBoardSize(): Unit = {
    board = Board.fill(rows.value, cols.value, states.head)
    drawBoard()
  }

  private def drawBoard(): Unit = {
    GUIUtils.drawBoard(board, cellSize.value, toColor.value)(gc)
  }

}