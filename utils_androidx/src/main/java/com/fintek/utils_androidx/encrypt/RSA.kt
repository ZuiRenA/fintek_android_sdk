/*
 * RSA加密原理概述
 * RSA的安全性依赖于大数的分解，公钥和私钥都是两个大素数（大于100的十进制位）的函数。
 * 据猜测，从一个密钥和密文推断出明文的难度等同于分解两个大素数的积
 * ===================================================================
 * 密钥的产生：
 * 1.选择两个大素数 p,q ,计算 n=p*q;
 * 2.随机选择加密密钥 e ,要求 e 和 (p-1)*(q-1)互质
 * 3.利用 Euclid 算法计算解密密钥 d , 使其满足 e*d = 1(mod(p-1)*(q-1)) (其中 n,d 也要互质)
 * 4:至此得出公钥为 (n,e) 私钥为 (n,d)
 * ===================================================================
 * 加解密方法：
 * 1.首先将要加密的信息 m(二进制表示) 分成等长的数据块 m1,m2,...,mi 块长 s(尽可能大) ,其中 2^s<n
 * 2:对应的密文是： ci = mi^e(mod n)
 * 3:解密时作如下计算： mi = ci^d(mod n)
 * ===================================================================
 * RSA速度
 * 由于进行的都是大数计算，使得RSA最快的情况也比DES慢上100倍，无论是软件还是硬件实现。
 * 速度一直是RSA的缺陷。一般来说只用于少量数据加密。
 */
package com.fintek.utils_androidx.encrypt

import android.text.TextUtils
import com.fintek.utils_androidx.UtilsBridge
import com.fintek.utils_androidx.encrypt.Base64.decode
import com.fintek.utils_androidx.encrypt.Base64.encode
import java.io.*
import java.math.BigInteger
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.*
import javax.crypto.Cipher

/**
 * About encryption filling method:
 * The default RSA implementation of the android system is "RSA/None/NoPadding",
 * while the standard JDK default RSA implementation is "RSA/None/PKCS1Padding"
 *
 *
 * About segment encryption:
 * The RSA algorithm stipulates: the number of bytes to be encrypted
 * cannot exceed the length of the key divided by 8 and then minus 11 (ie: KeySize / 8-11),
 * and the number of bytes in the ciphertext
 * obtained after encryption is exactly the key's Divide the length value by 8 (ie: KeySize / 8)
 */
object RSA {
    private const val KEY_PATH = "D:/RSAKey.txt"
    private const val KEY_ALGORITHM = "RSA"
    private const val CIPHER_ALGORITHM = "RSA/None/PKCS1Padding"
    private const val DEFAULT_KEY_SIZE = 1024
    private const val DEFAULT_BUFFER_SIZE = DEFAULT_KEY_SIZE / 8 - 11

