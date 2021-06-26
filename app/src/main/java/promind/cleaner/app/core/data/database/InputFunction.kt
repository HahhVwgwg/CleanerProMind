package promind.cleaner.app.core.data.database

import kotlin.Throws
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.util.zip.InflaterInputStream

object InputFunction {
    @JvmStatic
	@Throws(Exception::class)
    fun d(`is`: InputStream?, path: String?) {
        val iis = InflaterInputStream(`is`)
        val fos2 = FileOutputStream(path)
        doCopy(iis, fos2)
    }

    @Throws(Exception::class)
    private fun doCopy(`is`: InputStream, os: OutputStream) {
        val data = ByteArray(8192)
        var oneByte: Int
        val bis = BufferedInputStream(`is`)
        while (bis.read(data, 0, 8192).also { oneByte = it } != -1) {
            os.write(data)
        }
        os.close()
        `is`.close()
    }
}