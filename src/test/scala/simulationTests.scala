import app.logic3D.*
import app.logic3D.{Vector3D, testMatrix}
import math.sqrt

import collection.mutable.Stack
import org.scalatest.*
import flatspec.*
import matchers.*

// app.logic2D
import app.logic2D._

class ExampleSpec extends AnyFlatSpec with should.Matchers {

  "a matrix" should "return the correct vector when the vecMultiplyDivideW is called" in {
    val vec = new Vector3D(1, 2, 3, 1)
    val mat = new testMatrix(
      List(
        List(1.0, 2.0, 3.0, 4.0),
        List(1.0, 2.0, 3.0, 4.0),
        List(1.0, 2.0, 3.0, 4.0),
        List(1.0, 2.0, 3.0, 4.0)
      )
    )
    assert(
      mat.vecMultiply(vec).asList === List(18, 18, 18, 18).map(k => k.toDouble)
    )
  }
  it should "return the correct Matrix when the Mat4x4Multiply is called" in {
    val A = new testMatrix(
      List(
        List(4.0, 3.0, 2.0, 1.0),
        List(6.0, 5.0, 4.0, 78.0),
        List(34.0, 12.0, 54.0, 34.0),
        List(65.0, 23.0, 65.0, 12.0)
      )
    )
    val B = new testMatrix(
      List(
        List(8.0, 15.0, 54.0, 32.0),
        List(54.0, 125.0, 53.0, 36.0),
        List(1.0, 2.0, 3.0, 4.0),
        List(78.0, 135.0, 542.0, 542.0)
      )
    )
    val correctAB = testMatrix(
      List(
        List(274, 574, 923, 786),
        List(6406, 11253, 42877, 42664),
        List(3626, 6708, 21062, 20164),
        List(2763, 5600, 11428, 9672)
      )
    )
    val correctAC = testMatrix(
      List(
        List(3101, 22822, 3889, 6226),
        List(38249, 353153, 9341, 204344),
        List(36682, 257080, 27648, 130244),
        List(38820, 256058, 49783, 98072)
      )
    )

    val C = new testMatrix(
      List(
        List(64.0, 1215.0, 543.0, 532.0),
        List(657.0, 4321.0, 543.0, 236.0),
        List(221.0, 432.0, 23.0, 424.0),
        List(432.0, 4135.0, 42.0, 2542.0)
      )
    )

    assert(A.mat4x4Multiply(B).mat === correctAB.mat)
    assert(A.mat4x4Multiply(C).mat === correctAC.mat)

  }

  "a vec3D" should "return the correct 'aslist' list" in {
    val vec = new Vector3D(1, 2, 3, 4)
    assert(vec.asList === List(1, 2, 3, 4))
  }
  "vector3D normalize" should "return the correct values" in {
    val vec = new Vector3D(123, 321, 543, 4)
    val correct = vec / (sqrt(123*123 + 321*321 + 543*543))
    assert(vec.normalize === correct)
  }
  "vector3D truncate" should "function as expected" in {
    val vec = new Vector3D(541,765,234).truncate(5)
    val truncated = vec.normalize * 5
    assert(vec === truncated)
  }

  "vector3D length" should "function as expected" in {
    val vec = new Vector3D(541,765,234)
    val length = sqrt(541*541 + 765*765 + 234*234)
    assert(vec.length === length)
  }
  
  

  "vector3D crossproduct" should "return the correct vector when called with positive vectors" in {

    val A = new Vector3D(543, 234, 1235)
    val B = new Vector3D(432, 321, 327)
    val result = A.cross(B)

    val correctX = new Vector3D(-319917, 355959, 73215).x
    val correctY = new Vector3D(-319917, 355959, 73215).y
    val correctZ = new Vector3D(-319917, 355959, 73215).z
    assert(result.x === correctX)
    assert(result.y === correctY)
    assert(result.z === correctZ)
  }

  "a boid " should "move forward when calling tick()" in {

    val t = new Boid3D(new Vector3D(0,0,0))
    t.tick()
    assert((t.position.x, t.position.y) == (0, 0 ))
  }

  "grid " should " move all of the boids" in {
    val t1 = new Boid3D(new Vector3D(0,0,0))
    val pos1 = new Vector3D(t1.position.x, t1.position.y, t1.position.z)
    val t2 = new Boid3D(new Vector3D(1,1,1))
    val pos2 = new Vector3D(t2.position.x, t2.position.y, t2.position.z)
    val t3 = new Boid3D(new Vector3D(2,2,2))
    val pos3 = new Vector3D(t3.position.x, t3.position.y, t3.position.z)
    Grid3D.objects = List(t1,t2,t3)
    Grid3D.tick()
    assert(t1.position != pos1)
    assert(t2.position != pos2)
    assert(t3.position != pos3)
  }
}
