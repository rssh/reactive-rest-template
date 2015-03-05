package utils

import play.api.{Play, Configuration, Logger}
import play.api.data.Form
import play.api.libs.json.{Format, Json}
import play.api.mvc.{Action, Controller}
import reactivemongo.extensions.json.dao.JsonDao

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait DefaultRESTController[Model, Id] extends Controller with RequestMerger {

  def createForm: Form[Model]

  def updateForm(id: Id): Form[Model]

  val DAO: JsonDao[Model, Id]
  implicit val defaultFormatter: Format[Model]
  lazy val listFormatter = defaultFormatter
  lazy val itemFormatter = defaultFormatter

  val defaultPageSize: Int = Play.current.configuration.getInt("default.page.size").getOrElse(10)

  def findAll(page: Int) = Action.async {
    DAO.find(page = page, pageSize = defaultPageSize).map(documents => documents.size match {
      case 0 =>
        Logger.info("Cannot find any documents.")
        NoContent
      case _ =>
        Ok(Json.toJson(documents.map(listFormatter.writes)))
    })
  }

  def findById(id: Id) = Action.async {
    DAO.findById(id).map {
      case Some(document) => Ok(itemFormatter.writes(document))
      case None =>
        Logger.warn("Cannot find document with id: " + id)
        BadRequest(Json.toJson(ErrorWrapper.notFoundError))
    }
  }

  def create = Action.async(parse.json) { implicit request =>
    createForm.bindFromRequest.fold(
      errorsForm => {
        Logger.warn("Request is rejected with validation error.")
        Future.successful(BadRequest(Json.toJson(ErrorWrapper.validationError(errorsForm.errors))))
      },
      document => DAO.insert(document).map(lastError => lastError.ok match {
        case true =>
          Logger.info("Document created.")
          Created(itemFormatter.writes(document))
        case false =>
          Logger.error("Document is rejected by DB with message: " + lastError.message)
          InternalServerError(Json.toJson(ErrorWrapper.internalServerError))
      })
    )
  }

  def update(id: Id) = Action.async(parse.json) { implicit request =>
    DAO.findById(id).flatMap {
      case Some(document) =>
        mergeRequest(document, updateForm(id)).fold(
          errorsForm => {
            Logger.warn("Request is rejected with validation error.")
            Future.successful(BadRequest(Json.toJson(ErrorWrapper.validationError(errorsForm.errors))))
          },
          document => DAO.updateById(id, document).map(lastError => lastError.ok match {
            case true =>
              Logger.info("Document #" + id + " updated.")
              Ok(itemFormatter.writes(document))
            case false =>
              Logger.error("Document is rejected by DB with message: " + lastError.message)
              InternalServerError(Json.toJson(ErrorWrapper.internalServerError))
          })
        )
      case None =>
        Logger.warn("Cannot find document with id: " + id)
        Future.successful(BadRequest(Json.toJson(ErrorWrapper.notFoundError)))
    }
  }

  def remove(id: Id) = Action.async {
    DAO.findById(id).flatMap {
      case Some(document) => DAO.removeById(id).map(lastError => lastError.ok match {
        case true =>
          Logger.info("Document #" + id + " removed.")
          Ok
        case false =>
          Logger.error("Document is rejected by DB with message: " + lastError.message)
          InternalServerError(Json.toJson(ErrorWrapper.internalServerError))
      })
      case None =>
        Logger.warn("Cannot find document with id: " + id)
        Future.successful(BadRequest(Json.toJson(ErrorWrapper.notFoundError)))
    }
  }

}