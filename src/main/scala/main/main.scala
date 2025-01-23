package main

import app.logic3D.*
import app.logic2D.*
import app.scenes.{CanvasScene2D, CanvasScene3D, MainMenu}
import scalafx.animation.{AnimationTimer, Timeline}
import scalafx.application.JFXApp3
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, ButtonType, TextInputDialog}
import scalafx.scene.image.Image
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.scene.layout.{Pane, StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle

import scala.collection.mutable.Buffer
import scala.util.Random


object main extends JFXApp3:
  System.setProperty("quantum.multithreading", "true")
  
  //uncap fps https://stackoverflow.com/questions/28819409/how-to-ignore-the-60fps-limit-in-javafx
  System.setProperty("javafx.animation.fullspeed", "true") 
  
  // canvasScene2D/3D are outside of the start() method so that they can be accessed from anywhere
  var canvasScene2D: CanvasScene2D = null
  // @volatile is used in order for the the 3D threads to not shut off initially
  @volatile var canvasScene3D: CanvasScene3D = null

  override def start(): Unit =

    // create the scenes
    val canvasScene2D = new CanvasScene2D
    val canvasScene3D = new CanvasScene3D

    /* update the canvasScene2D/3D that are outside of the start method
     so that they can be accessed later by CubeThread */
    this.canvasScene2D = canvasScene2D
    this.canvasScene3D = canvasScene3D

    val mainMenu = new MainMenu

    // initial stage is mainMenu (choose 2D/3D window), don't allow resizing
    stage = new JFXApp3.PrimaryStage:
      title = "Selection menu"
      resizable = false
      scene = mainMenu

    // dialog for mainMenu that tells the user that the input wasn't valid
    val notNumberError = new Alert(AlertType.Error):
      title = "Error"
      headerText = "Please input a number that is 1 or greater"

    // prompt dialog for mainMenu that asks for the number of boids to be drawn into 2D
    val boidDialog2D = new TextInputDialog(defaultValue = "50"):
      title  = "Boid number chooser for 2D"
      contentText = "Please choose the number of boids to be simulated"
      headerText = "2D Simulation"

    // ====================================================================================
    // Main menu controls:
    // ====================================================================================

    // 2D clicked, prompt a dialog that asks for the number of boids to be drawn into 2D
    mainMenu.label2D.onMouseClicked = (mouse) =>
      boidDialog2D.showAndWait() match
        // input is not valid
        case None =>
        // input is not valid
        case k: Option[String] if k.flatMap(_.toIntOption).getOrElse(0) <= 0  =>
          notNumberError.showAndWait()

        // input is valid
        case k: Option[String] if k.flatMap(_.toIntOption).getOrElse(-3) > 0 =>
          val r = Random
          val c = Constants

          // remove initial boids
          Grid.objects = Grid.objects.empty

          // generate the new boids
          for i <- 0 until k.flatMap(_.toIntOption).getOrElse(50) do
            Grid.objects += new Boid2D(new Vector2D(
                r.between(0.0, c.CANVASWIDTH),
                r.between(0.0, c.CANVASHEIGHT)
            ))
          // suspend 3D boid threads that are initially on
          Grid3D.suspendThreads()

          // set stage title, change scene, start 2D timer
          stage.scene = canvasScene2D
          stage.title = "2D Simulation"
          canvasScene2D.timer.start()
        case _ =>

    // 3d clicked, set stage title and scene accordingly
    mainMenu.label3D.onMouseClicked = (mouse) =>
      stage.scene = canvasScene3D
      stage.title = "3D Simulation"
      canvasScene3D.timer.start()


    // ====================================================================================
    // 2D Simulation controls:
    // ====================================================================================
    // used to change the simulation mode to 3D
    canvasScene2D.changeTo3DItem.onAction = (event) =>
      // stop 2D timer, start 3D timer
      canvasScene2D.timer.stop()
      canvasScene3D.timer.start()
      //resume 3D threads
      Grid3D.resumeThreads()

      // switch the scene to 3D and set the stage title accordingly
      stage.scene = canvasScene3D
      stage.title = "3D Simulation"

    // 2D slider controls:
    canvasScene2D.separationSlider.      value.onChange {Grid.updateSeparation(canvasScene2D.      separationSlider.value.apply())}
    canvasScene2D.separationRadiusSlider.value.onChange {Grid.updateSeparationRadius(canvasScene2D.separationRadiusSlider.value.apply())}
    // cohesion:
    canvasScene2D.cohesionSlider.        value.onChange {Grid.updateCohesion(canvasScene2D.        cohesionSlider.value.apply())}
    canvasScene2D.cohesionRadiusSlider.  value.onChange {Grid.updateCohesionRadius(canvasScene2D.  cohesionSlider.value.apply())}
    // alignment:
    canvasScene2D.alignmentSlider.       value.onChange {Grid.updateAlignment(canvasScene2D.       alignmentSlider.value.apply())}
    canvasScene2D.alignmentRadiusSlider. value.onChange {Grid.updateAlignmentRadius(canvasScene2D. alignmentRadiusSlider.value.apply())}

    // ====================================================================================
    //3D simulation controls
    // ====================================================================================

    // Button controls:
      // change the simulation mode back to 2D
    canvasScene3D.changeTo2DItem.onAction = (event) =>
      // stop 3D timer, start 2D, switch the scene to 3D
      canvasScene3D.timer.stop()
      canvasScene2D.timer.start()
      // set stage title and scene
      stage.title = "2D Simulation"
      stage.scene = canvasScene2D
      Grid3D.suspendThreads()


      // change the drawn item, is done for all of the objFiles
    canvasScene3D.objMenuItemList.foreach(k =>
      k.onAction = (event) =>
        canvasScene3D.currentTriangles = Grid3D.allTriangles // updates the 'currentTriangles'
          .find(_._2 == k.getText) match
            case Some(b) => b._1
            case None => Grid3D.defaultTriangles
    )
    // 3D slider controls:

      // separation:
    canvasScene3D.separationSlider.value.onChange       {Grid3D.updateSeparation(canvasScene3D.      separationSlider.value.apply())}
    canvasScene3D.separationRadiusSlider.value.onChange {Grid3D.updateSeparationRadius(canvasScene3D.separationRadiusSlider.value.apply())}
      // cohesion:
    canvasScene3D.cohesionSlider.value.onChange         {Grid3D.updateCohesion(canvasScene3D.        cohesionSlider.value.apply())}
    canvasScene3D.cohesionRadiusSlider.value.onChange   {Grid3D.updateCohesionRadius(canvasScene3D.  cohesionSlider.value.apply())}
      // alignment:
    canvasScene3D.alignmentSlider.value.onChange        {Grid3D.updateAlignment(canvasScene3D.       alignmentSlider.value.apply())}
    canvasScene3D.alignmentRadiusSlider.value.onChange  {Grid3D.updateAlignmentRadius(canvasScene3D. alignmentRadiusSlider.value.apply())}
      // boid mass, maxspeed, maxforce
    canvasScene3D.boidMassSlider.value.onChange         {Grid3D.updateMass(canvasScene3D.boidMassSlider.value.apply())}
    canvasScene3D.boidMaxSpeedSlider.value.onChange     {Grid3D.updateMaxSpeed(canvasScene3D.boidMaxSpeedSlider.value.apply())}
    canvasScene3D.boidMaxForceSlider.value.onChange     {Grid3D.updateMaxForce(canvasScene3D.boidMaxForceSlider.value.apply())}
  
  
  /** 
   * Called when the simulation app is closed, stops the 3D threads */
  override def stopApp() = Grid3D.threads.foreach(_.stop())

end main
