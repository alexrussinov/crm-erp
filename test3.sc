import java.io.File

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 30/08/13
 * Time: 22:29
 * To change this template use File | Settings | File Templates.
 */
val a =List(1,2,3,4,5)

val b =List(1,1,1,5,5,5,5,5,5,5,5,3,3,3,3,3,4,4,4,2,2,2)


b.foldLeft(Map[Int,Int]() withDefaultValue 0){
  (x,m)=> x+(m->(1+x(m)))
}


 Map(1->1)+(1->2)