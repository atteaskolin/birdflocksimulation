package app.logic2D

import scala.collection.mutable.Buffer


case class Component(name: String, multiplier: Double, radius: Double)

trait SteeringBehaviour:
  // Constants, these will change in the objects that extend this trait:
  var cohesion: Component
  var alignment: Component
  var separation: Component

  /** Lists the Boids in the range r, eg. in a circle of radius r where
    * current Boids location is the centerpoint. The current boid is not taken into account*
    */
  def boidsInRange(current: Boid2D, radius: Double) =
    val inRange = Buffer[Boid2D]()
    //
    for boid <- Grid.objects do
      if !boid.eq(current) && (boid.position - current.position).length.abs < radius then
        inRange += boid
    // Grid.objects.map(_.position).foreach(println(_))
    inRange

  /** The Alignment algorithm for a Boid2D: https://www.red3d.com/cwr/steer/gdc99/ */
  def alignment(current: Boid2D): Vector2D =
    // used in the foldLeft as the starting radius
    val zeroVector = new Vector2D(0.0, 0.0)
    // used for the DivideXY
    val inRange = boidsInRange(current, alignment.radius).toList

    // Sums all of the Boids velocity vectors together and divided
    if inRange.nonEmpty then
      val alignmentVector = boidsInRange(current, alignment.radius)
        .map(_.velocity)
        .foldLeft[Vector2D](zeroVector)(_ + _)
        ./(inRange.length)
      // Multiply the alignment vector by a given constant:
      alignmentVector.normalize * alignment.multiplier
    else
      // No boids in Range, return 0 vector as in no force applied
      new Vector2D(0.0, 0.0)

  /** The separation algorithm: Takes one of the boids in range and subtracts
    * the current boids position with the other ones position, normalises it and
    * scales it by a factor of 1/r. This will be done to all of the boids in
    * range. Then the sum of all of those will be the resulting separation
    * vector
    * @param current
    *   The Boid2D this will be done with respect to
    */
  def separation(current: Boid2D): Vector2D =
    val zeroVector = new Vector2D(0.0, 0.0)
    val temporaryList = Buffer[Vector2D]()
    val inRange = boidsInRange(current, separation.radius)

    if inRange.nonEmpty then
      for boid <- inRange do
        val r = (current.position - boid.position).length.abs
        temporaryList += (current.position - boid.position).normalize * (1/r)
      val separationVector = temporaryList.foldLeft[Vector2D](zeroVector)(_ + _)

      // Multiply the separation vector by a given constant
      separationVector.normalize * separation.multiplier
    else
      // No boids in Range, return 0 vector as in no force applied
      new Vector2D(0.0, 0.0)

  /** The cohesion algorithm: Takes the average position of the other boids in
    * range and outputs a vector that points to the average position by
    * subtracting the current boids location with the location of the average
    * position.
    * @param current
    *   The Boid2D this will be done with respect to
    */
  def cohesion(current: Boid2D): Vector2D =
    val zeroVector = new Vector2D(0.0, 0.0)
    val numberOfBoidsInRange = boidsInRange(current, cohesion.radius).length.abs
    if numberOfBoidsInRange > 0 then
      val averagePosition = boidsInRange(current, cohesion.radius)
        .map(_.position)
        .foldLeft(zeroVector)(_ + _)
        ./(numberOfBoidsInRange)

      (averagePosition - current.position).normalize * cohesion.multiplier
    else
      // No boids in Range, return 0 vector as in no force applied
      new Vector2D(0.0, 0.0)

object Natural extends SteeringBehaviour:
  var cohesion = Component("cohesion", 0.2, 70)
  var alignment = Component("alignment", 1, 100)
  var separation = Component("separation", 1, 30)

class CustomBehaviour(var cohesion: Component, var alignment: Component, var separation: Component) extends SteeringBehaviour


