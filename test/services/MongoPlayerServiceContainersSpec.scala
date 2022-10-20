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
  }

  private def extractFromResult(r: InsertOneResult) = {
    org.mongodb.scala.model.Filters
      .equal("_id", r.getInsertedId.asInt64().longValue())
  }
}
