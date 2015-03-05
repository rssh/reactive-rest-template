package models

import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson.Macros.Annotations.Key
import reactivemongo.extensions.json.dao.JsonDao
import reactivemongo.extensions.json.dsl.JsonDsl
import utils.ValidationRules

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current

case class DBTestObject(_id: String = BSONObjectID.generate.stringify, title: String, uniqueField: String, age: Int)

object DBTestObject extends ValidationRules[String] {

  implicit val formatter = Json.format[DBTestObject]

  def form(_id: String = BSONObjectID.generate.stringify) = Form(
    mapping(
      "_id" -> default(nonEmptyText, _id),
      "title" -> nonEmptyText,
      "uniqueField" -> nonEmptyText.verifying(unique(_id, TestObjectDAO, "uniqueField")),
      "age" -> number(min = 0, max = 100)
    )(DBTestObject.apply)(DBTestObject.unapply)
  )

}

object TestObjectDAO extends JsonDao[DBTestObject, String](ReactiveMongoPlugin.db, "testObject") with JsonDsl
