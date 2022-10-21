package services

import com.dimafeng.testcontainers.{ForAllTestContainer, MongoDBContainer}
import models.{Stadium, Team}
import org.mongodb.scala.{Document, MongoClient}
import org.scalacheck.{Arbitrary, Gen}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

object MongoPlayerServiceContainersSpec {}

class MongoPlayerServiceContainersSpec
    extends PlaySpec
    with ForAllTestContainer
    with ScalaCheckPropertyChecks { //Include ScalaCheckPropertyChecks is included so that we can perform property testing

  @Override
  val container: MongoDBContainer = new MongoDBContainer()

  "MongoDBTeamService" must {
    "findById and include a stadium" in {
      val mongoClient: MongoClient =
        MongoClient(container.container.getConnectionString)
      val mongoDatabase = mongoClient.getDatabase("my_company")
      val teamCollection = mongoDatabase.getCollection("teams")

      val stadium = Stadium(203L, "Sweet Tea Stadium", 200, "Rochester", "USA")
      val stadiumService = new MongoStadiumService(mongoDatabase)
      val _ = stadiumService.create(stadium)

      val team = Team(20L, "Rocking Robins", stadium)
      val document = Document.apply(
        "_id" -> team.id,
        "name" -> team.name,
        "stadium" -> stadium.id
      )

      val teamService = MongoTeamService(mongoDatabase)

      val getId = teamCollection
        .insertOne(document)
        .head()

      val eventualMaybeTeam =
        getId
          .map(r => r.getInsertedId.asInt64().longValue())
          .flatMap(id => teamService.findById(id))

      import scala.concurrent.duration._

      val await: Option[Team] = Await.result(eventualMaybeTeam, 3.seconds)
      await must contain(team)
    }

    "Create a stadium" in {
      val genStadium: Gen[Stadium] = for {
        id <- Gen.posNum[Int]
        name <- Gen.alphaStr.suchThat(_.nonEmpty)
        seats <- Gen.choose(3, 20000)
        city <- Gen.alphaStr.suchThat(_.nonEmpty)
        country <- Gen.alphaStr.suchThat(_.nonEmpty)
      } yield (Stadium(id, name, seats, city, country))


      forAll(genStadium)((s:Stadium) => {
        println(s)

      })
    }

    "findAll and include a stadium" in {

      val mongoClient: MongoClient =
        MongoClient(container.container.getConnectionString)
      val mongoDatabase = mongoClient.getDatabase("my_company")

      val genStadium = for {
        id <- Gen.posNum[Int]
        name <- Gen.alphaStr.suchThat(_.nonEmpty)
        seats <- Gen.choose(3, 20000)
        city <- Gen.alphaStr.suchThat(_.nonEmpty)
        country <- Gen.alphaStr.suchThat(_.nonEmpty)
      } yield (Stadium(id, name, seats, city, country))

      val stadiumService = new MongoStadiumService(mongoDatabase)
      val teamService = MongoTeamService(mongoDatabase)

      implicit val arbitraryStadium: Arbitrary[Stadium] =
        Arbitrary[Stadium](genStadium)

      forAll(Gen.nonEmptyListOf(genStadium))((s: List[Stadium]) => {
        whenever(s.nonEmpty) {
          println(s)
          s.foreach(stadiumService.create)
          val genTeam: Gen[Team] = for {
            id <- Gen.posNum[Int]
            name <- Gen.alphaStr
            stadium <- Gen.oneOf(s)
          } yield (Team(id, name, stadium))

          implicit val arbitraryTeam: Arbitrary[Team] =
            Arbitrary[Team](genTeam)

          forAll((t: List[Team]) => {
            whenever(t.nonEmpty) {
              println(t)
              t.foreach(teamService.create)
              teamService
                .findAll()
                .map(result => result.size)
                .foreach(s => s mustBe (t.size))
              teamService.deleteAll()
            }
          })
        }
      })
    }
  }
}
