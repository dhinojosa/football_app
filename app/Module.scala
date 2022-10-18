import com.google.inject.{AbstractModule, Provides}
import org.mongodb.scala.{MongoClient, MongoDatabase}
import play.api.Configuration
import services.{AsyncStadiumService, MemoryStadiumService, MongoStadiumService, StadiumService}

/**
 * Preferred cleaned module. Different from before. But is workable and
 * injectable in all different services
 */
class Module extends AbstractModule {
  override def configure(): Unit = {

    @Provides
    def databaseProvider(configuration: Configuration): MongoDatabase = {
      val username = configuration.get[String]("mongo.username")
      val password = configuration.get[String]("mongo.password")
      val database = configuration.get[String]("mongo.database")
      val host = configuration.get[String]("mongo.host")
      val port = configuration.get[String]("mongo.port")
      val mongoClient: MongoClient = MongoClient(
        s"mongodb://$username:$password@${host}:" + port
      )
      mongoClient.getDatabase(database)
    }

    bind(classOf[StadiumService]).to(classOf[MemoryStadiumService])
    bind(classOf[AsyncStadiumService]).to(classOf[MongoStadiumService])
  }
}
