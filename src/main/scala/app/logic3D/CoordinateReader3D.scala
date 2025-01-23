package app.logic3D

import java.io.File
import java.io.FileWriter

import scala.io.Source
import scala.collection.mutable.Buffer


import io.circe._
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import io.circe.syntax._
import cats.syntax.either._


object CoordinateReader3D:
  // Generating the boids from the .txt file where each line represents a single boid's coordinates

  private val path = "src/main/resources/initialCoordinates/initialCoordinates3D.txt"
  private val file = Source.fromFile(path)
  private val eachLine = file.getLines().toList
  private val coordinateStrings = eachLine.filter(_.nonEmpty).filter(k => k.head.isDigit || k.startsWith("-") )
  private val coordinatePairs = coordinateStrings.map(s => s.split(","))

  val initialVector3DList =
    coordinatePairs.map(coordinates => new Vector3D(coordinates(0).toDouble, coordinates(1).toDouble, coordinates(2).toDouble))

  val initialBoidList = initialVector3DList.map(vector => new Boid3D(vector))

  // Generating the defaultTriangles from the obj files:

  // Inner Buffer[Triangle] represents objFile's defaultTriangles, String represents the filename
  val allObjTriangles = Buffer[(Buffer[Triangle], String)]()

  // the default .obj file that is drawn initially
  val defaultObjFileName = "cube"

  lazy val defaultObjTriangles: Buffer[Triangle] =
    allObjTriangles(allObjTriangles.map(_._2).indexOf(defaultObjFileName))(0)

  val objFiles = new File("src/main/resources/objFiles").listFiles()

  /** Generates the defaultTriangles for each file in the objFiles folder and places them into the allObjTriangles
    * buffer
    */
  def generateAllTriangles =
    for i <- objFiles do
      try
        val objPath = i.getPath
        val name_of_the_obj_file =
          i.getName.split('.').head // doesn't contain the file extension ("test.obj" => "test")
        val objFile = Source.fromFile(objPath)
        val objEachLine = objFile.getLines().toList
        val vertices = objEachLine
          .filter(_.startsWith("v"))
          .map(
            _.split(" ")
              .drop(1)
              .filter(_.nonEmpty)
              .map(_.toDouble)
          )
        val faces = objEachLine
          .filter(_.startsWith("f"))
          .map(
            _.split(" ")
              .drop(1)
              .filter(_.nonEmpty)
              .map(_.toInt - 1)
          )
        def generateTriangles =
          val triangles = Buffer[Triangle]()
          for f <- faces do
            val v = vertices
            val p1 = new Vector3D(v(f(0))(0), v(f(0))(1), v(f(0))(2))
            val p2 = new Vector3D(v(f(1))(0), v(f(1))(1), v(f(1))(2))
            val p3 = new Vector3D(v(f(2))(0), v(f(2))(1), v(f(2))(2))
            triangles += new Triangle(p1, p2, p3)
          triangles

        val triangles = (generateTriangles, name_of_the_obj_file)

        // add the pair (Buffer[Triangles], name_of_the_obj_file) to the "allObjTriangles" Buffer
        allObjTriangles += triangles
      catch case e => throw e

  generateAllTriangles



object JSONMaker:
  
 
  val presetFolderPath = "src/main/resources/presets"
  
  /**
   *  Saves a 'preset' object as a .json file to a folder (address: presetFolderPath)
   * @param preset the 'Preset' object to be stored as a json
   * @param file_name the name of the file without the file extension
   * */
  def savePresetAsJSON(preset: Preset, file_name: String) =
    println(s"Saved the preset to ${presetFolderPath}/$file_name.json")
    val new_file_path = s"${presetFolderPath}/$file_name.json"
    val new_file = new File(new_file_path)
    val w = new FileWriter(new_file)
    val jsonString: String = preset.asJson.spaces2
    try
      if jsonString != "" then
        w.write(jsonString)
    catch
      case e => println(e)
    finally
      w.close()
  
  
  /** 
   * Reads the .json presets stored at presetFolderPath, stores the 
   * 'Preset' objects at Grid3D.savedPresets and finally returns the Grid3D.savedPresets */
  def readJSONPresets(): Buffer[(Preset, String)] =
    val presetFolder = new File(presetFolderPath)
    for i <- presetFolder.listFiles() do
      val current_file_name = i.getName.split("\\.").head
      val file_extension = i.getName.split("\\.").last
      if file_extension == "json" then // check that it's a json
        val iterator = Source.fromFile(i).getLines()
        var dataString = ""
        for j <- iterator do
          dataString += j
        lazy val jsonString = parser.parse(dataString)

        val JsonToPreset = jsonString.flatMap(k => k.as[Preset]) match
          case Right(preset) =>
            Grid3D.savedPresets += ((preset, current_file_name)) // save the preset to Grid3D
          case Left(e) => println(s"Error at readJsonPresets() in file ${i.getName}: ${e}")
    // finally return the buffer containing all the saved presets
    Grid3D.savedPresets
  
  /** 
   * return true if the preset folder contains a file with the name
   * @name name of the file without the file extension */
  def presetFolderContains(name: String): Boolean =
    new File(presetFolderPath)
      .listFiles
      .map(_.getName.split("\\.").head) // drop the extensions
      .contains(name)
    




