package app.logic3D

import main.Constants

import scala.collection.mutable.Buffer

object Grid3D:
  var objects = CoordinateReader3D.initialBoidList
  val predators = Buffer[Boid3D]()
  // used to store the saved presets as in (preset, name_of_the_preset)
  val savedPresets = Buffer[(Preset, String)]()

  def getObjects = objects
  
  
  /** Stops all the 3D threads, will be called when the application is closed */
  def stopThreads() = threads.foreach(_.stop())

  /**
   * Suspend the separately created threads that are used in 3D,
   * the suspended threads are the ones that are stored in 'threads'
   * this will be called when the simulation is switched from 3D to 2D
   * */
  def suspendThreads() = threads.foreach(_.suspend)

  /**
   *  Resume all of the threads that are stored in 'threads' Buffer
   *  This will be called when the simulation is switched from 2D back to 3D
   *  */
  def resumeThreads() = threads.foreach(_.resume())


  val threads = Buffer[GeneralThread]()
  val objectsSplit = objects.grouped(((objects.size / Constants.SystemCores.toDouble) + 1).toInt) // round up

  // start all of the threads initially:
  // Boid related threads:
  for i <- objectsSplit do
    val t = new GridThread(i)
    threads += t
    t.start()
  // Cube related thread:
  threads += CubeThread
  CubeThread.start()
  
  var defaultTriangles = CoordinateReader3D.defaultObjTriangles
  val allTriangles = CoordinateReader3D.allObjTriangles


  def updatePreset(p: Preset) =
    updateMass(p.mass)
    updateMaxForce(p.max_force)
    updateMaxSpeed(p.max_speed)
    updateCohesion(p.cohesion.multiplier)
    updateCohesionRadius(p.cohesion.radius)
    updateAlignment(p.aligment.multiplier)
    updateAlignmentRadius(p.aligment.radius)
    updateSeparation(p.separation.multiplier)
    updateSeparationRadius(p.separation.radius)

  def getTriangles = defaultTriangles


  def updateMass(value: Double) =             getObjects.foreach(k => k.updateMass(value))
  def updateMaxForce(value: Double) =         getObjects.foreach(k => k.updateMaxForce(value))
  def updateMaxSpeed(value: Double) =         getObjects.foreach(k => k.updateMaxSpeed(value))

  def updateSeparation(value: Double) =       getObjects.foreach(k => k.updateSeparation(value))
  def updateSeparationRadius(value: Double) = getObjects.foreach(k => k.updateSeparationRadius(value))

  def updateAlignment(value: Double) =        getObjects.foreach(k => k.updateAlignment(value))
  def updateAlignmentRadius(value: Double) =  getObjects.foreach(k => k.updateAlignmentRadius(value))

  def updateCohesion(value: Double) =         getObjects.foreach(k => k.updateCohesion(value))
  def updateCohesionRadius(value: Double) =   getObjects.foreach(k => k.updateCohesionRadius(value))

  def tick() = objects.foreach(_.tick())
