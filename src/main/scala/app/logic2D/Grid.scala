package app.logic2D
import main.Constants

import java.nio.DoubleBuffer
import scala.collection.mutable.Buffer

object Grid:
  var objects = CoordinateReader.initialBoidList.toBuffer
  def getObjects = objects


  def updateSeparation(value: Double) =       getObjects.foreach(k => k.updateSeparation(value))
  def updateSeparationRadius(value: Double) = getObjects.foreach(k => k.updateSeparationRadius(value))
  
  def updateAlignment(value: Double) =       getObjects.foreach(k  => k.updateAlignment(value))
  def updateAlignmentRadius(value: Double) = getObjects.foreach(k  => k.updateAlignmentRadius(value))
  
  def updateCohesion(value: Double) =       getObjects.foreach(k   => k.updateCohesion(value))
  def updateCohesionRadius(value: Double) = getObjects.foreach(k   => k.updateCohesionRadius(value))
  
  def updateMass(value: Double) = Grid.objects.foreach(_.updateMass(value))
  def updateMaxSpeed(value: Double) = Grid.objects.foreach(_.updateMaxSpeed(value))
  def updateMaxForce(value: Double) = Grid.objects.foreach(_.updateMaxForce(value))
  

  
  def tick() = objects.foreach(_.tick())
