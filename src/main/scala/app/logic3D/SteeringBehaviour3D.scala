package app.logic3D

import scala.collection.mutable.Buffer

case class Component(name: String, multiplier: Double, radius: Double)

trait SteeringBehaviour extends Cloneable:
  // Constants, these will change in the objects that extend this trait:
  def copy() = this.clone()

  var cohesion:   Component
  var alignment:  Component
  var separation: Component

  /** Lists the Boids in the range r, eg. in a circle of radius r where current Boids location is the centerpoint. The
    * current boid is not taken into account*
    */
  def boidsInRange(current: Boid3D, radius: Double) =
    val inRange = Buffer[Boid3D]()
    for boid <- Grid3D.objects do
      if !boid.eq(current) && (boid.position - current.position).length.abs < radius then
        inRange += boid
    inRange

  /** The Alignment algorithm for a Boid2D: https://www.red3d.com/cwr/steer/gdc99/
    */
  def alignment(current: Boid3D): Vector3D =
    // used in the foldLeft as the starting radius
    val zeroVector = new Vector3D(0.0, 0.0, 0.0)
    // used for the DivideXY
    val inRange = boidsInRange(current, alignment.radius).toList

    // Sums all of the Boids velocity vectors together and divided
    if inRange.nonEmpty then
      val alignmentVector = boidsInRange(current, alignment.radius)
        .map(_.velocity)
        .foldLeft[Vector3D](zeroVector)(_ + _)
        ./(inRange.length)
      // Multiply the alignment vector by a given constant:
      alignmentVector.normalize * alignment.multiplier
    else
      /** No boids in Range, return 0 vector as in no force applied */
      new Vector3D(0.0, 0.0, 0.0)

  /** The separation algorithm: Takes one of the boids in range and subtracts the current boids position with the other
    * ones position, normalises it and scales it by a factor of 1/r. This will be done to all of the boids in range.
    * Then the sum of all of those will be the resulting separation vector
    * @param current
    *   The Boid2D this will be done with respect to
    */
  def separation(current: Boid3D): Vector3D =
    val zeroVector = new Vector3D(0.0, 0.0, 0.0)
    val temporaryList = Buffer[Vector3D]()
    val inRange = boidsInRange(current, separation.radius)

    if inRange.nonEmpty then
      for boid <- inRange do
        val r = (current.position - boid.position).length.abs
        temporaryList += (current.position - boid.position).normalize * (1 / r)
      val separationVector = temporaryList.foldLeft[Vector3D](zeroVector)(_ + _)

      /** Multiply the separation vector by a given constant */
      separationVector.normalize * separation.multiplier
    else
      /** No boids in Range, return 0 vector as in no force applied */
      new Vector3D(0.0, 0.0, 0.0)

  /** The cohesion algorithm: Takes the average position of the other boids in range and outputs a vector that points to
    * the average position by subtracting the current boids location with the location of the average position.
    * @param current
    *   The Boid2D this will be done with respect to
    */
  def cohesion(current: Boid3D): Vector3D =
    val zeroVector = new Vector3D(0.0, 0.0, 0.0)
    val numberOfBoidsInRange = boidsInRange(current, cohesion.radius).length.abs
    if numberOfBoidsInRange > 0 then
      val averagePosition = boidsInRange(current, cohesion.radius)
        .map(_.position)
        .foldLeft(zeroVector)(_ + _)
        ./(numberOfBoidsInRange)

      (averagePosition - current.position).normalize * cohesion.multiplier
    else
      /** No boids in Range, return 0 vector as in no force applied */
      new Vector3D(0.0, 0.0, 0.0)

  def seek(current: Boid3D, target: Boid3D): Vector3D =
    val desiredVelocity = (current.position - target.position).normalize * current.max_speed
    val steering = (desiredVelocity - current.velocity)
    steering

  def flee(current: Boid3D, target : Boid3D): Vector3D =
    !seek(current, target)

object Natural3D extends SteeringBehaviour:
  override def toString() = "Natural3D"
  var cohesion   = Component("cohesion"  , 0.1, 0.5)
  var alignment  = Component("alignment" , 0.1, 0.5)
  var separation = Component("separation", 0.1, 0.5)
  

class CustomBehaviour(var cohesion: Component, var alignment: Component, var separation: Component)
    extends SteeringBehaviour:
  override def toString() = "Custom"

case class Preset(max_speed: Double,
                  max_force: Double,
                  mass: Double,
                  cohesion: Component,
                  aligment: Component,
                  separation: Component
                 )



  //override def toString() = "Custom_steeringBehaviour"
  //val boid3D: Boid3D = b.copy()
  //val steeringBehaviour = boid3D.steeringBehaviour.copy()
