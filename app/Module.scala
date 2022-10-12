import com.google.inject.AbstractModule
import services.{MemoryStadiumService, StadiumService}

class Module extends AbstractModule{
    override def configure(): Unit = {
       bind(classOf[StadiumService]).to(classOf[MemoryStadiumService])
    }
}
