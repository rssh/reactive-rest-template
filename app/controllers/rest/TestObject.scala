package controllers.rest

import models.{TestObjectDAO, DBTestObject}
import play.api.data.Form
import play.api.libs.json.Format
import reactivemongo.extensions.json.dao.JsonDao
import utils.DefaultRESTController

object TestObject extends DefaultRESTController[DBTestObject, String] {

  override def createForm: Form[DBTestObject] = DBTestObject.form()

  override def updateForm(id: String): Form[DBTestObject] = DBTestObject.form(id)

  override val DAO: JsonDao[DBTestObject, String] = TestObjectDAO
  override implicit val defaultFormatter: Format[DBTestObject] = DBTestObject.formatter

}
