import main.Constants
import scala.util.Random
import scala.collection.mutable.Buffer

/* 
- Change numberOfBoids to the desired number
- run createInitialCoordinates2D/3D
- copypaste the printed values into resources/initialCoordinates/InitialCoordinates2D/3D.txt
 */

val numberOfBoids: Int = 500

@main def createInitialCoordinates2D() =
  for i <- 0 until numberOfBoids do
    println(
      s"${Random.nextInt(Constants.CANVASWIDTH)},${Random.nextInt(Constants.CANVASHEIGHT)}"
    )

@main def createInitialCoordinates3D() =
  for i <- 0 until numberOfBoids do
    val b = Constants.Boid
    println(
      s"${Random.between(b.minX.toDouble, b.maxX.toDouble )}," +
      s"${Random.between(b.minY.toDouble, b.maxY.toDouble)}," +
      s"${Random.between(b.minZ.toDouble, b.maxZ.toDouble)}"
    )



