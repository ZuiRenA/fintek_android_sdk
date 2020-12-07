package com.fintek.utils_androidx.encrypt

import com.fintek.utils_androidx.encrypt.RSA.decrypt
import com.fintek.utils_androidx.encrypt.RSA.encrypt
import java.security.Key

object RSAUtil {
    ///////////////////////////////////////////////////////////////////////////
    // RSA + Base64
    ///////////////////////////////////////////////////////////////////////////
    @JvmStatic
    fun base64Encrypt(encryptKey: Key?, data: String): String {
        return try {
            Base64.encode(encrypt(encryptKey, data.toByteArray()))
        } catch (e: Exception) {
            e.printStackTrace()
            data
        }
    }

    @JvmStatic
    fun base64Decrypt(decryptKey: Key?, data: String): String {
        return try {
            val base64Decrypt = decrypt(decryptKey, Base64.decode(data))
            String(base64Decrypt)
        } catch (e: Exception) {
            e.printStackTrace()
            data
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // RSA + BCD
    ///////////////////////////////////////////////////////////////////////////
    @JvmStatic
    fun bcdEncrypt(encryptKey: Key?, data: String): String {
        return try {
            BCD.encode(encrypt(encryptKey, data.toByteArray()))
        } catch (e: Exception) {
            e.printStackTrace()
            data
        }
    }

    @JvmStatic
    fun bcdDecrypt(decryptKey: Key?, data: String): String {
        return try {
            val bcdDecrypt = decrypt(decryptKey, BCD.decode(data))
            bcdDecrypt?.let { String(it) } ?: data
        } catch (e: Exception) {
            e.printStackTrace()
            data
        }
    }
}