package app.logic3D

import main.Constants
import scala.math.*
import scala.util.Random

class BoidLine(var x1: Double, var y1: Double, var x2:Double, var y2: Double) // NOT USED
class Predator(initial: Vector3D) extends Boid3D(initial) // NOT USED

//separate class from Boid2D since I didn't want to break the working 2D simulation  while trying to implement this.
case class Boid3D(val initialPos: Vector3D) extends Cloneable:
  // Initially natural, the sliders will replace the current steeringBehaviour with a "custom" steeringBehaviour
  var steeringBehaviour: SteeringBehaviour = Natural3D
  def copy(): Boid3D = this.clone() match
    case b: Boid3D => b
    case _ => throw new Error("Boid3D.clone() didn't produce a Boid3D object")

  // variables:
  var mass: Double = 15000
  var position: Vector3D = initialPos
  var velocity: Vector3D = Vector3D(Random.nextDouble(), Random.nextDouble(), Random.nextDouble())
  var max_force: Double = 0.001
  var max_speed: Double = 0.005

  def updateMass(value: Double) = mass = value
  def updateMaxForce(force: Double) = max_force = force
  def updateMaxSpeed(speed: Double) = max_speed = speed
  // variables used to draw the 3D image
  var position3D = position
    .cameraView
    .offsetZ
    .scaledPerspective
  var velocityLine3D = (position + velocity)
    .cameraView
    .offsetZ
    .scaledPerspective

  /** __only a thread should call this__ */
  def update3D() =
    updatePosition3D()
    updateVelocityLine3D()

  /** __Only a thread should use this function__ */
  def updatePosition3D() =
    position3D = position
      .offsetZ
      .cameraView
      .scaledPerspective
  /** __Only a thread should use this function__ */
  def updateVelocityLine3D() =
    velocityLine3D = (position + velocity * 5)
      .offsetZ
      .cameraView
      .scaledPerspective

  // Steering behaviour updates:
  // Cohesion:
  def updateCohesion(value: Double) =
    val s = steeringBehaviour.separation
    val c = Component(steeringBehaviour.cohesion.name, value, steeringBehaviour.cohesion.radius)
    val a = steeringBehaviour.alignment
    updatePreset(c, s, a)
  def updateCohesionRadius(radius: Double) =
    val s = steeringBehaviour.separation
    val c = Component(steeringBehaviour.cohesion.name, steeringBehaviour.cohesion.multiplier, radius)
    val a = steeringBehaviour.alignment
    updatePreset(c, s, a)
  // Alignment:
  def updateAlignment(value: Double) =
    val a = Component(steeringBehaviour.alignment.name, value, steeringBehaviour.alignment.radius)
    val c = steeringBehaviour.cohesion
    val s = steeringBehaviour.separation
    updatePreset(c, s, a)
  def updateAlignmentRadius(radius: Double) =
    val s = steeringBehaviour.separation
    val c = steeringBehaviour.cohesion
    val a = Component(steeringBehaviour.alignment.name, steeringBehaviour.alignment.multiplier, radius)
    updatePreset(c, s, a)
  // Separation:
  def updateSeparation(value: Double) =
    val s = Component(steeringBehaviour.separation.name, value, steeringBehaviour.separation.radius)
    val c = steeringBehaviour.cohesion
    val a = steeringBehaviour.alignment
    updatePreset(c, s, a)
  def updateSeparationRadius(radius: Double) =
    val s = Component(steeringBehaviour.separation.name, steeringBehaviour.separation.multiplier, radius)
    val c = steeringBehaviour.cohesion
    val a = steeringBehaviour.alignment
    updatePreset(c, s, a)

  // update the 3D when the simulation starts
  update3D()

  /** Updating a single steering behaviour steeringBehaviour */
  def updatePreset(cohesion: Component, separation: Component, alignment: Component) =
    steeringBehaviour = new CustomBehaviour(cohesion, alignment, separation)

  // Updating position and velocity:
  def updatePosition() =
    position.x += velocity.x
    position.y += velocity.y
    position.z += velocity.z
  def updateVelocity() =

    val force = (steeringBehaviour.cohesion(this)+steeringBehaviour.alignment(this)+steeringBehaviour.separation(this))
    // F=MA => a=f/m
    val acceleration = force / mass
    velocity = (velocity + acceleration).truncate(max_speed)

  // Screen boundaries:
  private def teleportBackToScreenXWise(): Unit =
    val maxX = Constants.Boid.maxX
    val minX = Constants.Boid.minX

    position match
      case pos if pos.x <= minX => position.x = maxX
      case pos if pos.x >= maxX => position.x = minX
      case _ =>
  private def teleportBackToScreenYWise() =
    val maxY = Constants.Boid.maxY
    val minY = Constants.Boid.minY

    position match
      case pos if pos.y <= minY => position.y = maxY
      case pos if pos.y >= maxY => position.y = minY
      case _ =>
  private def teleportBackToScreenZWise() =
    val maxZ = Constants.Boid.maxZ
    val minZ = Constants.Boid.minZ

    position match
      case pos if pos.z <= minZ => position.z = maxZ
      case pos if pos.z >= maxZ => position.z = minZ
      case _ =>

  /** The tick method that will make the boid move,
   * called by a separate thread */
  def tick() =
    updateVelocity()
    updatePosition()

    // Check if the Boid3D is in the screen area, if not then teleport it back into it.
    this.teleportBackToScreenXWise()
    this.teleportBackToScreenYWise()
    this.teleportBackToScreenZWise()




