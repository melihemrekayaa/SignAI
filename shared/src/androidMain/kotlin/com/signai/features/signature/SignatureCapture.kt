package com.signai.features.signature

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.signai.features.signature.creation.StrokeSegment
import java.io.ByteArrayOutputStream

actual class SignatureCapture {
    actual fun captureToPng(
        strokes: List<StrokeSegment>,
        width: Int,
        height: Int,
        backgroundColor: Int
    ): ByteArray {
        // 1. Bitmap oluştur
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 2. Arka planı boya
        canvas.drawColor(backgroundColor)

        // 3. Kalem ayarları
        val paint = Paint().apply {
            color = android.graphics.Color.BLACK // Varsayılan siyah kalem
            style = Paint.Style.STROKE
            strokeWidth = 5f // Çizgi kalınlığı
            isAntiAlias = true
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }

        // 4. Çizgileri çiz
        // NOT: StrokeSegment yapına göre buradaki 'points' erişimini düzenlemelisin.
        // Ben StrokeSegment'in içinde 'points' adında bir Offset listesi olduğunu varsayıyorum.
        /* strokes.forEach { segment ->
            if (segment.points.isNotEmpty()) {
                val path = Path()
                path.moveTo(segment.points.first().x, segment.points.first().y)
                for (i in 1 until segment.points.size) {
                    path.lineTo(segment.points[i].x, segment.points[i].y)
                }
                canvas.drawPath(path, paint)
            }
        }
        */

        // 5. Bitmap'i PNG formatına sıkıştır ve ByteArray'e çevir
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}