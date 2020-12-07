package com.fintek.utils_androidx.encrypt

import com.fintek.utils_androidx.encrypt.Base64.decode
import com.fintek.utils_androidx.encrypt.Base64.encode
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESedeKeySpec
import javax.crypto.spec.IvParameterSpec

object DES3 {
    private const val KEY_ALGORITHM = "DESede"
    private const val CIPHER_ALGORITHM = "desede/CBC/PKCS5Padding"
    private const val IV_KEY = "password"

    /**
     * Return key
     *
     * @param keyStr key string
     * @return key result
     * @throws Exception
     */
    @JvmStatic
    @Throws(Exception::class)
    private fun keyGenerator(keyStr: String): SecretKey {
        val temp = keyStr.toByteArray(charset("UTF-8"))
        val key: ByteArray
        if (temp.size < 24) {
            key = ByteArray(24)
            System.arraycopy(temp, 0, key, 0, temp.size)
        } else {
            key = ByteArray(temp.size)
            System.arraycopy(temp, 0, key, 0, key.size)
        }
        val secretKey = DESedeKeySpec(key)
        val keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM)
        return keyFactory.generateSecret(secretKey)
    }

    /**
     * encrypt data
     *
     * @param key encrypt key
     * @param data data that need encrypt
     * @return Base64
     * @throws Exception
     */
    @JvmStatic
    @JvmOverloads
    @Throws(Exception::class)
    fun encrypt(key: String, data: String, ivKey: String = IV_KEY): String {
        val secretKey: Key = keyGenerator(key)
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        val paramSpec = IvParameterSpec(ivKey.toByteArray())
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec)
        return encode(cipher.doFinal(data.toByteArray()))
    }

    /**
     * @param key key
     * @param data data that need decrypt
     * @return decrypted data
     * @throws Exception
     */
    @JvmStatic
    @JvmOverloads
    @Throws(Exception::class)
    fun decrypt(key: String, data: String?, ivKey: String = IV_KEY): String {
        val desKey: Key = keyGenerator(key)
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        val paramSpec = IvParameterSpec(ivKey.toByteArray())
        cipher.init(Cipher.DECRYPT_MODE, desKey, paramSpec)
        return String(cipher.doFinal(decode(data!!)))
    }
}