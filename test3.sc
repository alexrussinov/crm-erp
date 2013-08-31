import java.io.File

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 30/08/13
 * Time: 22:29
 * To change this template use File | Settings | File Templates.
 */
val file = new File("public/images/products/test.jpg")

val a = Option(Option(file))

a.map(f=>f.get.getPath)