package models

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 27/12/12
 * Time: 15:09
 * To change this template use File | Settings | File Templates.
 */
abstract class Documet {

}

abstract class DocumentLine {
def getLines(order_id : Int) : List[DocumentLine]
//def insertLine : Int
}
