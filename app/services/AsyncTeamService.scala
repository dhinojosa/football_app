package services

import models.{Stadium, Team}

import scala.concurrent.Future
import scala.util.Try

trait AsyncTeamService {
    def findById(id: Long): Future[Option[Team]]

    def create(stadium:Team): Future[Long]

    def update(stadium: Team): Future[Try[Team]]

    def findAll(): Future[List[Team]]

    def findByName(name: String): Future[Option[Team]]
}
