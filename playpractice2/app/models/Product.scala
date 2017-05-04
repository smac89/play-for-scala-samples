package models

import play.api.libs.json._

import scala.util.{Success, Try}

/**
  * A model class defining a product and it's attributes
  *
  * @param ean The European Article number
  * @param name The name of the product
  * @param description A description of the product
  */
case class Product (ean: Long, name: String, description: String)

object Product {
    private var products = Set(
        Product(5010255079763L, "Paperclips Large",
            "Large Plain Pack of 1000"),
        Product(5018206244666L, "Giant Paperclips",
            "Giant Plain 51mm 100 pack"),
        Product(5018306332812L, "Paperclip Giant Plain",
            "Giant Plain Pack of 10000"),
        Product(5018306312913L, "No Tear Paper Clip",
            "No Tear Extra Large Pack of 1000"),
        Product(5018206244611L, "Zebra Paperclips",
            "Zebra Length 28mm Assorted 150 Pack")
    )

    def findAll: List[Product] = products.toList.sortBy(_.ean)
    def findByEan(ean: Long): Option[Product] = products.find(_.ean == ean)
    def add(product: Product): Unit = products += product
    def remove(ean: Long): Boolean = findByEan(ean).exists { p =>
        products -= p
        true
    }

    implicit object ProductFormat extends Format[Product] {

        override def reads(json: JsValue): JsResult[Product] = {
            val readJSon = Try {
                Product(
                    (json \ "ean").as[Long],
                    (json \ "name").as[String],
                    (json \ "descrip").as[String]
                )
            }

            readJSon match {
                case Success(prod) => JsSuccess(prod)
                case _ => JsError(readJSon.failed.get.getMessage)
            }
        }

        override def writes(o: Product): JsValue = JsObject(Map[String, JsValue](
            "ean" -> JsNumber(o.ean),
            "name" -> JsString(o.name),
            "descrip" -> JsString(o.name)
        ))
    }
}
