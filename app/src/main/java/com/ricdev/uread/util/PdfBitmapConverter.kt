package com.ricdev.uread.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class PdfBitmapConverter @Inject constructor(
    private val context: Context
) {
    suspend fun getPageCount(contentUri: Uri): Int {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openFileDescriptor(contentUri, "r")?.use { descriptor ->
                    PdfRenderer(descriptor).use { renderer ->
                        renderer.pageCount
                    }
                } ?: throw IOException("Unable to open PDF file")
            } catch (e: Exception) {
                throw IOException("Failed to get page count: ${e.message}", e)
            }
        }
    }

    suspend fun pdfToBitmap(contentUri: Uri, pageIndex: Int, scaleFactor: Float = 2f): Bitmap {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openFileDescriptor(contentUri, "r")?.use { descriptor ->
                    PdfRenderer(descriptor).use { renderer ->
                        if (pageIndex < 0 || pageIndex >= renderer.pageCount) {
                            throw IndexOutOfBoundsException("Invalid page index: $pageIndex")
                        }
                        renderer.openPage(pageIndex).use { page ->
                            val width = (page.width * scaleFactor).toInt()
                            val height = (page.height * scaleFactor).toInt()

                            val bitmap = Bitmap.createBitmap(
                                width,
                                height,
                                Bitmap.Config.ARGB_8888
                            )
                            page.render(
                                bitmap,
                                null,
                                null,
                                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                            )
                            bitmap
                        }
                    }
                } ?: throw IOException("Unable to open PDF file")
            } catch (e: Exception) {
                throw IOException("Failed to render page $pageIndex: ${e.message}", e)
            }
        }
    }
}