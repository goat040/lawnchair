package app.lawnchair.icons

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette

class IntelligentAdaptiveIconDrawable(
    private val foregroundDrawable: Drawable,
) : AdaptiveIconDrawable(ColorDrawable(Color.TRANSPARENT), foregroundDrawable) {

    init {
        val bitmap = foregroundDrawable.toBitmap()
        Palette.from(bitmap).generate { palette ->
            val dominantColor = palette?.dominantSwatch?.rgb ?: Color.WHITE
            val blendedColor = blendColor(dominantColor, Color.WHITE, 0.7f)
            var background = ColorDrawable(blendedColor)
            val newBitmap = overrideEdgeColors(bitmap, blendedColor)
            var foreground = BitmapDrawable(newBitmap)
        }
    }

    private fun blendColor(color1: Int, color2: Int, ratio: Float): Int {
        val inverseRatio = 1 - ratio
        val r = Color.red(color1) * ratio + Color.red(color2) * inverseRatio
        val g = Color.green(color1) * ratio + Color.green(color2) * inverseRatio
        val b = Color.blue(color1) * ratio + Color.blue(color2) * inverseRatio
        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }

    override fun draw(canvas: Canvas) {
        val scale = 0.8f
        canvas.save()
        canvas.scale(scale, scale, bounds.width() / 2f, bounds.height() / 2f)
        super.draw(canvas)
        canvas.restore()
    }

    private fun overrideEdgeColors(bitmap: Bitmap, backgroundColor: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    val pixel = pixels[y * width + x]
                    if (colorIsSimilar(pixel, backgroundColor)) {
                        pixels[y * width + x] = backgroundColor
                    }
                }
            }
        }

        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }

    private fun colorIsSimilar(color1: Int, color2: Int, threshold: Double = 20.0): Boolean {
        val r1 = Color.red(color1)
        val g1 = Color.green(color1)
        val b1 = Color.blue(color1)
        val r2 = Color.red(color2)
        val g2 = Color.green(color2)
        val b2 = Color.blue(color2)

        val distance = Math.sqrt(
            Math.pow((r1 - r2).toDouble(), 2.0) +
                Math.pow((g1 - g2).toDouble(), 2.0) +
                Math.pow((b1 - b2).toDouble(), 2.0),
        )

        return distance < threshold
    }
}
