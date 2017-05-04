package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.{Action, Controller}

import scala.util.Try

@Singleton
class Barcodes extends Controller {

    val ImageResolution = 144

    def barcode(ean: Long) = Action {
        val MimeType = "image/png"

        val attempt = Try (ean13BarCode(ean, MimeType))
          .map(imageData => Ok(imageData).as(MimeType))
        attempt.getOrElse(BadRequest(s"Couldn't generate bar code. Error: ${attempt.failed.get.getMessage}"))
    }

    private def ean13BarCode(ean: Long, mimeType: String): Array[Byte] = {
        import java.io.ByteArrayOutputStream
        import java.awt.image.BufferedImage
        import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider
        import org.krysalis.barcode4j.impl.upcean.EAN13Bean

        val output: ByteArrayOutputStream = new ByteArrayOutputStream
        val canvas: BitmapCanvasProvider =
            new BitmapCanvasProvider(output, mimeType, ImageResolution,
                BufferedImage.TYPE_BYTE_GRAY, true, 0)
        val barcode = new EAN13Bean()
        barcode.generateBarcode(canvas, String valueOf ean)
        canvas.finish()

        output.toByteArray
    }
}
