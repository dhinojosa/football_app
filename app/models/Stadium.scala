package models

case class Stadium (id:Long, name:String, seats: Int, city:String, country:String)
object Stadium {
  def apply(name:String) = new Stadium(0L, name, 0, "London", "UK")
}
