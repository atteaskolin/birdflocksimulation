package app.logic2D

import scala.io.Source

object CoordinateReader:
  private val path = "src/main/resources/initialCoordinates/initialCoordinates2D.txt"
  private val file = Source.fromFile(path)
  private val eachLine = file.getLines().toList
  private val coordinateStrings = eachLine.filter(_.nonEmpty).filter(_.head.isDigit)
  private val coordinatePairs = coordinateStrings.map(s => s.split(","))

  val initialVector2DList = coordinatePairs.map(coordinates =>
    new Vector2D(coordinates(0).toInt, coordinates(1).toInt)
  )

  val initialBoidList = initialVector2DList.map(vector => new Boid2D(vector))

//  @main def testi() = println(initialVector3DList)
