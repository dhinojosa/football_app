package services

import models.Stadium

import scala.collection.mutable.ListBuffer
import scala.util.Try

class MemoryStadiumService extends StadiumService {
    val mutableList: ListBuffer[Stadium] = ListBuffer.empty

    override def findById(id: Long): Option[Stadium] =
        mutableList.find(t => t.id == id)

    override def create(team: Stadium): Unit = mutableList += team

    override def update(team: Stadium): Try[Stadium] = {
        Try(mutableList.find(t => t.id == team.id).head)
            .map(t => {
                mutableList.filterInPlace(t => t.id != team.id).addOne(team);
                team
            })
    }

    override def findAll(): List[Stadium] = mutableList.toList

    override def findByName(name: String): Option[Stadium] =
        mutableList.find(t => t.name == name)

    override def findByCountry(country: String): List[Stadium] =
        mutableList.filter(t => t.country == country).toList
}
