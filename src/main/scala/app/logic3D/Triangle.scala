package app.logic3D

import main.Constants

class Triangle(val p1: Vector3D, val p2: Vector3D, val p3: Vector3D):
  override def toString: String =
    s"-------------------------\nTRIANGLE: \n$p1\n$p2\n$p3"

  // the points that will be updated by a separate thread. Initially (0.0, 0.0)
  var points3D: Seq[Vector3D] = Seq[Vector3D]()

  /**  __Should only be used by a separate thread__ */


  def update3D() =
    points3D = this
      .offsetZ
      .cameraView
      .scaledPerspective
      .toSeq
      //.map(t => (t.x, t.y))

  def toSeq = Seq(p1, p2, p3)

  /** Creates a new set of vectors that correspond to the p1,p2,p3 but with perspective applied
   * The vectors are inside the "image space" that is normalized in a way that all
   * the x,y,z values are between [-1, 1]
   * @return Seq containing the new normalized perspective projection vectors */
  def perspective = new Triangle(p1.perspective, p2.perspective, p3.perspective)

  /** Creates a new set of vectors that correspond to the previous
   *  p1,p2,p3 but with perspective (perspective projection) and scaling applied to the x and y values.
   *  The scaling is done with respect to the canvas width and height
   *  {{{ (width and height values are located in main/Constants object) }}}
   *  @return Seq containing the new scaled perspective vectors */
  def scaledPerspective = new Triangle(p1.scaledPerspective, p2.scaledPerspective, p3.scaledPerspective)

  def cameraView = new Triangle(p1.cameraView, p2.cameraView, p3.cameraView)

  def rotated = new Triangle(p1.rotate, p2.rotate, p3.rotate)

  def offsetZ = new Triangle(p1.offsetZ, p2.offsetZ, p3.offsetZ)



  def normal =
    val p12 = p1 - p3
    val p13 = p2 - p3
    //println(s"wihtout normalizing: ${p12.cross(p13)}")
    //println(s"normalizing: ${p12.cross(p13).normalize}")
    p12.cross(p13)

  def normalScaledPers = normal.scaledPerspective
