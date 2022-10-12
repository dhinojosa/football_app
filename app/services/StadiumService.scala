package services

import models.Stadium

import scala.collection.immutable.List
import scala.util.Try

trait StadiumService {
    def findById(id: Long): Option[Stadium]

    def create(stadium:Stadium): Unit

    def update(stadium: Stadium): Try[Stadium]

    def findAll(): List[Stadium]

    def findByName(name: String): Option[Stadium]

    def findByCountry(name: String): List[Stadium]
}
