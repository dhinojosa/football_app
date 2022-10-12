package services

import models.Team

import scala.collection.mutable.ListBuffer
import scala.util.Try;

class MemoryTeamService extends TeamService {
  val mutableList: ListBuffer[Team] = ListBuffer.empty

  override def findById(id: Long): Option[Team] =
    mutableList.find(t => t.id == id)

  override def create(team: Team): Unit = mutableList += team

  //try: 2
  //boolean: 1
  //list: 1
  override def update(team: Team): Try[Team] = {
    Try(mutableList.find(t => t.id == team.id).head)
      .map(t => {
        mutableList.filterInPlace(t => t.id != team.id).addOne(team); team
      })
  }

  override def findAll(): List[Team] = mutableList.toList

  override def findByName(name: String): Option[Team] =
    mutableList.find(t => t.name == name)
}
