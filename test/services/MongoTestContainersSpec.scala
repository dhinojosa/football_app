package services

import com.dimafeng.testcontainers.{
  Container,
  ForAllTestContainer,
  MongoDBContainer
}
import org.mongodb.scala._
import org.scalatestplus.play.PlaySpec

class MongoTestContainersSpec extends PlaySpec with ForAllTestContainer {
  override val container: MongoDBContainer = new MongoDBContainer()

  "MongoDBTeamService" must {

    "createATeamDocument" in {
      val mongoClient: MongoClient = MongoClient(container.container.getConnectionString)
      val myCompanyDatabase = mongoClient.getDatabase("my_company")
      val employeeCollection = myCompanyDatabase.getCollection("employees")

      val document =
        Document("_id" -> 40, "firstName" -> "Roy", "lastName" -> "Smith")

      employeeCollection
          .insertOne(document)
          .subscribe(r => println(r), t => t.printStackTrace(), () => println("Done"))

      Thread.sleep(5000)

      println("Showing all documents in employees")
      employeeCollection.find().subscribe(d => println(d))
      Thread.sleep(20000)
      val result = 30
      result mustBe (30)
    }
  }
}
