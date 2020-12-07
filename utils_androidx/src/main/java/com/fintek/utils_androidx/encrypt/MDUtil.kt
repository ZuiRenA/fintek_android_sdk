package com.fintek.utils_androidx.encrypt

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest

object MDUtil {

    /**
     * @param type encode type ***see*** [TYPE]
     * @param data data that need encode
     * @return encoded data
     */
    @JvmStatic
    fun encode(type: TYPE, data: String): String {
        return try {
            val digest = MessageDigest.getInstance(type.value)
            digest.update(data.toByteArray())
            bytes2Hex(digest.digest())
        } catch (e: Exception) {
            e.printStackTrace()
            data
        }
    }

    /**
     * @param type encode type ***see*** [TYPE]
     * @param file encode file
     * @return md file
     */
    @JvmStatic
    fun fileMD(type: TYPE, file: File): String {
        var fileInputStream: FileInputStream? = null
        return try {
            val digest = MessageDigest.getInstance(type.value)
            fileInputStream = FileInputStream(file)
            val buffer = ByteArray(8192)
            var length: Int
            while (fileInputStream.read(buffer).also { length = it } != -1) {
                digest.update(buffer, 0, length)
            }
            BigInteger(1, digest.digest()).toString(16)
        } catch (e: Exception) {
            e.printStackTrace()
            file.path
        } finally {
            try {
                fileInputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 32-bit encryption zero padding
     * @param bts
     * @return hex string
     */
    private fun bytes2Hex(bts: ByteArray): String {
        var des = ""; var tmp = ""
        for (i in bts.indices) {
            tmp = Integer.toHexString(bts[i].toInt() and 0xFF)
            if (tmp.length == 1) {
                des += "0"
            }
            des += tmp
        }
        return des
    }

    enum class TYPE(val value: String) {
        MD2("MD2"),
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA224("SHA-224"),
        SHA256("SHA-256"),
        SHA384("SHA-384"),
        SHA512("SHA-512");
    }
}