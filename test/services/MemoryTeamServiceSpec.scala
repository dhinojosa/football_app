package services

import models.{Stadium, Team}
import org.scalatestplus.play.PlaySpec

import scala.util.{Failure, Success}

class MemoryTeamServiceSpec extends PlaySpec {
  "MemoryTeamService" must {
    "return the size of the list after I create the team" in {
      val memoryTeamService = new MemoryTeamService();
      val team = Team(12L, "Arsenal", Stadium("Emirates Stadium"))
      memoryTeamService.create(team)
      memoryTeamService.findAll().size mustBe (1)
    }
    "find a team by an id and return that team if it is in there" in {
      val memoryTeamService = new MemoryTeamService();
      val arsenal = Team(12L, "Arsenal", Stadium("Emirates Stadium"))
      val chelsea = Team(10L, "Chelsea", Stadium("Stamford Bridge"))
      memoryTeamService.create(arsenal)
      memoryTeamService.create(chelsea)
      val result = memoryTeamService.findById(10L)
      result mustBe (Some(chelsea))
    }
    "find a team by an id that doesn't exist" in {
      val memoryTeamService = new MemoryTeamService();
      val arsenal = Team(12L, "Arsenal", Stadium("Emirates Stadium"))
      val chelsea = Team(10L, "Chelsea", Stadium("Stamford Bridge"))
      memoryTeamService.create(arsenal)
      memoryTeamService.create(chelsea)
      val result = memoryTeamService.findById(14L)
      result mustBe (Option.empty[Team])
    }
    "find a team by a name and return that team if it is in there" in {
      val memoryTeamService = new MemoryTeamService();
      val arsenal = Team(12L, "Arsenal", Stadium("Emirates Stadium"))
      val chelsea = Team(10L, "Chelsea", Stadium("Stamford Bridge"))
      memoryTeamService.create(arsenal)
      memoryTeamService.create(chelsea)
      val result = memoryTeamService.findByName("Arsenal")
      result mustBe (Some(arsenal))
    }
    "be able to update a team from the service" in {
      val memoryTeamService = new MemoryTeamService();
      val arsenal = Team(12L, "Arsenal", Stadium("Emirates Stadium"))
      val chelsea = Team(10L, "Chelsea", Stadium("Stamford Bridge"))
      memoryTeamService.create(arsenal)
      memoryTeamService.create(chelsea)
      val maybeTeam = memoryTeamService.findById(12L).get
      val updated = arsenal.copy(stadium = Stadium("Ashburton Grove"))
      println(updated)
      memoryTeamService.update(updated)
      val result = memoryTeamService.findById(12L).get
      result.stadium mustBe (Stadium("Ashburton Grove"))
      memoryTeamService.findAll().size mustBe (2)
    }
    "be able to update a team that is not in the service" in {
      val memoryTeamService = new MemoryTeamService();
      val arsenal = Team(12L, "Arsenal", Stadium("Emirates Stadium"))
      val chelsea = Team(10L, "Chelsea", Stadium("Stamford Bridge"))
      memoryTeamService.create(arsenal)
      memoryTeamService.create(chelsea)
      val result = memoryTeamService.update(Team(30L, "Lewes FC", Stadium("The Dripping Pan")))
      result mustBe(Failure(new NoSuchElementException("Team is not in service")))
    }
  }
}
