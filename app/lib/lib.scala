package lib



/**
 * Project: ${PROJECT_NAME}
 * User: alex
 * Date: 09/03/13
 * Time: 18:00
 *
 */
object Convertion{
case class ParseOp[T](op: String => T)

implicit val popDouble = ParseOp[Double] (_.toDouble)
implicit val popInt = ParseOp[Int] (_.toInt)
def prse[T: ParseOp] (s: String) = {
try {
Some (implicitly[ParseOp[T]].op (s) )
}
catch {
case _ => None
}
}

}

object mat {
  def round (x : Double) : Double ={
    (math rint x * 100) / 100
  }
}


object CustomIO {
  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally { p.close() }
  }
}