package app.scenes

import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import app.logic2D.*
import app.logic3D.Vector3D
import main.Constants
import scalafx.animation.{AnimationTimer, KeyFrame, Timeline}
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{AnchorPane, BorderPane, HBox, Pane, StackPane, VBox}
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color.*
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.{Label, Menu, MenuBar, MenuItem, Slider}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.util.Duration
import scalafx.Includes.*
import scalafx.geometry.Pos.{CENTER, TOP_CENTER}

import scala.collection.mutable.Buffer

class CanvasScene2D extends Scene(Constants.CANVASWIDTH, Constants.CANVASHEIGHT):

  // Changing this will make the game run faster / slower
  var tickrate = 1500

  // Timer related variables
  var lastTime: Long = 0
  val updateHz = 500 / tickrate
  var counter = 0
  // Timer, used Mark Lewis' tutorial to get the animation timer working: https://www.youtube.com/watch?v=zojzE67cjj8
  val timer = AnimationTimer { t =>
    if t > 0 then
      val timeInSeconds = (t - lastTime) / 1e9
      counter += 1
      if counter > updateHz then
        // All tick() etc here
        tick2D()
        draw2D()
        // reset counter for tickrate to work
        counter = 0
    // resets the counter
    lastTime = t
  }
  // Timer will run until GUI is closed
  // timer.start() // timer start/stop is currently controlled by main

  // Canvas
  val canvas = new Canvas(Constants.CANVASWIDTH, Constants.CANVASHEIGHT)
  val g = canvas.graphicsContext2D

  // Sliders
  val cohesionSlider = new Slider(0.0, 100.0, 0.5)
  val cohesionRadiusSlider = new Slider(0.0, Constants.CANVASHEIGHT, 70)

  val alignmentSlider = new Slider(0.0, 100.0, 0.5)
  val alignmentRadiusSlider = new Slider(0.0, Constants.CANVASHEIGHT, 100)

  val separationSlider = new Slider(0.0, 100.0, 0.5)
  val separationRadiusSlider = new Slider(0.0, Constants.CANVASHEIGHT, 30)

  val boidMassSlider =         new Slider(1, 20000, 15000)
  val boidMaxForceSlider =     new Slider(0.0001, 10, 1)
  val boidMaxSpeedSlider =     new Slider(0.0001, 10, 1)


  val maxSpeedText = new Label:
    text = "Max speed: "
      + boidMaxSpeedSlider.value.apply().toString.take(6)
    textFill = White

  val maxForceText = new Label:
    text = "Max force: "
      + boidMaxForceSlider.value.apply().toString.take(6)
    textFill = White
  val massText = new Label:
    text = "Mass: "
      + boidMassSlider.value.apply().toString.take(6)
    textFill = White

  val cohesionText = new Label:
    text = "Cohesion: "
      + cohesionSlider.value.apply().toString.take(4)
      + " (radius: " + cohesionRadiusSlider.value.apply().toString.take(4) + ")"
    textFill = White

  val alignmentText = new Label:
    text = "Alignment: "
      + alignmentSlider.value.apply().toString.take(4)
      + " (radius: " + alignmentRadiusSlider.value.apply().toString.take(4) + ")"
    textFill = White

  val separationText = new Label:
    text = "Separation: "
      + separationSlider.value.apply().toString.take(4)
      + " (radius: " + separationRadiusSlider.value.apply().toString.take(4) + ")"
    textFill = White

  boidMaxForceSlider.value.onChange {
    Grid.updateMaxForce(boidMaxForceSlider.value.apply())
    maxForceText.text = "Max force: "
      + boidMaxForceSlider.value.apply().toString.take(6)
  }
  boidMaxSpeedSlider.value.onChange {
    Grid.updateMaxSpeed(boidMaxSpeedSlider.value.apply())
    maxSpeedText.text = "Max speed: "
      + boidMaxSpeedSlider.value.apply().toString.take(6)
  }
  boidMassSlider.value.onChange {
    Grid.updateMass(boidMassSlider.value.apply())
    massText.text = "Mass: "
      + boidMassSlider.value.apply().toString.take(6)
  }


  // updating labels for the sliders:
  alignmentSlider.value.onChange {
    alignmentText.text = "Alignment: "
    + alignmentSlider.value.apply().toString.take(4)
    + " (radius: " + alignmentRadiusSlider.value.apply().toString.take(4) + ")"
  }

  // update text to alignment radius slider
  alignmentRadiusSlider.value.onChange {
    alignmentText.text = "Alignment: "
    + alignmentSlider.value.apply().toString.take(4)
    + " (radius: " + alignmentRadiusSlider.value.apply().toString.take(4) + ")"
  }

  // update text to separation slider
  separationSlider.value.onChange {
    separationText.text = "Separation: "
    + separationSlider.value.apply().toString.take(4)
    + " (radius: " + separationRadiusSlider.value.apply().toString.take(4) + ")"
  }

  // update text to separation radius slider
  separationRadiusSlider.value.onChange {
    separationText.text = "Separation: "
    + separationSlider.value.apply().toString.take(4)
    + " (radius: " + separationRadiusSlider.value.apply().toString.take(4) + ")"
  }

  // update text to cohesion slider
  cohesionSlider.value.onChange {
  cohesionText.text =  "Cohesion: "
    + cohesionSlider.value.apply().toString.take(4)
    + " (radius: " + cohesionRadiusSlider.value.apply().toString.take(4) + ")"
  }

  // update text to cohesion slider
  cohesionRadiusSlider.value.onChange {
  cohesionText.text =  "Cohesion: "
    + cohesionSlider.value.apply().toString.take(4)
    + " (radius: " + cohesionRadiusSlider.value.apply().toString.take(4) + ")"
  }

  val sliders = List(
    new VBox(cohesionText,   cohesionSlider,   cohesionRadiusSlider),
    new VBox(alignmentText,  alignmentSlider,  alignmentRadiusSlider),
    new VBox(separationText, separationSlider, separationRadiusSlider),
    new VBox(massText,       boidMassSlider){alignment = TOP_CENTER},
    new VBox(maxSpeedText,   boidMaxSpeedSlider){alignment = TOP_CENTER},
    new VBox(maxForceText,   boidMaxForceSlider){alignment = TOP_CENTER},
  )
  val sliderBox = new HBox:
    children = sliders
    spacing = 10
    alignment = CENTER



  // Menu Items
  val changeTo3DItem = new MenuItem("Change to 3D")
  val exitItem = new MenuItem("Exit")
  val saveItem = new MenuItem("Save")
  val simulationMenu = new Menu("Simulation"):
    items = List(saveItem, exitItem)
  val editMenu = new Menu("Edit"):
    items = List(changeTo3DItem)

  val menuBar = new MenuBar:
    menus = List(simulationMenu, editMenu)
  val anchorPane = new AnchorPane:
    prefWidth = Constants.CANVASWIDTH
    prefHeight = Constants.CANVASHEIGHT
    children = List(canvas)


  val BorderPane = new BorderPane:
    prefWidth = Constants.CANVASWIDTH
    prefHeight = Constants.CANVASHEIGHT
    bottom = sliderBox
    top = menuBar


  val finalPane = new StackPane:
    children = List(anchorPane, BorderPane)



  // Nodes:
  content = finalPane//List(canvas, menuBar, vBox)


  // 2D:
  /** Calls the Grid's tick method that makes the 2D Boids move in 2D space */
  def tick2D() = Grid.tick()

  /** Draws the 2D Boids into the canvas in 2D */

  def fillBackground = g.fillRect(0, 0, Constants.CANVASWIDTH, Constants.CANVASHEIGHT)
  def draw2D() =
    g.fill = Color.Black; fillBackground
    g.stroke = White
    g.fill = Color.Yellow
    for boid <- Grid.objects do
      val pos = boid.position
      val vel = boid.position + (boid.velocity * 500).truncate(10)


      g.fillOval(pos.x - 2, pos.y - 2, 4, 4) // draw the boid as circle

      g.strokeLine(pos.x, pos.y, vel.x, vel.y) // draw it's velocity as a line

