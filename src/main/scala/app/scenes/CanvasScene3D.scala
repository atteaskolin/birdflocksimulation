package app.scenes

import app.logic2D.*
import app.logic3D.*
import javafx.geometry.Pos
import main.Constants
import scalafx.Includes.*
import scalafx.animation.{AnimationTimer, KeyFrame, Timeline}
import scalafx.application.{JFXApp3, Platform}
import scalafx.geometry.Pos.{BOTTOM_CENTER, BOTTOM_RIGHT, BottomCenter, BottomRight, CENTER, TOP_CENTER, TopLeft}
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.Alert.*
import scalafx.scene.control.{Alert, Label, Menu, MenuBar, MenuItem, Slider, TextInputDialog}
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.scene.layout.{AnchorPane, BorderPane, GridPane, HBox, Pane, StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Font, TextAlignment}
import scalafx.util.Duration

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import scala.collection.mutable.Buffer


class CanvasScene3D extends Scene(Constants.CANVASWIDTH, Constants.CANVASHEIGHT):
  override def toString = "CanvasScene3D"

  // Timer related variables
  var lastTime: Long = 0
  var showFps = false

  // Timer, used Mark Lewis' tutorial to get the animation timer working: https://www.youtube.com/watch?v=zojzE67cjj8
  val timer = AnimationTimer { t =>
    if t > 0 then
      val fps = 1/((t - lastTime) / 1e9)
      if showFps then
        // fps label:
        fpsLabel.setText(s"fps: ${fps.toString.take(6)}")

        // calculate and update the labels that show the tick per second (per thread)
        val labels = (Grid3D.threads.zip(tickLabels)).zipWithIndex
        for tickLabel <- labels do // updating the tick labels
          val text = tickLabel._2 match
            case index if index == (labels.length - 1) =>
              s"obj: ${tickLabel._1._1.fps.toString.take(6)} "
            case _ =>
              s"${tickLabel._2}: ${tickLabel._1._1.fps.toString.take(6)} "
            tickLabel._1._2.setText(text)
      // All draw() etc here
      draw3D()
    // resets the counter
    lastTime = t
  }
  // Timer will run until the scene is changed


  // Labels:
  val tickLabelExplanation = new Label(s"Boid threads (tps): "):
    font = new Font("Microsoft Yi Baiti", 20)
    textFill = White
    layoutX = Constants.CANVASWIDTH - 160
    layoutY = 60

  // fps label for the GUI window
  val fpsLabel = new Label(s"fps: "):
    font = new Font("Microsoft Yi Baiti", 20)
    textFill = White
    layoutX = Constants.CANVASWIDTH - 100
    layoutY = 25

  val tickLabels = Grid3D.threads.zipWithIndex.map(k => new Label(s"ticks ${k._2}: "):
    font = new Font("Microsoft Yi Baiti", 20)
    textFill = White
    layoutX = Constants.CANVASWIDTH - 100
    layoutY = 80 + (k._2 * 20)
  )

  val labelList = List(fpsLabel, tickLabelExplanation) ++ tickLabels

  // Menu items:
  val exitItem = new MenuItem("Exit"):
    onAction = (k) => Platform.exit()

  val inputFileNameDialog = new TextInputDialog(defaultValue = ""):
      title = "Preset saving Window"
      contentText = "Please enter the name of the preset: "
      headerText = s"Save current slider settings as a .json to folder ${JSONMaker.presetFolderPath}"

  val saveItem = new MenuItem("Save"):
    onAction = (k) => // save the current preset as json
      inputFileNameDialog.showAndWait() match // ask the user for a file name
        case None => // file name not found -> do nothing

        // check that there isn't already a file with that name, if there is, throw error dialog
        case file_name: Option[String] if JSONMaker
          .presetFolderContains(file_name
          .getOrElse(throw new Error("Option shouldn't should be None"))) =>
            new Alert(AlertType.Error) {
              headerText = "Name is already in use"
              contentText = s"A preset named ${file_name.getOrElse("")} already exists, please try another name"
            }.showAndWait()

        // file name is valid, proceed
        case Some(file_name)  =>
          // create a new Preset and store it as json into "presets" folder with the name the user has inputted
          val b = Grid3D.objects.head // Boid3D
          val s = b.steeringBehaviour // Boid3D.steeringBehaviour
          val newPreset =
            new Preset(b.max_speed,
                       b.max_force,
                       b.mass,
                       s.cohesion,
                       s.alignment,
                       s.separation)
          Grid3D.savedPresets +=  ((newPreset,file_name)) // add it into the savedPresets Buffer in order to create a new button for it later
          JSONMaker.savePresetAsJSON(newPreset, file_name) // create the new json at JSONMaker (located in CoordinateReader3D.scala)

          // finally create a new button for the GUI for this preset
          presetMenu.items += new MenuItem(file_name){
            onAction = (k) =>
              Grid3D.savedPresets.find(_._2 == file_name) match
                case Some(p: Preset, name: String) => Grid3D.updatePreset(p)
                case None => println(s"Couldn't find the $file_name preset in the saved presets")
              updateSliders(Grid3D.objects.head)
          }

  val simulationMenu = new Menu("Simulation"):
    items = List(saveItem, exitItem)

  val changeTo2DItem = new MenuItem("Change to 2D")
  val editMenu = new Menu("Edit"):
    items = List(changeTo2DItem)

  val objMenuItemList = Grid3D.allTriangles.map(k => new MenuItem(k._2))
  val drawMenu = new Menu("Draw"):
    items = objMenuItemList

  val labelsOffOnItem = new MenuItem(if showFps then "Labels off" else "Labels on"):
    onAction = (click) => this.text.apply().toString.split(" ").last match
      case "off" =>
        text = "Labels on"
        showFps = false
        labelList.foreach(_.setVisible(false))

      case "on" => text = "Labels off"
        showFps = true
        labelList.foreach(_.setVisible(true))


  val presetMenu = new Menu("Presets")

  // create the items for presetMenu. Each preset.json represents a single item
  for i <- JSONMaker.readJSONPresets() do
    val file_name = i._2
    presetMenu.items += new MenuItem(file_name){
      onAction = (k) =>
        Grid3D.savedPresets.find(_._2 == file_name) match
          case Some(p: Preset, name: String) => Grid3D.updatePreset(p)
          case None => println(s"Couldn't find the $file_name preset in the saved presets")
        updateSliders(Grid3D.objects.head)
    }


  val fpsMenu = new Menu("Labels"):
    items = List(labelsOffOnItem)

  val menuBar = new MenuBar:
    menus = List(simulationMenu, editMenu, drawMenu, fpsMenu, presetMenu)

  val menuBox = new HBox:
    children = List(menuBar)


  // Sliders:
  val cohesionSlider =         new Slider(0.0, 1.0, Natural3D.cohesion.multiplier)
  val cohesionRadiusSlider =   new Slider(0.0, 2.0, Natural3D.cohesion.radius)
  val alignmentSlider =        new Slider(0.0, 1.0, Natural3D.alignment.multiplier)
  val alignmentRadiusSlider =  new Slider(0.0, 2.0, Natural3D.alignment.radius)
  val separationSlider =       new Slider(0.0, 1.0, Natural3D.separation.multiplier)
  val separationRadiusSlider = new Slider(0.0, 2.0, Natural3D.separation.radius)
  val boidMassSlider =         new Slider(1, 20000, 15000)
  val boidMaxForceSlider =     new Slider(0.0001, 0.01, 0.001)
  val boidMaxSpeedSlider =     new Slider(0.0001, 0.01, 0.005)

  // labels for the sliders :
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


  // updating labels for the sliders:

  // update text to massSlider, maxSpeedSlider, maxForceSlider
  boidMaxForceSlider.value.onChange {
    maxForceText.text = "Max force: "
      + boidMaxForceSlider.value.apply().toString.take(6)
  }
  boidMaxSpeedSlider.value.onChange {
    maxSpeedText.text = "Max speed: "
      + boidMaxSpeedSlider.value.apply().toString.take(6)
  }
  boidMassSlider.value.onChange {
    massText.text = "Mass: "
      + boidMassSlider.value.apply().toString.take(6)
  }

  // update text to aligment slider
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



  // Canvas:
  val canvas = new Canvas(Constants.CANVASWIDTH, Constants.CANVASHEIGHT)
  val g = canvas.graphicsContext2D

  val anchorPane = new AnchorPane:
    prefWidth = Constants.CANVASWIDTH
    prefHeight = Constants.CANVASHEIGHT
    children = List(canvas) ++ labelList


  val BorderPane = new BorderPane:
    prefWidth = Constants.CANVASWIDTH
    prefHeight = Constants.CANVASHEIGHT
    bottom = sliderBox
    top = menuBar


  val finalPane = new StackPane:
    children = List(anchorPane, BorderPane)

  content = finalPane

  // hide (fps) labels if showfps is false
  if !showFps then
    labelList.foreach(_.setVisible(false))



  /**
   * Update all of the sliders to correct values, this is called when the preset is changed
   * @param b: the boid whose .preset will be used as a reference*/

  def updateSliders(b: Boid3D) =
    val s = b.steeringBehaviour // the steeringbehaviour of the boid

    separationSlider.value       = s.separation.multiplier
    separationRadiusSlider.value = s.separation.radius

    cohesionSlider.value         = s.cohesion.multiplier
    cohesionRadiusSlider.value   = s.cohesion.radius

    alignmentSlider.value        = s.alignment.multiplier
    alignmentRadiusSlider.value  = s.alignment.radius

    boidMaxForceSlider.value     = b.max_force
    boidMaxSpeedSlider.value     = b.max_speed
    boidMassSlider.value         = b.mass




  // =================================================================================================================================
  // All canvas drawing is below this line
  // =================================================================================================================================


  // Set the origo to be in the middle of the screen
  g.translate(Constants.CANVASWIDTH / 2, Constants.CANVASHEIGHT / 2)

  // default the background to black without the draw3D  - for debugging purposes
  g.fill = Black; fillBackground()
  /** Calls the Grid's tick method that makes the 3D Boids move in 3D simulation space */
  def tick3D() = Grid3D.tick()

  /** Draws the Boids into the canvas in 3D */
  def draw3D() =
    g.stroke = Color.White
    g.fill = Color.Black
    fillBackground()
    g.fill = Color.Yellow

    drawBoidsIntoCubeThreads()
    //drawBoidsIntoCube()
    //drawRotatingCube()
    //drawCube()
    //drawBoids()
    //drawNormals()

  def fillBackground() = g.fillRect(-Constants.CANVASWIDTH / 2, -Constants.CANVASHEIGHT / 2, Constants.CANVASWIDTH, Constants.CANVASHEIGHT)

  var currentTriangles = Grid3D.defaultTriangles  // must be var, can be changed using the GUI

  def getCurrentTriangles = currentTriangles

  /** Draw a rotating cube, don't utilise threading*/
  def drawRotatingCube() =
    // rotate the cube (changes the angle values on the object rotatationMatrix_cube)
    rotationMatrix_cube.x += 0.001
    rotationMatrix_cube.z += 0.002
    rotationMatrix_cube.y += 0.003

    for triangle <- currentTriangles do
      if triangle.rotated.cameraView.normal.z > 0 then
        g.fillPolygon(triangle
          .rotated
          .cameraView
          .offsetZ
          .scaledPerspective
          .toSeq
          .map(t => (t.x, t.y))
        )
  /** Draw a cube, don't utilise threading */
  def drawCube() =
    for triangle <- currentTriangles do
      //if triangle.rotated.normal.z > 0 then
        g.strokePolygon(triangle
          .cameraView
          .offsetZ
          .scaledPerspective
          .toSeq
          .map(t => (t.x, t.y))
        )
  /** Draw boids into a cube, don't utilise threading */
  def drawBoidsIntoCube() =
    //drawCube()
      for boid <- Grid3D.objects do
        val velocity = (boid.velocity + boid.position)
          .cameraView
          .offsetZ
          .scaledPerspective
        val pos = boid.position
          .cameraView
          .offsetZ
          .scaledPerspective
        g.strokeLine(pos.x, pos.y, velocity.x, velocity.y)
        g.fillOval(pos.x - 2, pos.y - 2, 4, 4) // the position must be offseted by (-size/2) in order for the velocityline to line up

  /** Draw boids into a cube, utilise threading
   * Grid3D.threads must be running for this to work */
  def drawBoidsIntoCubeThreads() =
    // draw the obj file
    for triangle <- currentTriangles do
      val posZ = triangle.points3D.map(_.z)
      if posZ.forall(_ < 1) then // don't draw if its not visible (outside of normalized cube or image space)
        g.strokePolygon(triangle.points3D.map(k => ((k.x, k.y)) ))

    // draw the boids as dots
    for boid <- Grid3D.objects do
      val pos = boid.position3D
      val velocity = boid.velocityLine3D
      if pos.z < 1 && velocity.z < 1 then // don't draw if its not visible (outside of normalized cube or image space)
        g.fillOval(pos.x - 2, pos.y - 1, 2, 2)
        g.strokeLine(pos.x, pos.y, velocity.x, velocity.y)

  /** Draw only the boids, don't utilise threading */
  def drawBoids() =
    Grid3D.objects
      .foreach(k => g
        .fillOval(k.position.scaledPerspective.x, k.position.scaledPerspective.y, 2, 2)
      )

  // camera controls:
  onKeyPressed = (ke: KeyEvent) =>
    ke.code match
      // camera position
      case KeyCode.W     => Camera.pos += (CameraMatrix.dir * Camera.speed) // move forwards
      case KeyCode.S     => Camera.pos -= (CameraMatrix.dir * Camera.speed) // move backwards
      case KeyCode.A     => Camera.pos -= (CameraMatrix.right  * Camera.speed) // move left
      case KeyCode.D     => Camera.pos += (CameraMatrix.right  * Camera.speed) // move right
      case KeyCode.Space => Camera.pos.y -= Camera.speed // move up
      /* ctrl, shift and alt won't repeatedly update when held down
       (unlike W,A,S,D, space) which is why 'down' is Z */
      case KeyCode.Z     => Camera.pos.y += Camera.speed // move down
      // Rotation
      case KeyCode.I =>
        rotationMatrix_camera.x -= Camera.rotSensitivity // look up
        rotationMatrix_camera.updateMat
      case KeyCode.K =>
        rotationMatrix_camera.x += Camera.rotSensitivity // look down
        rotationMatrix_camera.updateMat
      case KeyCode.J =>
        rotationMatrix_camera.y += Camera.rotSensitivity // look left
        rotationMatrix_camera.updateMat
      case KeyCode.L =>
        rotationMatrix_camera.y -= Camera.rotSensitivity // look right
        rotationMatrix_camera.updateMat
      case _ =>