    /**
     * Randomly generate RSA key pair
     *
     * @param keyLength Key length, range: 512～2048 default 1024
     */
    private fun generateRSAKeyPair(keyLength: Int = DEFAULT_KEY_SIZE): KeyPair? {
        return try {
            val kpg = KeyPairGenerator.getInstance(KEY_ALGORITHM)
            kpg.initialize(keyLength)
            val kp = kpg.genKeyPair()
            printPublicKeyInfo(kp.public)
            printPrivateKeyInfo(kp.private)
            kp
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Restore the public key through the public key byte[]
     *
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    private fun getPublicKey(keyBytes: ByteArray): PublicKey {
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        // X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        return keyFactory.generatePublic(keySpec)
    }

    /**
     * 通过私钥byte[]将私钥还原
     *
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    private fun getPrivateKey(keyBytes: ByteArray): PrivateKey {
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        // X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        return keyFactory.generatePrivate(keySpec)
    }

    /**
     * Use N and e values to restore the public key
     * Obtain the public key through modulus + exponent
     *
     * @param modulus        Modulus
     * @param publicExponent Public key index
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    private fun getPublicKey(modulus: String, publicExponent: String): RSAPublicKey {
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val keySpec = RSAPublicKeySpec(BigInteger(modulus), BigInteger(publicExponent))
        return keyFactory.generatePublic(keySpec) as RSAPublicKey
    }

    /**
     * Use N and d values to restore the private key
     * Obtain private key through modulus + exponent
     *
     * @param modulus         Modulus
     * @param privateExponent Private key index
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    private fun getPrivateKey(modulus: String, privateExponent: String): RSAPrivateKey {
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val keySpec = RSAPrivateKeySpec(BigInteger(modulus), BigInteger(privateExponent))
        return keyFactory.generatePrivate(keySpec) as RSAPrivateKey
    }

    /**
     * Load public key from string
     *
     * @param publicKeyStr Public key data string
     * @return Public key
     * @throws Exception Exception when loading public key
     */
    @Throws(Exception::class)
    private fun loadPublicKey(publicKeyStr: String): PublicKey {
        return try {
            val buffer = decode(publicKeyStr)
            val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
            val keySpec = X509EncodedKeySpec(buffer)
            keyFactory.generatePublic(keySpec)
        } catch (e: NoSuchAlgorithmException) {
            throw Exception("无此算法")
        } catch (e: InvalidKeySpecException) {
            throw Exception("公钥非法")
        } catch (e: NullPointerException) {
            throw Exception("公钥数据为空")
        }
    }

    /**
     * Load private key from string
     * PKCS8EncodedKeySpec (PKCS#8 encoded Key instruction) is used when loading.
     *
     * @param privateKeyStr Private key data string
     * @return Private key
     * @throws Exception Exception when loading private key
     */
    @Throws(Exception::class)
    private fun loadPrivateKey(privateKeyStr: String): PrivateKey {
        return try {
            val buffer = decode(privateKeyStr)
            val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
            // X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            val keySpec = PKCS8EncodedKeySpec(buffer)
            keyFactory.generatePrivate(keySpec)
        } catch (e: NoSuchAlgorithmException) {
            throw Exception("No such algorithm")
        } catch (e: InvalidKeySpecException) {
            throw Exception("Illegal private key")
        } catch (e: NullPointerException) {
            throw Exception("The private key data is empty")
        }
    }

    /**
     * Load the public key from the file input stream
     *
     * @param in Public key input stream
     * @return Public key
     * @throws Exception Exception when loading public key
     */
    @Throws(Exception::class)
    private fun loadPublicKey(`in`: InputStream): PublicKey {
        return try {
            loadPublicKey(readKey(`in`))
        } catch (e: IOException) {
            throw Exception("Public key data stream read error")
        } catch (e: NullPointerException) {
            throw Exception("The public key input stream is empty")
        }
    }

    /**
     * Load private key from file input stream
     *
     * @param in Private key input stream
     * @return Private key
     * @throws Exception Exception when loading public key
     */
    @Throws(Exception::class)
    private fun loadPrivateKey(`in`: InputStream): PrivateKey {
        return try {
            loadPrivateKey(readKey(`in`))
        } catch (e: IOException) {
            throw Exception("Private key data read error")
        } catch (e: NullPointerException) {
            throw Exception("The private key input stream is empty")
        }
    }

    /**
     * Read key information
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun readKey(`in`: InputStream): String {
        val br = BufferedReader(InputStreamReader(`in`))
        val sb = StringBuilder()
        var readLine: String
        while (br.readLine().also { readLine = it } != null) {
            if (readLine[0] != '-') {
                sb.append(readLine)
                sb.append('\r')
            }
        }
        return sb.toString()
    }

    /**
     * Save the key file
     *
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun saveKeyPair(kp: KeyPair) {
        val fos = FileOutputStream(KEY_PATH)
        val oos = ObjectOutputStream(fos)
        oos.writeObject(kp)
        oos.close()
        fos.close()
    }

    /**
     * Read key file
     *
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun readKeyPair(path: String): KeyPair {
        var path: String? = path
        if (TextUtils.isEmpty(path)) {
            path = KEY_PATH
        }
        val fis = FileInputStream(path)
        val oos = ObjectInputStream(fis)
        val kp = oos.readObject() as KeyPair
        oos.close()
        fis.close()
        return kp
    }

    /**
     * Read source file content
     *
     * @param path file path
     * @return byte[] file content
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun readFile(path: String): String {
        val br = BufferedReader(FileReader(path))
        var s = br.readLine()
        var str = ""
        while (s[0] != '-') {
            str += s
            s = br.readLine()
        }
        br.close()
        return str
    }

    /**
     * Print public key information
     */
    private fun printPublicKeyInfo(publicKey: PublicKey) {
        val rsaPublicKey = publicKey as RSAPublicKey
        UtilsBridge.v(
            "---------- RSAPublicKey ----------",
            "Modulus.length= ${rsaPublicKey.modulus.bitLength()}",
            "Modulus= ${rsaPublicKey.modulus}",
            "PublicExponent.length= ${rsaPublicKey.publicExponent.bitLength()}",
            "PublicExponent= ${rsaPublicKey.publicExponent}",
            "PublicEncoded= ${encode(rsaPublicKey.encoded)}"
        )
    }

    /**
     * Print private key information
     */
    private fun printPrivateKeyInfo(privateKey: PrivateKey) {
        val rsaPrivateKey = privateKey as RSAPrivateKey
        UtilsBridge.v(
            "---------- RSAPrivateKey ----------",
            "Modulus.length= ${rsaPrivateKey.modulus.bitLength()}",
            "Modulus= ${rsaPrivateKey.modulus}",
            "PrivateExponent.length= ${rsaPrivateKey.privateExponent.bitLength()}",
            "PrivateExponent= ${rsaPrivateKey.privateExponent}",
            "PrivateEncoded= ${encode(rsaPrivateKey.encoded)}"
        )
    }

    /**
     * encrypt
     *
     * @param key  Encrypted key
     * @param data Data to be encrypted
     * @return Encrypted data
     * @throws Exception
     */
    @JvmStatic
    @Throws(Exception::class)
    fun encrypt(key: Key?, data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val blockSize = DEFAULT_BUFFER_SIZE
        val outputSize = cipher.getOutputSize(data.size)
        val leavedSize = data.size % blockSize
        val blocksSize = if (leavedSize != 0) data.size / blockSize + 1 else data.size / blockSize
        val raw = ByteArray(outputSize * blocksSize)
        var i = 0
        while (data.size - i * blockSize > 0) {
            if (data.size - i * blockSize > blockSize) {
                cipher.doFinal(data, i * blockSize, blockSize, raw, i * outputSize)
            } else {
                cipher.doFinal(data, i * blockSize, data.size - i * blockSize, raw, i * outputSize)
            }
            i++
        }
        return raw
    }

    /**
     * decrypt
     *
     * @param key  Decrypted key
     * @param data Data to be decrypted
     * @return Decrypted data
     * @throws Exception
     */
    @JvmStatic
    @Throws(Exception::class)
    fun decrypt(key: Key?, data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        // 编码前设定编码方式及密钥
        cipher.init(Cipher.DECRYPT_MODE, key)
        // int                   blockSize = cipher.getBlockSize();
        val blockSize = DEFAULT_BUFFER_SIZE + 11
        val bout = ByteArrayOutputStream(64)
        var i = 0
        while (data.size - i * blockSize > 0) {
            bout.write(cipher.doFinal(data, i * blockSize, blockSize))
            i++
        }
        return bout.toByteArray()
    }

    /**
     * Private key type
     */
    const val TYPE_PRIVATE = "0"

    /**
     * Public key type
     */
    const val TYPE_PUBLIC = "1"

    /**
     * @param mode   Key loading mode
     * @param params Key information
     * First digit: Key type: 0-private key, 1-public key
     * Second place: key string, key file path, modulus
     * Third place: Index
     */
    @JvmStatic
    fun getKey(mode: MODE, vararg params: String): Key? {
        return try {
            if (params.size <= 1) {
                return null
            }
            var type = ""
            var arg1 = ""
            var arg2 = ""
            if (params.size == 2) {
                type = params[0]
                arg1 = params[1]
            } else if (params.size == 3) {
                type = params[0]
                arg1 = params[1]
                arg2 = params[2]
            }
            when (mode) {
                MODE.PEM_STRING -> if (TYPE_PRIVATE == type) {
                    loadPrivateKey(arg1)
                } else {
                    loadPublicKey(arg1)
                }
                MODE.MODULUS_EXPONENT -> if (TYPE_PRIVATE == type) {
                    getPrivateKey(arg1, arg2)
                } else {
                    getPublicKey(arg1, arg2)
                }
                MODE.PEM_FILE -> if (TYPE_PRIVATE == type) {
                    loadPrivateKey(readFile(arg1))
                } else {
                    loadPublicKey(readFile(arg1))
                }
                MODE.KEY_FILE -> if (TYPE_PRIVATE == type) {
                    readKeyPair(arg1).private
                } else {
                    readKeyPair(arg1).public
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Key loading mode
     */
    enum class MODE {
        // PEM certificate string
        PEM_STRING,  // Decimal-modulus, exponent
        MODULUS_EXPONENT,  // PEM certificate file
        PEM_FILE,  // Key file
        KEY_FILE
    }
}