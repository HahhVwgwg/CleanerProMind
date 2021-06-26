package promind.cleaner.app.core.data.database

import java.io.BufferedInputStream
import java.io.FileInputStream
import java.util.zip.CRC32

object FSEnvironment {
    private var firstDword = 0
    private var toScan: FileInputStream? = null
    private var code = 0
    private var crc: CRC32? = null
    private var temp = 0
    fun scanFile(path: String?): Int {
        code = 0
        try {
            toScan = FileInputStream(path)
        } catch (e: Exception) {
            return 0
        }
        try {
            val bis = BufferedInputStream(toScan)
            bis.mark(4)
            val buffer = ByteArray(4)
            bis.read(buffer, 0, 4)
            bis.reset()
            firstDword = (0xFF and buffer[3].toInt() shl 24 or (0xFF and buffer[2]
                .toInt() shl 16) or (0xFF and buffer[1].toInt() shl 8)
                    or (0xFF and buffer[0].toInt()))
            if (firstDword == 0x04034b50) {
                code = Envi.scanApk(path)
            } else if (firstDword == 0x0a786564) {
                crc = CRC32()
                while (bis.read().also { temp = it } != -1) {
                    crc!!.update(temp)
                }
                bis.reset()
                code = Envi.scanDex(bis, crc!!.value.toInt())
            } else if (firstDword == 0x464c457f) {
                crc = CRC32()
                while (bis.read().also { temp = it } != -1) {
                    crc!!.update(temp)
                }
                bis.reset()
                code = Envi.scanELF(bis, crc!!.value.toInt())
            } else if (firstDword == 0x214f3558) {
                code = Envi.scanDOS(bis)
            } else {
                code = 0
            }
            bis.close()
        } catch (e: Exception) {
            return 0
        }
        return code
    }
}