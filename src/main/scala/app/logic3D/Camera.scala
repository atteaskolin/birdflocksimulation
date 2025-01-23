package app.logic3D


object Camera:

  val speed = 0.05 // speed at which the camera moves in the grid
  val rotSensitivity = 0.03

  var pos = new Vector3D(0, 0, 0) // the camera location
  val dirInitial = new Vector3D(0,0,-1) // the direction the camera points at (initially -z axis)
  val up = new Vector3D(0,1,0)   // the "up" vector with respect to dirInitial

  def getPos = pos
  def getDir = rotationMatrix_camera.vecMultiply(dirInitial)
  def getUp = up

  // the direction the camera points at currently
  def dir = rotationMatrix_camera.vecMultiply(dirInitial)

