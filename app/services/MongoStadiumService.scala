package services

import models.Stadium
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.{Document, MongoClient}

import scala.concurrent.Future
import scala.util.Try

class MongoStadiumService extends AsyncStadiumService {

  val mongoClient: MongoClient = MongoClient(
    "mongodb://mongo-root:mongo-password@localhost:" + 27017
  )
  val myCompanyDatabase = mongoClient.getDatabase("football_app")
  val stadiumCollection = myCompanyDatabase.getCollection("stadiums")

  override def findById(id: Long): Future[Option[Stadium]] = {
    stadiumCollection
      .find(equal("_id", id))
      .map { d =>
        documentToStadium(d)
      }
      .toSingle()
      .headOption()
  }

  override def create(stadium: Stadium) = {
    val document: Document = stadiumToDocument(stadium)

    stadiumCollection
      .insertOne(document)
        .map(r => r.getInsertedId.asInt64().longValue())
        .head()

  }

  private def stadiumToDocument(stadium: Stadium) = {
    Document(
      "_id" -> stadium.id,
      "name" -> stadium.name,
      "seats" -> stadium.seats,
      "city" -> stadium.city,
      "country" -> stadium.country
    )
  }

  override def update(stadium: Stadium): Future[Try[Stadium]] = ???

  override def findAll(): Future[List[Stadium]] = stadiumCollection
    .find()
    .map(documentToStadium)
    .foldLeft(List.empty[Stadium])((list, stadium) => stadium :: list)
    .head()

  private def documentToStadium(d: Document) = {
    Stadium(
      d.getLong("_id"),
      d.getString("name"),
      d.getInteger("seats"),
      d.getString("city"),
      d.getString("country")
    )
  }

  override def findByName(name: String): Future[Option[Stadium]] = ???

  override def findByCountry(name: String): Future[List[Stadium]] = ???
}
