import java.lang.StringBuilder
import kotlin.experimental.xor
import kotlin.math.pow

fun main(args: Array<String>) {
    val rc4 = Encrypter(16, arrayListOf(3, 4, 24, 17, 33, 53))

    val encrypted = rc4.encrypt("Dunkirk")
    print(String.format("encrypted string: %s\n", encrypted))

    val decrypted = rc4.encrypt(encrypted)
    print(String.format("decrypted string: %s\n", decrypted))
}

class Encrypter(
    blockSz: Int,
    secret: ArrayList<Int>
) {
    private val n = blockSz
    private val sz: Int = (2.toDouble().pow(n)).toInt()
    private val key: ArrayList<Int> = secret
    private lateinit var sBlock: ArrayList<Int>

    init {
        buildSubstitutionBlock(key)
    }

    private fun buildSubstitutionBlock(key: ArrayList<Int>) {
        val l: List<Int> = (1..sz).map { 0 }
        sBlock = ArrayList(l)
        for (i in 0..<sz) sBlock[i] = i
        var j = 0
        for (i in 0..<sz) {
            j = (j + sBlock[i] + key[i % key.size]) % sz

            val t = sBlock[i]
            sBlock[i] = sBlock[j]
            sBlock[j] = t
        }
    }

    private fun psrga(): Int {
//        val k = ByteArray(n / 8 + (if (n % 8 == 0) 0 else 1))
        var i = 0
        var j = 0

        val s = sBlock

        i = (i + 1) % sz
        j = (j + s[i]) % sz

        val t = s[i]
        s[i] = s[j]
        s[j] = t

        val temp = (s[i] + s[j]) % sz
//        k[byte] = s[temp]

        return s[temp]
    }

    private fun encrypt(msg: ArrayList<Int>): ArrayList<Int> {
        val k = psrga()

        val res = ArrayList((1..msg.size).map { 0 })

        for (i in msg.indices) {
            res[i] = msg[i].xor(k)
        }

        return res
    }

    fun encrypt(msg: String): String {
        val array = ArrayList((1..msg.length).map { 0 })
        for (i in msg.indices) array[i] = msg[i].code
        val enc = encrypt(array)
        val res = StringBuilder(array.size)
        for (i in enc.indices) {
            res.append(enc[i].toChar())
        }
        return res.toString()
    }
}
