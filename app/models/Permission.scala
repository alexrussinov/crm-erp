package models

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 12/01/13
 * Time: 21:01
 * To change this template use File | Settings | File Templates.
 */

sealed trait Permission

case object Administrator extends Permission

case object NormalUser extends Permission


