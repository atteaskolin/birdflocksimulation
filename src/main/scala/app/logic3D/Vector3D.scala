package app.logic3D

import main.Constants

import scala.annotation.targetName
import scala.math.*

case class Vector3D(var x: Double, var y: Double, var z: Double, var w: Double = 1):

  def length = sqrt(pow(x.toDouble, 2) + pow(y.toDouble, 2) + pow(z.toDouble, 2))
  def normalize =
    new Vector3D(this.x / length, this.y / length, this.z / length)
  
  @targetName("Flip the vector (same as -vector)")
  def unary_! = new Vector3D(-x, -y, -z, w)
  
  @targetName("Multiply components by a scalar")
  def *(by: Double) = new Vector3D(this.x * by, this.y * by, this.z * by)

  @targetName("Divide components by a scalar")
  def /(by: Double) = new Vector3D(this.x / by, this.y / by, this.z / by)

  def truncate(to: Double) =
    if this.length > to then
      val normalized = this.normalize
      val newX = normalized.x * to
      val newY = normalized.y * to
      val newZ = normalized.z * to
      new Vector3D(newX, newY, newZ)
    else this

  def truncateWithRespectToTPS(to: Double) =
    if this.length > to then
      val normalized = this.normalize
      val newX = (normalized.x * to) / Constants.TPS
      val newY = (normalized.y * to) / Constants.TPS
      val newZ = (normalized.z * to) / Constants.TPS
      new Vector3D(newX, newY, newZ)
    else this

  @targetName("Vector subtraction")
  def -(other: Vector3D) =
    Vector3D(this.x - other.x, this.y - other.y, this.z - other.z)

  @targetName("Vector addition")
  def +(other: Vector3D) =
    Vector3D(this.x + other.x, this.y + other.y, this.z + other.z)

  def dot(other: Vector3D) = this.x * other.x + this.y * other.y + this.z * other.z

  @targetName("Vector cross product")
  def cross(other: Vector3D) =

    val newX = (this.y * other.z) - (this.z * other.y)
    val newY = (this.z * other.x) - (this.x * other.z)
    val newZ = (this.x * other.y) - (this.y * other.x)
    new Vector3D(newX, newY, newZ)

  def rotate = rotationMatrix_cube.vecMultiply(this)

  def cameraView = CameraMatrix.vecMultiply(this)

  def asList = List[Double](x, y, z, w)
  
  def toSeq  = Seq[Double](x, y, z, w)

  def offsetZ = new Vector3D(x, y, (z - 10), w)

  def abs = new Vector3D(x.abs, y.abs, z.abs, w.abs)

  /** Creates a new vector with perspective applied.
   * The vector is inside the "image space" that is normalized in a way that all
   * the x,y values are between [-1, 1] */
  def perspective = ProjectionMatrix.vecMultiplyDivideW(this)

  /** Creates a new vector with perspective and scaling applied.
   * The values are scaled with respect to the canvas height and width.
   * The x and y values can be used to draw the vector to the screen in 3D (perspective projection) */
  def scaledPerspective =
    val height = Constants.CANVASHEIGHT
    val width = Constants.CANVASWIDTH

    val p = this.perspective // This vector with perspective projection applied
    
    val scaledVec = new Vector3D((p.x * width / 2), (p.y * height / 2), p.z, p.w)  // scaled with respect to width and height

    scaledVec


  override def toString: String = s"x: $x, y: $y, z: $z"

