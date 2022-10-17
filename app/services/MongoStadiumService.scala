package services

import models.Stadium
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.{Document, MongoDatabase}

import javax.inject.Inject
import scala.concurrent.Future
import scala.util.Try

/** In this latest change I am injecting the database rather creating it inside
  * of this particular service. This was a tradeoff to get this to work.
  * Check the previous commit to see the difference
  *
  * @param mongoDatabase the MongoDatabase connection being used
  */
class MongoStadiumService @Inject() (mongoDatabase: MongoDatabase)
    extends AsyncStadiumService {

  override def findById(id: Long): Future[Option[Stadium]] =
    mongoDatabase
      .getCollection("stadiums")
      .find(equal("_id", id))
      .map(documentToStadium)
      .toSingle()
      .headOption()

  override def create(stadium: Stadium) = {
    mongoDatabase
      .getCollection("stadiums")
      .insertOne(stadiumToDocument(stadium))
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

  override def findAll(): Future[List[Stadium]] =
    mongoDatabase
      .getCollection("stadiums")
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
