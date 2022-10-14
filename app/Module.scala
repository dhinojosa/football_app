import com.google.inject.{AbstractModule, Provides}
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import play.api.Configuration
import services.{AsyncStadiumService, MemoryStadiumService, MongoStadiumService, StadiumService}

import javax.inject.Named

class Module extends AbstractModule {
  override def configure(): Unit = {

    @Provides
    def databaseProvider(configuration: Configuration): MongoDatabase = {
      val username = configuration.get[String]("mongo.username")
      val password = configuration.get[String]("mongo.password")
      val database = configuration.get[String]("mongo.database")
      val mongoClient: MongoClient = MongoClient(
        s"mongodb://$username:$password@localhost:" + 27017
      )
      mongoClient.getDatabase(database)
    }

    @Provides
    @Named("stadiumCollection")
    def stadiumCollectionProvider(mongoDatabase: MongoDatabase): MongoCollection[Document] = {
      mongoDatabase.getCollection("stadiums")
    }

    @Provides
    @Named("playerCollection")
    def playerCollectionProvider(mongoDatabase: MongoDatabase)   = {
      mongoDatabase.getCollection("player")
    }

    @Provides
    @Named("teamCollection")
    def teamCollectionProvider(mongoDatabase: MongoDatabase): MongoCollection[Document] = {
      mongoDatabase.getCollection("teams")
    }

    bind(classOf[StadiumService]).to(classOf[MemoryStadiumService])
    bind(classOf[AsyncStadiumService]).to(classOf[MongoStadiumService])
  }
}
