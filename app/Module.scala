import com.google.inject.AbstractModule
import services.{
  AsyncStadiumService,
  MemoryStadiumService,
  MongoStadiumService,
  StadiumService
}

class Module extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[StadiumService]).to(classOf[MemoryStadiumService])
    bind(classOf[AsyncStadiumService]).to(classOf[MongoStadiumService])
  }
}
