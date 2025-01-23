package app.logic2D
import scala.annotation.targetName
import scala.math.*

class Vector2D(var x: Double, var y: Double):

  def length = sqrt(pow(x,2) + pow(y,2))
  def normalize = new Vector2D(this.x/length, this.y/length)

  @targetName("Multiply components by a scalar")
  def *(by: Double) = new Vector2D(this.x*by, this.y*by)

  @targetName("Divide components by a scalar")
  def /(by: Double)  = new Vector2D(this.x/by, this.y/by)

  def truncate(to: Double) =
    if this.length > to then
      val normalized = this.normalize
      val newX = normalized.x * to
      val newY = normalized.y * to
      new Vector2D(newX, newY)
    else
      this

  @targetName("vector subtraction")
  def -(vector2D: Vector2D) = Vector2D(this.x - vector2D.x, this.y - vector2D.y)

  @targetName("vector addition")
  def +(vector2D: Vector2D) = Vector2D(this.x + vector2D.x, this.y + vector2D.y)

  @targetName("vector multiplication")
  def cross(vector2D: Vector2D) = ???

  override def toString: String = s"x:$x, y: $y"


@main def testi() =
  println(Vector2D(2,3))





