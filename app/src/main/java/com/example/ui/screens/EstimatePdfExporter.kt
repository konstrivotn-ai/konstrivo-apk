package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.local.ProjectEstimateEntity
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Creates a printable, shareable PDF for an already-saved estimate. */
object EstimatePdfExporter {
    fun share(context: Context, estimate: ProjectEstimateEntity) {
        val file = create(context, estimate)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_SUBJECT, "KONSTRIVO - Devis ${estimate.id}")
            putExtra(Intent.EXTRA_TEXT, "Devis KONSTRIVO pour ${estimate.clientName}")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Partager le devis PDF"))
    }

    private fun create(context: Context, estimate: ProjectEstimateEntity): File {
        val document = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 42f
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(17, 36, 62)
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val headingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(17, 36, 62)
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.DKGRAY
            textSize = 10f
        }
        val totalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(177, 130, 35)
            textSize = 15f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        var pageNumber = 0
        var page: PdfDocument.Page? = null
        var canvas: android.graphics.Canvas? = null
        var y = 0f

        fun newPage() {
            page?.let { document.finishPage(it) }
            pageNumber++
            page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
            canvas = page!!.canvas
            y = 48f
            canvas!!.drawText("KONSTRIVO", margin, y, titlePaint)
            y += 24f
            canvas!!.drawText("DEVIS / ESTIMATE #${estimate.id}", margin, y, headingPaint)
            y += 22f
            canvas!!.drawLine(margin, y, pageWidth - margin, y, bodyPaint)
            y += 22f
        }
        fun line(text: String, paint: Paint = bodyPaint, gap: Float = 16f) {
            if (y > pageHeight - 55f) newPage()
            val maxWidth = pageWidth - margin * 2
            val safe = text.replace('\n', ' ').replace('\r', ' ')
            var remaining = safe
            while (remaining.isNotEmpty()) {
                var count = remaining.length
                while (count > 1 && paint.measureText(remaining.substring(0, count)) > maxWidth) count--
                canvas!!.drawText(remaining.substring(0, count), margin, y, paint)
                remaining = remaining.substring(count).trimStart()
                y += gap
                if (remaining.isNotEmpty() && y > pageHeight - 55f) newPage()
            }
        }

        try {
            newPage()
            line("Client: ${estimate.clientName}")
            if (estimate.clientPhone.isNotBlank()) line("Telephone: ${estimate.clientPhone}")
            line("Chantier: ${estimate.location}")
            line("Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(estimate.createdAt))}")
            line("Pays: ${estimate.countryCode}    Devise: ${estimate.currency}")
            line(" ")
            line("DETAIL DES MATERIAUX", headingPaint, 20f)
            line("Materiau / Material                         Quantite       Total", headingPaint)
            val items = try { JSONArray(estimate.materialsJson) } catch (_: Exception) { JSONArray() }
            for (i in 0 until items.length()) {
                val item = items.optJSONObject(i) ?: continue
                val name = item.optString("nameFr", item.optString("id", "Materiau"))
                val qty = item.optDouble("quantity", 0.0)
                val unit = item.optString("unit", "")
                val amount = item.optDouble("totalPrice", 0.0)
                line("$name  |  ${String.format(Locale.US, "%.3f", qty)} $unit  |  ${String.format(Locale.US, "%.3f", amount)} ${estimate.currency}")
            }
            line(" ")
            line("Sous-total materiaux: ${String.format(Locale.US, "%.3f", estimate.totalMaterialCost)} ${estimate.currency}")
            line("Main d'oeuvre: ${String.format(Locale.US, "%.3f", estimate.laborCost)} ${estimate.currency}")
            line("TOTAL TTC: ${String.format(Locale.US, "%.3f", estimate.grandTotal)} ${estimate.currency}", totalPaint, 24f)
            if (estimate.notes.isNotBlank()) {
                line(" ")
                line("Notes:", headingPaint)
                line(estimate.notes)
            }
            line(" ")
            line("Document genere par KONSTRIVO. Verifier les quantites et tarifs avant signature.")
            page?.let { document.finishPage(it) }
            val directory = File(context.cacheDir, "devis-pdf").apply { mkdirs() }
            val file = File(directory, "KONSTRIVO_devis_${estimate.id}.pdf")
            FileOutputStream(file).use { document.writeTo(it) }
            return file
        } finally {
            document.close()
        }
    }
}
