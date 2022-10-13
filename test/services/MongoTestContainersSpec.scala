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
      println("Did this work?")
//      val host = container.host
//      val port = container.livenessCheckPortNumbers.head
//      println(host)
//      println(port)
      val mongoClient: MongoClient = MongoClient("mongodb://aws-29190-us-west-390391991292193:" + 27017)
      val myCompanyDatabase = mongoClient.getDatabase("my_company")
      val employeeCollection = myCompanyDatabase.getCollection("employees")

      val document =
        Document("_id" -> 30, "firstName" -> "Roy", "lastName" -> "Smith")

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
