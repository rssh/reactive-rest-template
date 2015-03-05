package utils

import play.api.data.FormError
import play.api.libs.json.{JsValue, Json}

case class ErrorWrapper(reason: String, description: String, url: Option[String] = None, data: Option[JsValue] = None, code: String)

object ErrorWrapper {

  implicit val formatter = Json.format[ErrorWrapper]

  def validationError(formErrors: Seq[FormError]) =
    ErrorWrapper("Validation error.", "An error occurred during validation of your request. Please, check your data and try again.",
      code = "error.validation",
      data = Option(Json.toJson(formErrors.map(error =>
        Json.obj("field" -> error.key, "code" -> error.message)
      ))))

  def notFoundError =
    ErrorWrapper("Resource not found.", "Requested resource not found, please check your query parameters.", code = "error.notFound")

  val internalServerError =
    ErrorWrapper("Internal server error.", "An error occurred during handling of your request. It`s our fault, please try again later.", code = "error.internal")

  val badRequestError =
    ErrorWrapper("Wrong request parameters.", "An error occurred during handling of your request parameters. Please check you request and try again.", code = "error.request")

}