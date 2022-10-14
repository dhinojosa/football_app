package services

import models.Stadium

import scala.concurrent.Future
import scala.util.Try

trait AsyncStadiumService {
    def findById(id: Long): Future[Option[Stadium]]

    def create(stadium:Stadium): Future[Long]

    def update(stadium: Stadium): Future[Try[Stadium]]

    def findAll(): Future[List[Stadium]]

    def findByName(name: String): Future[Option[Stadium]]

    def findByCountry(name: String): Future[List[Stadium]]
}
