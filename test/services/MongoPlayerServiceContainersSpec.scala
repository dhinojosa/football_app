package services

import com.dimafeng.testcontainers.{ForAllTestContainer, MongoDBContainer}
import models.{Stadium, Team}
import org.mongodb.scala.model.Aggregates
import org.mongodb.scala.result.InsertOneResult
import org.mongodb.scala.{Document, MongoClient}
import org.scalatestplus.play.PlaySpec
import org.mongodb.scala.model.Filters.equal

import java.util
import scala.concurrent.ExecutionContext.Implicits.global

class MongoPlayerServiceContainersSpec
    extends PlaySpec
    with ForAllTestContainer {

  @Override
  val container: MongoDBContainer = new MongoDBContainer()

  "MongoDBTeamService" must {
    "be able to create a player using the team id" in {
      val mongoClient: MongoClient =
        MongoClient(container.container.getConnectionString)
      val mongoDatabase = mongoClient.getDatabase("my_company")
      val teamCollection = mongoDatabase.getCollection("teams")
      val stadiumCollection = mongoDatabase.getCollection("stadiums")

      val stadium = Stadium(203L, "Sweet Tea Stadium", 200, "Rochester", "USA")
      val stadiumService = new MongoStadiumService(mongoDatabase)
      val _ = stadiumService.create(stadium)

      val team = Team(20L, "Rocking Robins", stadium)
      val document = Document.apply(
        "_id" -> team.id,
        "name" -> team.name,
        "stadium" -> stadium.id
      )

      val response = teamCollection.insertOne(document)
      response
        .head()
        .foreach(id => {
          id mustNot be(null)
        })

      val aggregated = teamCollection.aggregate(
        Seq(
          Aggregates
            .lookup("stadiums", "stadium", "_id", "stadiumArray")
        )
      )

      import scala.jdk.CollectionConverters._
      case class TeamStadiumView(
          teamId: Long,
          teamName: String,
          stadiumName: String,
          stadiumId: Long
      )
      aggregated
        .map(d => {
          println("1: " + d)
          println("2: " + d.get("stadiumArray"))
          println("3: " + d.get("stadiumArray").get.getClass)
          val result = d
            .getList("stadiumArray", classOf[util.Map[_, _]])
            .asScala
          println("4: " + result)
          val stadiumName = result.head.get("name")
          println("5: " + stadiumName)
          TeamStadiumView(
            d.getLong("_id"),
            d.getString("name"),
              stadiumName.asInstanceOf[String],
            d.getLong("stadium")
          )
        })
        .subscribe(
          d => println(d),
          t => t.printStackTrace(),
          () => println("Done")
        )
    }

      "be able to an aggregate and put into a temporary variable" in {
          val mongoClient: MongoClient =
              MongoClient(container.container.getConnectionString)
          val mongoDatabase = mongoClient.getDatabase("my_company")
          val teamCollection = mongoDatabase.getCollection("teams")
          val stadiumCollection = mongoDatabase.getCollection("stadiums")

          val stadium = Stadium(203L, "Sweet Tea Stadium", 200, "Rochester",
              "USA")
          val stadiumService = new MongoStadiumService(mongoDatabase)
          val _ = stadiumService.create(stadium)

          val team = Team(20L, "Rocking Robins", stadium)
          val document = Document.apply(
              "_id" -> team.id,
              "name" -> team.name,
              "stadium" -> stadium.id
          )

          val response = teamCollection.insertOne(document)
          response
              .head()
              .foreach(id => {
                  id mustNot be(null)
              })

          val aggregated = teamCollection.aggregate(
              Seq(
                  Aggregates
                      .lookup("stadiums", "stadium", "_id", "stadiumArray")
              )
          )

          import scala.jdk.CollectionConverters._
          case class TeamStadiumView(
                                        teamId: Long,
                                        teamName: String,
                                        stadiumName: String,
                                        stadiumId: Long
                                    )
          aggregated
              .map(d => {
                  val stadiumName = d
                      .getList("stadiumArray", classOf[util.Map[_, _]])
                      .asScala.head.get("name")
                  TeamStadiumView(
                      d.getLong("_id"),
                      d.getString("name"),
                      stadiumName.asInstanceOf[String],
                      d.getLong("stadium")
                  )
              }).toSingle().headOption()
      }

    "must be able to lookup items for the team" in {
        val mongoClient: MongoClient =
            MongoClient(container.container.getConnectionString)
        val mongoDatabase = mongoClient.getDatabase("my_company")
        val teamCollection = mongoDatabase.getCollection("teams")
        val stadiumCollection = mongoDatabase.getCollection("stadiums")


        val stadium = Stadium(203L, "Sweet Tea Stadium", 200, "Rochester",
            "USA")
        val stadiumService = new MongoStadiumService(mongoDatabase)
        val _ = stadiumService.create(stadium)

        val team = Team(20L, "Rocking Robins", stadium)
        val document = Document.apply(
            "_id" -> team.id,
            "name" -> team.name,
            "stadium" -> stadium.id
        )

        val response = teamCollection.insertOne(document)
        response
            .head()
            .foreach(id => {
                id mustNot be(null)
            })


        mongoDatabase
            .getCollection("teams")
            .aggregate(
                List(
                    Aggregates.lookup("stadiums", "stadium", "_id", "stadiumDetails"),
                    Aggregates.out("temp")
                )
            )
            .toSingle()
            .subscribe(n => println(n), t => t.printStackTrace(), () => println("Done"));


            //.headOption()





//            .flatMap {
//                case Some(teamInfo) =>
//                    teamService
//                        .findById(id)
//                        .map {
//                            case Some(team) => Ok(
//                                views.html.team.show(team, teamInfo))
//                            case None => NotFound("Team not found")
//                        }
//                case None => Future(NotFound("Team not found"))
//            }

    }
  }




  private def extractFromResult(r: InsertOneResult) = {
    org.mongodb.scala.model.Filters
      .equal("_id", r.getInsertedId.asInt64().longValue())
  }
}
