package app.logic3D
import app.scenes.*
import main.Constants
import scalafx.beans.property.ReadOnlyProperty

def tps = Constants.TPS
/**
 *  A thread class that calls the tick() and update3D() for a list 
 * of boids in a while loop until the thread is manually shut off or set to idle
 * @param list the list that contains the boids whose tick() 
 *             and update3D() methods will be called in a (while true) loop  */

class GridThread(list: Seq[Boid3D]) extends GeneralThread:
  var fps = 0.0
  var lastTime = System.nanoTime()

  override def run() =
    var c = System.currentTimeMillis()
    while true do
      // println(c + "now:" + System.currentTimeMillis())
      // cap the update frequency to the limited 'tick per second' set by Constant.Boid
      if (System.nanoTime() - lastTime)/1e9 > (1/tps) then
        list.foreach(k =>
          k.tick()
          k.update3D()
        )
        // update fps
        fps = 1/((System.nanoTime() - lastTime)/1e9)
        lastTime = System.nanoTime()

/** A thread object that updates the drawn .obj file triangles in a while true loop
 * the .obj file (=triangles) is set by canvasScene3D.currentTriangles. 
 * Will run until the thread is manually shut off*/
object CubeThread extends GeneralThread:
  var fps = 0.0
  var lastTime = System.nanoTime()

  override def run() =
    while true do
      // println("work") // https://meta.stackoverflow.com/questions/269174/questions-about-threadloop-not-working-without-print-statement
      // fixed by using @volatile in main.main.canvasScene3D

      // update each triangles '3D points':
        // cap the update frequency to the limited 'tick per second' set by Constant.Boid
      if (System.nanoTime() - lastTime)/1e9 > (1/tps) then
        // the canvasScene3D is initially null
        if main.main.canvasScene3D != null then
          main.main.canvasScene3D.getCurrentTriangles
            .foreach(triangle => triangle.update3D())

          // update fps (or actually ticks per second)
          fps = 1/((System.nanoTime() - lastTime)/1e9)
          lastTime = System.nanoTime()

trait GeneralThread extends Thread:
  def start(): Unit
  def run(): Unit
  var fps: Double