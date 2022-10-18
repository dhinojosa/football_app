package models


sealed trait Position

case object StrongForward extends Position {
    override def toString: String = "Strong Forward"
}
