package utils

import play.api.data.Form
import play.api.libs.json.{JsValue, Format, JsObject, Json}
import play.api.mvc.Request

trait RequestMerger {

  def mergeRequest[Model](document: Model, form: Form[Model])(implicit formatter: Format[Model], request: Request[JsValue]): Form[Model] =
    form.fillAndValidate(formatter.reads(Json.toJson(document).as[JsObject] ++ request.body.as[JsObject]).get)

}
