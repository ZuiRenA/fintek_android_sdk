package com.fintek.utils_androidx.encrypt

object BCD {
    /**
     * BCD to ASCII
     * @param bytes bcd data
     * @return ASCII data
     */
    @JvmStatic
    fun encode(bytes: ByteArray): String {
        val temp = CharArray(bytes.size * 2)
        var `val`: Char
        for (i in bytes.indices) {
            `val` = (bytes[i].toInt() and 0xf0 shr 4 and 0x0f).toChar()
            temp[i * 2] = if (`val`.toInt() > 9) `val` + 'A'.toInt() - 10 else `val` + '0'.toInt()
            `val` = (bytes[i].toInt() and 0x0f).toChar()
            temp[i * 2 + 1] = (if (`val`.toInt() > 9) `val` + 'A'.toInt() - 10 else `val` + '0'.toInt())
        }
        return String(temp)
    }

    /**
     * ASCII to BCD
     * @param data ASCII data
     * @return BCD data
     */
    @JvmStatic
    fun decode(data: String): ByteArray {
        val ascii = data.toByteArray()
        val ascLen = ascii.size
        val bcd = ByteArray(ascLen / 2)
        var j = 0
        for (i in 0 until (ascLen + 1) / 2) {
            bcd[i] = asc2bcd(ascii[j++])
            bcd[i] = ((if (j >= ascLen) 0x00 else asc2bcd(ascii[j++])) + (bcd[i].toInt() shl 4)).toByte()
        }
        return bcd
    }

    private fun asc2bcd(asc: Byte): Byte {
        return if (asc >= '0'.toByte() && asc <= '9'.toByte()) {
            (asc - '0'.toByte()).toByte()
        } else if (asc >= 'A'.toByte() && asc <= 'F'.toByte()) {
            (asc - 'A'.toByte() + 10).toByte()
        } else if (asc >= 'a'.toByte() && asc <= 'f'.toByte()) {
            (asc - 'a'.toByte() + 10).toByte()
        } else {
            (asc - 48).toByte()
        }
    }
}