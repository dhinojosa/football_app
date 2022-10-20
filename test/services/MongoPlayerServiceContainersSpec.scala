package services

import com.dimafeng.testcontainers.{ForAllTestContainer, MongoDBContainer}
import models.{Stadium, Team}
import org.mongodb.scala.model.Aggregates
import org.mongodb.scala.result.InsertOneResult
import org.mongodb.scala.{Document, MongoClient}
import org.scalatestplus.play.PlaySpec

import java.util
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

class MongoPlayerServiceContainersSpec
    extends PlaySpec
    with ForAllTestContainer {

  @Override
  val container: MongoDBContainer = new MongoDBContainer()

  "MongoDBTeamService" must {
    "be able to an aggregate and put into a temporary variable" in {
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

      val response = teamCollection.insertOne(document)
      response
        .head()
        .foreach(id => {
          id mustNot be(null)
        })

      case class TeamStadiumView(
          teamId: Long,
          teamName: String,
          stadiumName: String,
          stadiumId: Long
      )

      val aggregated = teamCollection.aggregate(
        Seq(
          Aggregates
            .lookup("stadiums", "stadium", "_id", "stadiumArray")
        )
      )

      val eventualMaybeView = aggregated
        .map(d => {
          TeamStadiumView(
            d.getLong("_id"),
            d.getString("name"),
            getFromMongoList(d).asInstanceOf[String],
            d.getLong("stadium")
          )
        })
        .toSingle()
        .headOption()
      import scala.concurrent.duration._
      val await = Await.result(eventualMaybeView, 3.seconds)
      println(await)
    }
  }

  private def getFromMongoList(d: Document) = {
    import scala.jdk.CollectionConverters._
    d
      .getList("stadiumArray", classOf[util.Map[_, _]])
      .asScala
      .head
      .get("name")
  }

  private def extractFromResult(r: InsertOneResult) = {
    org.mongodb.scala.model.Filters
      .equal("_id", r.getInsertedId.asInt64().longValue())
  }
}
