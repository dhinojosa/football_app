package services

import models.Team

import scala.collection.mutable.ListBuffer
import scala.util.Try;

class MemoryTeamService extends TeamService {
  val mutableList: ListBuffer[Team] = ListBuffer.empty

  override def findById(id: Long): Option[Team] = mutableList.find(t => t.id == id)

  override def create(team: Team): Unit = mutableList += team

  //try: 2
  //boolean: 1
  //list: 1
  override def update(team: Team): Try[Team] = {
      //does the team name already exist if so deny it
    // does the team id already exist if so deny it
//      Try(mutableList.find(t => t.id == team.id).get)
//      Try(mutableList.find(t => t.name == team.name).get)
//      mutableList.filterInPlace(t => t.id != team.id).addOne(team)
    Try(team)
  }

    override def findAll(): List[Team] = mutableList.toList

  override def findByName(name: String): Option[Team] = mutableList.find(t => t.name == name)
}
