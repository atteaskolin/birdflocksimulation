package main

import app.logic3D.Vector3D


// Most of these constants were only used for testing
object Constants:
  // the amount of cores that will be used to update the boids
  val SystemCores = Runtime.getRuntime().availableProcessors() / 2

  val TPS = 200.0

  object Boid:
    val maxX = 0       // is used
    val minX = -2      // is used

    val maxY = 2       // is used
    val minY = 0       // is used

    val maxZ = 2       // is used
    val minZ = 0       // is used
    

  val CANVASHEIGHT = 1000         // is used
  val CANVASWIDTH = 1200          // is used
