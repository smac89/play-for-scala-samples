package controllers

import javax.inject.{Inject, Singleton}

import models.Product
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText}
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller, Flash}

@Singleton
class Products @Inject()(implicit val messagesApi: MessagesApi) extends Controller with I18nSupport {
    private val EditProductKey = "Edit"

    def list = Action { implicit request =>
        val products = Product.findAll
        Ok(views.html.products.list(products))
    }

    def show(ean: Long) = Action { implicit request =>
        Product.findByEan(ean).map(product =>
            Ok(views.html.products.details(product))
        ).getOrElse(NotFound)
    }

    def save = Action { implicit request =>
        val productForm = request.session.get(EditProductKey)
          .map(_ => editProductForm.bindFromRequest())
          .getOrElse(newProductForm.bindFromRequest())

        productForm.fold (
            hasErrors = { form =>
                BadRequest(views.html.products.editProduct(form, form.value))
            },

            success = { newProduct =>
                lazy val preparedAction = {
                    Product.add(newProduct)
                    val message = Messages("products.new.success", newProduct.name)
                    Redirect(routes.Products.show(newProduct.ean))
                      .flashing("success" -> message) removingFromSession EditProductKey
                }

                request.session.get(EditProductKey).map { prodJson =>
                    val editedProduct = Json.parse(prodJson).as[Product]
                    val existing = Product.findByEan(newProduct.ean).exists { someOther =>
                        someOther.ean != editedProduct.ean
                    }
                    if (existing) { // check if the new item conflicts with another
                        val form = productForm.withError("ean", Messages("validation.ean.duplicate"))
                        BadRequest(views.html.products.editProduct(form, form.value))
                    } else {
                        Product.remove(editedProduct.ean)
                        val message = Messages("products.update.success", newProduct.name)
                        preparedAction.flashing("success" -> message)
                    }
                }.getOrElse(preparedAction)
            }
        )
    }

    def newProduct = Action { implicit request =>
        val form = if (request.flash.get("error").isDefined)
            newProductForm.bind(request.flash.data)
        else newProductForm

        Ok(views.html.products.editProduct(form))
    }

    def editProduct(ean: Long) = Action { implicit request =>
        Product.findByEan(ean).map { product =>
            val form = editProductForm.fill(product)
            Ok(views.html.products.editProduct(form, Some(product)))
              .addingToSession((EditProductKey, Json.prettyPrint(Json.toJson(product))))
        } getOrElse NotFound
    }

    private val editProductForm: Form[Product] = Form (
        mapping(
            "ean" -> longNumber.verifying(Constraint { num: Long =>
                if (String.valueOf(num).length != 13) {
                    Invalid(Messages("validation.ean.length"))
                } else Valid
            } and Constraint { num: Long =>
                val (checkdigit, rest): (Long, List[Char]) = (num % 10, num.toString.toList.init)
                val odd: Long = rest.sliding(1, 2).flatten.map(_.asDigit).sum
                val even: Long = rest.drop(1).sliding(1, 2).flatten.foldLeft(0)(_ + _.asDigit * 3)
                if ((odd + even + checkdigit) % 10 != 0) {
                    Invalid(Messages("validation.ean.checkdigit"))
                } else Valid
            }),
            "name" -> nonEmptyText,
            "description" -> nonEmptyText
        )(Product.apply)(Product.unapply)
    )

    private val newProductForm: Form[Product] = Form(
        mapping(
            "ean" -> longNumber.verifying(Constraint { num: Long =>
                if (String.valueOf(num).length != 13) {
                    Invalid(Messages("validation.ean.length"))
                } else Valid
            } and Constraint { num: Long =>
                val (checkdigit, rest): (Long, List[Char]) = (num % 10, num.toString.toList.init)
                val odd: Long = rest.sliding(1, 2).flatten.map(_.asDigit).sum
                val even: Long = rest.drop(1).sliding(1, 2).flatten.foldLeft(0)(_ + _.asDigit * 3)
                if ((odd + even + checkdigit) % 10 != 0) {
                    Invalid(Messages("validation.ean.checkdigit"))
                } else Valid
            } and Constraint { num: Long =>
                Product.findByEan(num)
                  .map(_ => Invalid(Messages("validation.ean.duplicate")))
                  .getOrElse(Valid)
            }),
            "name" -> nonEmptyText,
            "description" -> nonEmptyText
        )(Product.apply)(Product.unapply)
    )
}
