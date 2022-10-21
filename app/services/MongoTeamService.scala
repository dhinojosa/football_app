package services

import models.{Stadium, Team}
import org.mongodb.scala.model.{Aggregates, Filters}
import org.mongodb.scala.{
  AggregateObservable,
  Document,
  MongoCollection,
  MongoDatabase
}

import scala.concurrent.Future
import scala.util.Try

case class MongoTeamService(mongoDatabase: MongoDatabase)
    extends AsyncTeamService {

  val teamCollection: MongoCollection[Document] =
    mongoDatabase.getCollection("teams")

  implicit class MyDocumentWrapper(document: Document) {
    def getArray(key: Any): List[Map[Any, Any]] = {
      import scala.jdk.CollectionConverters._
      document
        .getList(key, classOf[java.util.Map[_, _]])
        .asScala
        .toList
        .map(_.asScala.toMap)
    }
  }

  override def findById(id: Long): Future[Option[Team]] = {
    val aggregated: AggregateObservable[Document] =
      teamCollection.aggregate(
        Seq(
          Aggregates.`match`(Filters.equal("_id", 20L)),
          Aggregates
            .lookup("stadiums", "stadium", "_id", "stadiumArray")
        )
      )
    aggregated
      .map(documentToTeam)
      .toSingle()
      .headOption()
  }

  private def documentToTeam(d: Document) = {
    Team(
      d.getLong("_id"),
      d.getString("name"),
      mapToStadium(d.getArray("stadiumArray").head)
    )
  }

  def mapToStadium(map: Map[Any, Any]): Stadium = {
    val id = map.getOrElse("_id", -1L).asInstanceOf[Long]
    val name = map.getOrElse("name", "").asInstanceOf[String]
    val seats = map.getOrElse("seats", 0).asInstanceOf[Int]
    val city = map.getOrElse("city", "").asInstanceOf[String]
    val country = map.getOrElse("country", "").asInstanceOf[String]
    Stadium(id, name, seats, city, country)
  }

  override def create(stadium: Team): Future[Long] = {
    teamCollection.insertOne(
      Document(
        "_id" -> stadium.id,
        "name" -> stadium.name,
        "seats" -> stadium.stadium.id
      )
    ).map(r => r.getInsertedId.asInt64().longValue()).toSingle().head()
  }

  override def update(stadium: Team): Future[Try[Team]] = ???

  override def findAll(): Future[List[Team]] = {
    teamCollection.find().map(d => documentToTeam(d)).collect().map(s=>s.toList).toSingle().head()
  }

  override def findByName(name: String): Future[Option[Team]] = ???

  protected[services] def deleteAll() = {
    teamCollection.deleteMany(Filters.empty()).subscribe(d => println(d))
  }
}
