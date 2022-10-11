package models

import org.scalatestplus.play.PlaySpec

class TeamSpec extends PlaySpec {
  "A team" must {
    "return the name that I provide it" in {
      val team = Team(12L, "Leeds", Stadium("Elland Road"))
      team.name mustBe ("Leeds")
    }
  }
}
