package app.scenes

import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import app.logic2D.*
import app.logic3D.Vector3D
import main.Constants
import scalafx.animation.{AnimationTimer, KeyFrame, Timeline}
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{AnchorPane, BorderPane, Pane}
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color.*
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.{ChoiceDialog, ContentDisplay, Label, Menu, MenuBar, MenuItem, Slider, TextInputDialog}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.util.Duration
import scalafx.Includes.*
import scalafx.geometry.Pos.{CenterRight, TopCenter}
import scalafx.scene.control.ContentDisplay.Center
import scalafx.scene.text.Font

import scala.collection.mutable.Buffer

class MainMenu extends Scene:

  val label2D = new Label:
    layoutY = 398
    layoutX = 407
    text = "2D"
    font = new Font("Microsoft Tai Le", 96)

  val label3D = new Label:
    alignment = CenterRight
    layoutY = 398.0
    layoutX = 680.0
    contentDisplay = Center
    text = "3D"
    font = new Font("Microsoft Tai Le", 96)

  val labelSelect = new Label:
    alignment = TopCenter
    layoutX = 386.0
    layoutY = 531.0
    prefHeight = 229.0
    prefWidth = 438.0
    font = new Font("Microsoft Yi Baiti", 44)
    text = "Select the simulation mode"

  val center = new AnchorPane:
    children = List(label2D, label3D, labelSelect)

  val borderPane = new BorderPane(center, null, null, null, null):
    prefHeight = Constants.CANVASHEIGHT
    prefWidth = Constants.CANVASWIDTH

  content = borderPane
