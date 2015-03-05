package utils

import play.api.data.validation.{Invalid, Valid, Constraint}
import reactivemongo.extensions.json.dao.JsonDao
import reactivemongo.extensions.json.dsl.JsonDsl

import scala.concurrent.Await
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

trait ValidationRules[DocumentID] extends JsonDsl {

  def unique(documentId: DocumentID, dao: JsonDao[_, DocumentID], fieldName: String): Constraint[String] = Constraint("constraints.unique")({
    content =>
      Await.result(dao.count($and("_id" $ne documentId.toString, fieldName $eq content)).map {
        case 0 => Valid
        case _ => Invalid("error.unique")
      }, 5.seconds)
  })

}
