package app.logic2D
import app.logic2D.Vector2D
import main.Constants

import scala.math.*
import scala.util.Random


class Boid2D(initialPos: Vector2D):
  private def random = Random
  var preset: SteeringBehaviour = Natural

  var mass: Double = 1500
  val position: Vector2D = initialPos
  var velocity: Vector2D = Vector2D(Random.nextDouble(), Random.nextDouble())
  var max_force: Double = 0.01
  var max_speed: Double = 0.05
  
  def updateMaxForce(value: Double) = max_force = value
  def updateMaxSpeed(value: Double) = max_speed = value
  def updateMass(value: Double) = mass = value
 
  /** __should only be used by tick()__ */
  def updatePosition() =
    position.x += velocity.x
    position.y += velocity.y
  /** __should only be used by tick()__ */
  def updateVelocity() =
    val force = (preset.cohesion(this)+preset.alignment(this)+preset.separation(this))
    // F=MA => a=f/m
    val acceleration = force / mass
    velocity = (velocity + acceleration).truncate(max_speed)

  private def teleportBackToScreenXWise() =
      if this.position.x < 0 then
        this.position.x = Constants.CANVASWIDTH
      else if this.position.x > Constants.CANVASWIDTH then
        this.position.x = 1
  private def teleportBackToScreenYWise() =
      if this.position.y < 0 then
        this.position.y = Constants.CANVASHEIGHT
      else if this.position.y > Constants.CANVASHEIGHT then
        this.position.y = 1

  def updateCohesion(value: Double) =
    val p = preset.cohesion
    val s = preset.separation
    val a = preset.alignment
    val c = Component(p.name, value, p.radius)
    updatePreset(c, s, a)

  def updateAlignment(value: Double) =
    val p = preset.alignment
    val a = Component(p.name, value, p.radius)
    val c = preset.separation
    val s = preset.separation
    updatePreset(c, s, a)
  def updateSeparation(value: Double) =
    val s = Component(preset.separation.name, value, preset.separation.radius)
    val c = preset.cohesion
    val a = preset.alignment
    updatePreset(c, s, a)

  def updatePreset(cohesion: Component, separation: Component, alignment: Component) =
    preset = new CustomBehaviour(cohesion, alignment, separation)

  def updateCohesionRadius(radius: Double) =
    val s = preset.separation
    val c = Component(preset.cohesion.name, preset.cohesion.multiplier, radius)
    val a = preset.alignment
    updatePreset(c, s, a)
  
  def updateAlignmentRadius(radius: Double) =
    val s = preset.separation
    val c = preset.cohesion
    val a = Component(preset.alignment.name, preset.alignment.multiplier, radius)
    updatePreset(c, s, a)
  
  def updateSeparationRadius(radius: Double) =
    val s = Component(preset.separation.name, preset.separation.multiplier, radius)
    val c = preset.cohesion
    val a = preset.alignment
    updatePreset(c, s, a)




  def tick() =
    updateVelocity()
    updatePosition()

    // Check if the Boid2D is in the screen area, if not then teleport it back into it.
    this.teleportBackToScreenYWise()
    this.teleportBackToScreenXWise()



