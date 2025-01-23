package app.logic3D

import main.Constants
import scala.collection.mutable.Buffer
import scala.math._


object CameraMatrix extends Matrix:
  //taken from https://computergraphics.stackexchange.com/a/7581 and modified slightly

  def dir = (Camera.dir).normalize
  def right = (dir.cross(Camera.up)).normalize
  def up = (right.cross(dir)).normalize

  def mat: List[List[Double]] =
    List(
      List(right.x, right.y, right.z, -(right.dot(Camera.pos))),
      List(   up.x,    up.y,    up.z,    -(up.dot(Camera.pos))),
      List(  dir.x,   dir.y,   dir.z,   -(dir.dot(Camera.pos))),
      List(      0,       0,       0,                       1)
      )

object rotationMatrix_cube extends RotationMatrix
object rotationMatrix_camera extends RotationMatrix

class RotationMatrix extends Matrix:
  var (x,y,z) = (0.0, 0.0, 0.0)

  def sinA = sin(x) // Alpha
  def cosA = cos(x)

  def sinB = sin(y) // Beta
  def cosB = cos(y)

  def cosG = cos(z) // Gamma
  def sinG = sin(z)
  //https://en.wikipedia.org/wiki/Rotation_matrix (General 3D rotations)

  // updateMat is called only when i,j,k,l is pressed i.e. when the camera is rotating
  def updateMat =
    mat = List[List[Double]](
    List(cosB*cosG, (sinA*sinB*cosG)-(cosA*sinG), (cosA*sinB*cosG)+(sinA*sinG), 0),
    List(cosB*sinG, (sinA*sinB*sinG)+(cosA*cosG), (cosA*sinB*sinG)-(sinA*cosG), 0),
    List(-sinB    , sinA*cosB                   , cosA*cosB                   , 0),
    List(0        , 0                           , 0                           , 1)
  )



  var mat = List[List[Double]](
    List(cosB*cosG, (sinA*sinB*cosG)-(cosA*sinG), (cosA*sinB*cosG)+(sinA*sinG), 0),
    List(cosB*sinG, (sinA*sinB*sinG)+(cosA*cosG), (cosA*sinB*sinG)-(sinA*cosG), 0),
    List(-sinB    , sinA*cosB                   , cosA*cosB                   , 0),
    List(0        , 0                           , 0                           , 1)
  )

object ProjectionMatrix extends Matrix:
  // taken from https://youtu.be/EqNcqBdrNyI?si=maeQ5Y4vBQP5Q3cB&t=1221
  val aspectR = Constants.CANVASHEIGHT.toDouble / Constants.CANVASWIDTH.toDouble
  val fov = 90.toRadians
  val tanfov = tan(fov.toDouble / 2.toDouble)
  val far = 100000
  val near = 0.01

  val mat = List(
    List(aspectR / tanfov, 0          , 0                 , 0                          ),
    List(0               ,(1 / tanfov), 0                 , 0                          ),
    List(0               , 0          , far / (far - near),(-far * near) / (far - near)),
    List(0               , 0          , 1                 , 0                          )
  )

class normalMatrix(val mat: List[List[Double]]) extends Matrix

trait Matrix:
  def mat : List[List[Double]]
  /** Multiples a column vector with the current matrix
   * @param vec a column vector that will be multiplied with the current matrix
   * @return the multiplied column vector   
   * */
  def vecMultiply(vec: Vector3D) =
     Vector3D(
      (mat(0)(0) * vec.x + mat(0)(1) * vec.y + mat(0)(2) * vec.z + mat(0)(3) * vec.w),
      (mat(1)(0) * vec.x + mat(1)(1) * vec.y + mat(1)(2) * vec.z + mat(1)(3) * vec.w),
      (mat(2)(0) * vec.x + mat(2)(1) * vec.y + mat(2)(2) * vec.z + mat(2)(3) * vec.w),
      (mat(3)(0) * vec.x + mat(3)(1) * vec.y + mat(3)(2) * vec.z + mat(3)(3) * vec.w)
    )
  
  /** Multiples a column vector with the current matrix and divides all of the x,y,z values with the w value
   * @param vec a column vector that will be multiplied with the current matrix and divided by w
   * @return the multiplied column vector with xyz divided by w
   * */
  def vecMultiplyDivideW(vec: Vector3D) =
    val vector = vecMultiply(vec)
    if vector.w != 0.toDouble then
      vector.x = vector.x / vector.w
      vector.y = vector.y / vector.w
      vector.z = vector.z / vector.w
    vector
  
  /** 
   * Multiplies the current matrix with another 4x4 matrix
   * @param m2 a 4x4 matrix
   * @return the multiplied 4x4 matrix */
  def mat4x4Multiply(m2: Matrix) =
    // Check if the matrix is 4x4
    if this.mat.length == 4 && m2.mat.length == 4 then
      // Array used to store the rows of the multiplied matrix
      val finalArray = new Array[Array[Double]](4)

      // matrix multiplication:
      for k <- 0 to 3 do
        val array = new Array[Double](4)
        for i <- 0 to 3 do
          var total = 0.0
          for j <- 0 to 3 do

            total += mat(k)(j) * m2.mat(j)(i)
          array(i) = total
        finalArray(k) = array

      new normalMatrix(finalArray.map(k => k.toList).toList)
    // Matrix wasn't 4x4
    else throw new Error("matrix wasn't 4x4")



// used to test that the mat4x4 works
class testMatrix(val mat: List[List[Double]]) extends Matrix
