import java.io.File
import lib.{CustomIO, Convertion}

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 22/07/13
 * Time: 02:15
 * To change this template use File | Settings | File Templates.
 */

CustomIO.getFileTree(new File("imports/")) filter (_.isFile) map (f=>Map(f.getName->f.getPath))




