package services

import models.Team

import scala.collection.mutable.ListBuffer
import scala.util.Try;

object AlternateMemoryTeamService {
  def apply: AlternateMemoryTeamService = {
    new AlternateMemoryTeamService(ListBuffer.empty)
  }
}

class AlternateMemoryTeamService private (val teams:ListBuffer[Team]) extends TeamService {

  override def findById(id: Long): Option[Team] =
    teams.find(t => t.id == id)

  override def create(team: Team): Unit = teams += team

  //try: 2
  //boolean: 1
  //list: 1
  override def update(team: Team): Try[Team] = {
    Try(teams.find(t => t.id == team.id).head)
      .map(t => {
        teams.filterInPlace(t => t.id != team.id).addOne(team); t
      })
  }

  override def findAll(): List[Team] = teams.toList

  override def findByName(name: String): Option[Team] =
    teams.find(t => t.name == name)
}
